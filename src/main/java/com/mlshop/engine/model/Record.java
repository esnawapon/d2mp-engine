package com.mlshop.engine.model;

import lombok.Data;

@Data
public class Record {
    private String date;
    private String itemName;
    private Double quantity;
    private String size;

    public String getUniqueKey() {
        return itemName + "|" + size;
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
