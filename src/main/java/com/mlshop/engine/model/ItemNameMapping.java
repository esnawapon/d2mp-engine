package com.mlshop.engine.model;

import lombok.Data;

import java.util.List;

@Data
public class ItemNameMapping {
    private List<String> itemNames;

    public ItemNameMapping(List<String> itemNames) {
        this.itemNames = itemNames;
    }
}
