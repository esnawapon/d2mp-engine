package com.mlshop.engine.model;

import com.mlshop.engine.Constant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MonthlySummary implements CsvSavedAble {
    private List<Item> items;

    public MonthlySummary() {
        items = new ArrayList();
    }

    @Override
    public String[] getCsvHeader() {
        return Constant.CSV_HEADER_MONTHLY;
    }

    @Override
    public List<Object[]> getRows() {
        return items.stream().map(e -> new Object[] {e.itemNameIndex, e.itemName,e.size, e.unitPrice, e.quantity, e.totalPrice}).collect(Collectors.toList());
    }

    public boolean addItem(Integer itemNameIndex, String itemName, String size, Double unitPrice, Double quantity, Double totalPrice) {
        return items.add(new Item(itemNameIndex, itemName, size, unitPrice, quantity, totalPrice));
    }

    @Data
    public static class Item {
        private Integer itemNameIndex;
        private String itemName;
        private String size;
        private Double unitPrice;
        private Double quantity;
        private Double totalPrice;

        public Item(Integer itemNameIndex, String itemName, String size, Double unitPrice, Double quantity, Double totalPrice) {
            this.itemNameIndex = itemNameIndex;
            this.itemName = itemName;
            this.size = size;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }
    }
}
