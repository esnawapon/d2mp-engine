package com.mlshop.engine.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class ExcelUtils {
    public static Double getDoubleFromCell(Cell cell) {
        try {
            String value = ExcelUtils.getStringFromCell(cell);
            return Double.parseDouble(value);
        } catch (Exception e) {
            System.out.println("EXCEPTION on parsing cell[" + cell.getAddress().formatAsString() + "] to DOUBLE");
            return null;
        }
    }

    public static String getStringFromCell(Cell cell) {
        switch (cell.getCellType()) {
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case STRING: return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case FORMULA: return cell.getCellFormula();
            default: return null;
        }
    }
}
