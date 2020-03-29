package com.mlshop.engine.model;

import lombok.Data;

import java.util.List;

@Data
public class ItemNameKey {
    private List<OneHotAttributeMapping> keys;

    public ItemNameKey(List<OneHotAttributeMapping> keys) {
        this.keys = keys;
    }
}
