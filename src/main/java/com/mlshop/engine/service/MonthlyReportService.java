package com.mlshop.engine.service;

import com.mlshop.engine.Constant;
import com.mlshop.engine.model.AllMonthSummary;
import com.mlshop.engine.model.MonthlySummary;
import com.mlshop.engine.model.Record;
import com.mlshop.engine.util.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MonthlyReportService {
    private AllMonthSummary allMonthSummary;

    public void reloadResource() throws IOException {
        File file = new File(Constant.FILE_NAME_ALL_MONTHS_SUMMARY_JSON);
        if (file.exists()) {
            allMonthSummary = FileUtils.readJsonFile(Constant.FILE_NAME_META_DATA, AllMonthSummary.class);
        } else {
            allMonthSummary = new AllMonthSummary();
        }
    }

    public AllMonthSummary getAllMonthSummary() throws IOException {
        if (allMonthSummary == null) {
            reloadResource();
        }
        return allMonthSummary;
    }

    public void saveAll(List<Record> records) throws IOException {
        for (Map.Entry<String, List<Record>> group: records.stream().collect(Collectors.groupingBy(e -> e.getDate())).entrySet()) {
            writeMonthlyReport(group.getKey(), group.getValue());
            saveAllSummaryByMonth(group.getKey(), group.getValue());
        }
        writeResource();
    }

    public synchronized File writeMonthlyReport(String month, List<Record> records) throws IOException {
        String filePath = Constant.DIR_NAME_MONTHLY_REPORT + "/" + month;
        String filePathJson = filePath + ".json";
        String filePathCsv = filePath + ".csv";
        MonthlySummary monthlySummary = new MonthlySummary();
        for (Record record: records) {
            monthlySummary.addItem(
                record.getItemNameIndex(),
                record.getItemName(),
                record.getSize(),
                record.getUnitPrice(),
                record.getQuantity(),
                record.getUnitPrice() * record.getQuantity()
            );
        }
        FileUtils.writeReplaceJsonFile(filePathJson, monthlySummary);
        return FileUtils.writeReplaceCsvFile(filePathCsv, monthlySummary);
    }

    public void saveAllSummaryByMonth(String month, List<Record> records) throws IOException {
        // update all summary
        AllMonthSummary.MonthSummary summary = getAllMonthSummary().getMonths().stream()
                .filter(e -> e.getMonth().equals(month))
                .findFirst().orElse(null);
        if (summary == null) {
            summary = new AllMonthSummary.MonthSummary();
            summary.setMonth(month);
            getAllMonthSummary().getMonths().add(summary);
        }
        summary.setTotalPrice(records.stream().mapToDouble(e -> e.getUnitPrice() * e.getQuantity()).sum());
        summary.setTotalQuantity(records.stream().mapToDouble(e -> e.getQuantity()).sum());
    }

    public void writeResource() throws IOException {
        FileUtils.writeReplaceJsonFile(Constant.FILE_NAME_ALL_MONTHS_SUMMARY_JSON, allMonthSummary);
        FileUtils.writeReplaceCsvFile(Constant.FILE_NAME_ALL_MONTHS_SUMMARY_CSV, allMonthSummary);

        reloadResource();
    }
}
