package com.mlshop.engine.model;

import com.mlshop.engine.Constant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PredictionSummary implements CsvSavedAble {
    private List<Item> items;

    public PredictionSummary() {
        this.items = new ArrayList();
    }

    @Override
    public String[] getCsvHeader() {
        return Constant.CSV_HEADER_PREDICT;
    }

    @Override
    public List<Object[]> getRows() {
        return items.stream().map(e -> new Object[] {e.month, e.itemIndex, e.itemName, e.size, e.unitPrice, e.predictedQuantity, e.totalPrice}).collect(Collectors.toList());
    }

    public boolean addItem(String month, Integer itemIndex, String itemName, String size, Double unitPrice, Double predictedQuantity, Double totalPrice) {
        return items.add(new Item(month, itemIndex, itemName, size, unitPrice, predictedQuantity, totalPrice));
    }
    @Data
    public static class Item {
        private String month;
        private Integer itemIndex;
        private String itemName;
        private String size;
        private Double unitPrice;
        private Double predictedQuantity;
        private Double totalPrice;

        public Item(String month, Integer itemIndex, String itemName, String size, Double unitPrice, Double predictedQuantity, Double totalPrice) {
            this.month = month;
            this.itemIndex = itemIndex;
            this.itemName = itemName;
            this.size = size;
            this.unitPrice = unitPrice;
            this.predictedQuantity = predictedQuantity;
            this.totalPrice = totalPrice;
        }
    }
}
