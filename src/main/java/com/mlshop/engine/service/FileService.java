package com.mlshop.engine.service;

import com.mlshop.engine.Constant;
import com.mlshop.engine.model.Record;
import com.mlshop.engine.util.ColumnIndex;
import com.mlshop.engine.util.StreamUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FileService {
    @Autowired
    NameMappingService nameMappingService;
    private Iterator<Row> loadRowIterator(String filePath) throws IOException {
        File file = new ClassPathResource(filePath).getFile();
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> itr = sheet.rowIterator();
        itr.next();  // first record is column names
        return  itr;
    }

    public Map transformAll() throws IOException {
        String[] fileNames = new String[] {
                "201901.xls",
                "201902.xls",
                "201903.xls",
                "201904.xls",
                "201905.xls",
                "201906.xls",
                "201907.xls",
                "201908.xls",
                "201909.xls",
                "201910.xls",
                "201911.xls",
                "201912.xls"
        };
        List<Record> records = new ArrayList();
        for (String fileName: fileNames) {
            records.addAll(transformExcelToRecords(fileName));
        }
        File file = write(records);
        Map result = new HashMap();
        result.put("status", "success");
        result.put("fileName", file.getAbsolutePath());
        return result;
    }


    public List<Record> transformExcelToRecords(String excelFileName) throws IOException {
        // transform to List<ExcelRecord>
        List<Record> records = StreamUtils.asStream(loadRowIterator(excelFileName), false)
                .map(row -> fromRowToExcelRecord(row))
                .filter(each -> each.getQuantity() != null)
                .collect(Collectors.toList());

        // fill missing timestamp (if a order has many items data will store 1 item per 1 row but order id name timestamp will store only first item)
        records.stream().reduce((a, b) -> {
            if (b.getDate() == null) {
                b.setDate(a.getDate());
            }
            return b;
        });

        // group same item into one record
        Map<String, Record> grouped = new HashMap();
        for (Record record: records) {
            Record existRecord = grouped.get(record.getUniqueKey());
            if (existRecord == null) {
                grouped.put(record.getUniqueKey(), record);
            } else {
                existRecord.merge(record);
            }
        }
        List<Record> groupedRecords = grouped.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());

        // replace name with index
        final Map<String, Integer> nameMappings = nameMappingService.mapItemNameToResource(
            groupedRecords.stream()
            .map(e -> e.getItemName())
            .collect(Collectors.toSet())
        );
        groupedRecords.forEach(e -> e.setItemName(nameMappings.get(e.getItemName()).toString()));
        return groupedRecords;
    }

    private static Record fromRowToExcelRecord(Row row) {
        Record record = new Record();
        String timestamp = getStringFromCell(row.getCell(ColumnIndex.TIMESTAMP));
        if (timestamp != null) {
            record.setDate(timestamp.substring(0, timestamp.indexOf(" ")));
        }
        record.setItemName(getStringFromCell(row.getCell(ColumnIndex.ITEM_NAME)).toLowerCase());
        record.setQuantity(getDoubleFromCell(row.getCell(ColumnIndex.QUANTITY)));
        String option = getStringFromCell(row.getCell(ColumnIndex.OPTION)).toLowerCase();
        if (option != null) {
            String[] options = option.split(",");
            for (String opt: options) {
                if (isSize(opt)) {
                    record.setSize(opt);
                } else {
                    record.setItemName(record.getItemName() + " " + opt);
                }
            }
        }
        return record;
    }

    private static boolean isSize(String size) {
        if (size.equals("m")) return true;
        if (size.equals("l")) return true;
        if (size.indexOf("s") == size.length() - 1 && size.length() <= 2) return true;
        if (size.indexOf("xl") == size.length() - 2 && size.length() <= 3) return true;
        return false;
    }

    private static String getStringFromCell(Cell cell) {
        switch (cell.getCellType()) {
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case STRING: return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case FORMULA: return cell.getCellFormula();
            default: return null;
        }
    }

    private static Double getDoubleFromCell(Cell cell) {
        try {
            String value = getStringFromCell(cell);
            return Double.parseDouble(value);
        } catch (Exception e) {
            System.out.println("EXCEPTION on parsing cell[" + cell.getAddress().formatAsString() + "] to DOUBLE");
            return null;
        }
    }

    private static String toArffRecords(List<Record> records) {
        return records.stream().map(record -> toArffRecord(record)).collect(Collectors.joining("\n"));
    }
    private static String toArffRecord(Record record) {
        StringBuilder sb = new StringBuilder();
        sb.append(record.getDate()).append(",")
            .append(record.getItemName()).append(",")
            .append(record.getSize() == null ? "" : record.getSize()).append(",")
            .append(record.getQuantity());
        return sb.toString();
    }

    private String getItemNameNominalType() throws IOException {
        int size = nameMappingService.itemNameSize();
        return "{" + IntStream.range(0, size - 1).mapToObj(e -> String.valueOf(e)).collect(Collectors.joining(",")) + "}";
    }

    public File write(List<Record> records) throws IOException {
        String templateStr = "";
        try (FileReader reader = new FileReader(Constant.FILE_NAME_TEMPLATE)) {
            StringBuffer stringBuffer = new StringBuffer();
            int numCharsRead;
            char[] charArray = new char[1024];
            while ((numCharsRead = reader.read(charArray)) > 0) {
                stringBuffer.append(charArray, 0, numCharsRead);
            }
            templateStr = stringBuffer.toString();
        } catch (IOException e) {
            System.out.println("Cannot read template");
            throw e;
        }
        Set<String> itemNames = records.stream().map(each -> each.getItemName()).collect(Collectors.toSet());
        templateStr = templateStr.replace("?1", getItemNameNominalType());
        templateStr = templateStr.replace("?3", toArffRecords(records));
        File arffTempFile = new File(Constant.DIR_NAME_ARFF + genTempFileName());
        if (!arffTempFile.exists()) {
            arffTempFile.createNewFile();
        }
        try (FileWriter writer = new FileWriter(arffTempFile, false)) {
            writer.write(templateStr);
        } catch (IOException e) {
            System.out.println("Cannot write template");
            throw e;
        }
        return arffTempFile;
    }

    public String genTempFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return "/temp-" + sdf.format(Calendar.getInstance().getTime()) + ".arff";
    }
}
