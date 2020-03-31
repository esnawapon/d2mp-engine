package com.mlshop.engine.model;

import com.mlshop.engine.Constant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AllMonthSummary implements CsvSavedAble {
    private List<MonthSummary> months;

    public AllMonthSummary() {
        this.months = new ArrayList();
    }

    @Override
    public String[] getCsvHeader() {
        return Constant.CSV_HEADER_ALL_MONTHS;
    }

    @Override
    public List<Object[]> getRows() {
        return months.stream().map(e -> new Object[] {e.month, e.totalQuantity, e.totalPrice}).collect(Collectors.toList());
    }

    @Data
    public static class MonthSummary {
        private String month;
        private Double totalQuantity;
        private Double totalPrice;
    }
}
