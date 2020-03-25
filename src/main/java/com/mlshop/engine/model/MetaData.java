package com.mlshop.engine.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MetaData {
    private List<Item> items;

    @Data
    public static class Item {
        private Integer index;
        private String name;
        private Map<String, Double> sizeAndPrice;

        public Item(Integer index, String name, Map<String, Double> sizeAndPrice) {
            this.index = index;
            this.name = name;
            this.sizeAndPrice = sizeAndPrice;
        }

        @Override
        public String toString() {
            return index + ": " + name;
        }
    }
}

