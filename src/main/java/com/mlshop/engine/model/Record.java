package com.mlshop.engine.model;

import lombok.Data;

@Data
public class Record {
    private String date;
    private String itemName;
    private String size;
    private Double quantity;

    public Record() {}
    public Record(String date, String itemName, String size, Double quantity) {
        this.date = date;
        this.itemName = itemName;
        this.size = size;
        this.quantity = quantity;
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
