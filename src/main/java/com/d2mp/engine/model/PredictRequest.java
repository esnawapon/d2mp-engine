package com.d2mp.engine.model;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PredictRequest {
  @NotNull
  private Integer mode;
  @NotNull
  private Integer hero1;
  @NotNull
  private Integer hero2;
  @NotNull
  private Integer hero3;
  @NotNull
  private Integer hero4;
  @NotNull
  private Integer hero5;
  @NotNull
  private Integer hero6;
  @NotNull
  private Integer hero7;
  @NotNull
  private Integer hero8;
  @NotNull
  private Integer hero9;
  @NotNull
  private Integer hero10;

  private Boolean win;
}