package com.mlshop.engine.model;

import lombok.Data;

@Data
public class RangeConfig {
    private Integer index;
    private Double eq;
    private Double gte;
    private Double lte;
}
