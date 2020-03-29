package com.mlshop.engine.service;

import com.mlshop.engine.Constant;
import com.mlshop.engine.Constant.ColumnIndex;
import com.mlshop.engine.model.Record;
import com.mlshop.engine.util.ArffUtils;
import com.mlshop.engine.util.DataUtils;
import com.mlshop.engine.util.ExcelUtils;
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
    MetaDataService metaDataService;
    @Autowired
    ItemNameKeyService itemNameKeyService;
    @Autowired
    QuantityRangeService quantityRangeService;

    public String transformAll() throws IOException {
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
                "201912.xls",
        };
        List<Record> records = new ArrayList();
        for (String fileName: fileNames) {
            records.addAll(transformExcelToRecords(fileName));
        }

        Map<String, List<Record>> groupped = records.stream().collect(Collectors.groupingBy(e -> e.getDate()));
        groupped.entrySet().stream().forEach(e -> {
            System.out.println("===================");
            System.out.println(e.getKey());
            Double max = e.getValue().stream().mapToDouble(ea -> ea.getQuantity()).max().getAsDouble();
            Double min = e.getValue().stream().mapToDouble(ea -> ea.getQuantity()).min().getAsDouble();
            System.out.println("max:" + max);
            System.out.println("min:" + min);
        });

        itemNameKeyService.updateItemNameKeys(records);
        metaDataService.updateMetaData(records);
        quantityRangeService.classifyRange(records);

        // write file
        String header = genArffHeader();
        File train = writeTrainArff(header, records);
        writePredictArff(header);
        return train.getName();
    }


    private List<Record> transformExcelToRecords(String excelFileName) throws IOException {
        // transform to List<ExcelRecord>
        List<Record> records = StreamUtils.asStream(loadRowIterator(excelFileName), false)
                .map(row -> transFormRowToRecord(row))
                .filter(each -> each.getQuantity() != null)
                .collect(Collectors.toList());

        // fill missing timestamp (if a order has many items data will store 1 item per 1 row but order id name timestamp will store only first item)
        records.stream().reduce((a, b) -> {
            if (b.getDate() == null) {
                b.setDate(a.getDate());
            }
            return b;
        });

        // group same item in same day into one record
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
        return groupedRecords;
    }

    private Iterator<Row> loadRowIterator(String filePath) throws IOException {
        File file = new ClassPathResource(filePath).getFile();
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> itr = sheet.rowIterator();
        itr.next();  // first record is column names
        return itr;
    }

    private static Record transFormRowToRecord(Row row) {
        Record record = new Record();
        String timestamp = ExcelUtils.getStringFromCell(row.getCell(ColumnIndex.TIMESTAMP));
        if (timestamp != null) {
            record.setDate(timestamp.substring(0, 7));
        }
        record.setItemName(ExcelUtils.getStringFromCell(row.getCell(ColumnIndex.ITEM_NAME)).toLowerCase());
        record.setQuantity(ExcelUtils.getDoubleFromCell(row.getCell(ColumnIndex.QUANTITY)));
        record.setUnitPrice(ExcelUtils.getDoubleFromCell(row.getCell(ColumnIndex.SELL_PRICE)));
        String option = ExcelUtils.getStringFromCell(row.getCell(ColumnIndex.OPTION)).toLowerCase();
        if (option != null) {
            String[] options = option.split(",");
            for (String opt: options) {
                if (DataUtils.isSize(opt)) {
                    record.setSize(opt);
                } else {
                    record.setItemName(record.getItemName() + " " + opt);
                }
            }
        }
        return record;
    }
    private String genArffHeader() throws IOException {
        try (FileReader reader = new FileReader(Constant.FILE_NAME_TEMPLATE)) {
            StringBuffer stringBuffer = new StringBuffer();
            int numCharsRead;
            char[] charArray = new char[1024];
            while ((numCharsRead = reader.read(charArray)) > 0) {
                stringBuffer.append(charArray, 0, numCharsRead);
            }
            String header = stringBuffer.toString();
            header = header.replace("?1", ArffUtils.genNominalTypeFromCollection(Arrays.asList(Constant.SIZES)));
            header = header.replace("?2", ArffUtils.genOneHotAttributesHeader(itemNameKeyService.getAttributeNames()));
//            header = header.replace("?3", ArffUtils.genNominalTypeFromCollection(quantityRangeService.getIndexes()));
            return header;
        } catch (IOException e) {
            System.out.println("Cannot read template");
            throw e;
        }
    }

    private File writeTrainArff(String header, List<Record> records) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String fileName = "/temp-" + sdf.format(Calendar.getInstance().getTime()) + ".arff";
        String filePath = Constant.DIR_NAME_ARFF + fileName;

        String content = header + ArffUtils.toArffRecords(records, itemNameKeyService.getAttributeNames());
        File result = writeReplaceFile(filePath, content);
        return result;
    }

    private File writeReplaceFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Cannot write " + filePath);
            throw e;
        }
        return file;
    }

    public File writePredictArff(String header) throws IOException {
        int itemLength = metaDataService.itemLength();
        List<Record> allCombinations = DataUtils.genDateFromNow(Constant.MONTH_PREDICTION_LENGTH + 1)
            .stream()
            .map(date -> IntStream.range(0, itemLength)
                .mapToObj(item -> Arrays.stream(Constant.SIZES)
                    .map(size -> new Record(date, String.valueOf(item), size, null))
                    .collect(Collectors.toList())
                ).flatMap(List::stream)
                .collect(Collectors.toList())
            ).flatMap(List::stream)
            .collect(Collectors.toList());
//        String content = header + ArffUtils.toArffRecords(allCombinations);
        String content = header;
        File result = writeReplaceFile(Constant.FILE_NAME_MAIN_PREDICT, content);
        return result;
    }
}
