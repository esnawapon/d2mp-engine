package com.mlshop.engine.model;

import lombok.Data;

import java.util.Set;

@Data
public class Record {
    private String date;
    private String itemName;
    private Integer itemNameIndex;
    private String size;
    private Double quantity;
    private Double unitPrice;

    private Set<String> activeKeyAttributes;
    private Integer quantityRange;

    public Record() {}
    public Record(String date, Integer itemNameIndex, String itemName, String size, Double quantity, Double unitPrice) {
        this.date = date;
        this.itemNameIndex = itemNameIndex;
        this.itemName = itemName;
        this.size = size;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getUniqueKey() {
        return date + "|" + itemName + "|" + size;
    }

    public void merge(Record record) {
        quantity += record.getQuantity();
    }

    @Override
    public Object clone() {
        Record obj = new Record();
        obj.date = this.date;
        obj.itemName = this.itemName;
        obj.quantity = this.quantity;
        obj.size = this.size;
        return obj;
    }
}
