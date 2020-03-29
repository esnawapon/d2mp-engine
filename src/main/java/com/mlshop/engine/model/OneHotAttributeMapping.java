package com.mlshop.engine.model;

import lombok.Data;

@Data
public class OneHotAttributeMapping {
    private String attributeName;
    private String name;
    public OneHotAttributeMapping() {}
    public OneHotAttributeMapping(String attributeName, String name) {
        this.attributeName = attributeName;
        this.name = name;
    }
}
