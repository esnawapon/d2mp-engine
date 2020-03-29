package com.mlshop.engine.model;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PredictionResult {
    private Map<String, DateGroupPrediction> dates;
    private Integer totalQuantity;
    private Integer totalQuantityNoPrice;
    private Double totalPrice;

    public PredictionResult() {
        dates = new HashMap<>();
    }

    public PredictionResult addRecord(Record record) {
        DateGroupPrediction dateGroup = dates.get(record.getDate());
        if (dateGroup == null) {
            dateGroup = new DateGroupPrediction();
            dates.put(record.getDate(), dateGroup);
        }
        ItemGroupPrediction itemGroup = dateGroup.items.get(record.getItemName());
        if (itemGroup == null) {
            itemGroup = new ItemGroupPrediction();
            dateGroup.items.put(record.getItemName(), itemGroup);
        }
        SizeGroupPrediction sizeGroup = new SizeGroupPrediction();
        sizeGroup.totalQuantity = record.getQuantity().intValue();
        sizeGroup.unitPrice = record.getUnitPrice();
        itemGroup.sizes.put(record.getSize(), sizeGroup);
        return this;
    }

    public PredictionResult compute() {
        dates.forEach((key, value) -> value.compute());
        totalQuantity = dates.entrySet().stream()
                .mapToInt(e -> e.getValue().totalQuantity)
                .sum();
        totalQuantityNoPrice = dates.entrySet().stream()
                .mapToInt(e -> e.getValue().totalQuantityNoPrice)
                .sum();
        totalPrice = dates.entrySet().stream()
                .filter(e -> e.getValue().totalPrice != null)
                .mapToDouble(e -> e.getValue().totalPrice)
                .sum();
        return this;
    }

    @Data
    private static class DateGroupPrediction {
        private Map<String, ItemGroupPrediction> items;
        private Integer totalQuantity;
        private Integer totalQuantityNoPrice;
        private Double totalPrice;
        public DateGroupPrediction() {
            items = new HashMap<>();
        }

        public void compute() {
            items.forEach((key, value) -> value.compute());
            totalQuantity = items.entrySet().stream()
                    .mapToInt(e -> e.getValue().totalQuantity)
                    .sum();
            totalQuantityNoPrice = items.entrySet().stream()
                    .mapToInt(e -> e.getValue().totalQuantityNoPrice)
                    .sum();
            totalPrice = items.entrySet().stream()
                    .filter(e -> e.getValue().totalPrice != null)
                    .mapToDouble(e -> e.getValue().totalPrice)
                    .sum();
        }
    }

    @Data
    private static class ItemGroupPrediction {
        private Map<String, SizeGroupPrediction> sizes;
        private Integer totalQuantity;
        private Integer totalQuantityNoPrice;
        private Double totalPrice;
        public ItemGroupPrediction() {
            sizes = new HashMap<>();
        }

        public void compute() {
            sizes.forEach((k, v) -> v.compute());
            totalQuantity = sizes.entrySet().stream()
                .mapToInt(e -> e.getValue().totalQuantity)
                .sum();
            totalQuantityNoPrice = sizes.entrySet().stream()
                .filter(e -> e.getValue().unitPrice == null)
                .mapToInt(e -> e.getValue().totalQuantity)
                .sum();
            totalPrice = sizes.entrySet().stream()
                .filter(e -> e.getValue().totalPrice != null)
                .mapToDouble(e -> e.getValue().totalPrice)
                .sum();
        }
    }

    @Data
    private static class SizeGroupPrediction {
        private Double unitPrice;
        private Integer totalQuantity;
        private Double totalPrice;

        public void compute() {
            totalPrice = unitPrice == null ? null : unitPrice * totalQuantity;
        }
    }
}
