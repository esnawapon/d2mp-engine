package com.mlshop.engine.model;

import java.util.List;

public interface CsvSavedAble  {
    String[] getCsvHeader();
    List<Object[]> getRows();
}
