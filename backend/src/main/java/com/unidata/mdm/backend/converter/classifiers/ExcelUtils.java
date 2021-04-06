package com.unidata.mdm.backend.converter.classifiers;

import org.apache.poi.ss.usermodel.Cell;

public final class ExcelUtils {
    private ExcelUtils() {}

    public static String extractCellValue(final Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellTypeEnum()){
            case NUMERIC:
                return String.valueOf(Double.valueOf(cell.getNumericCellValue()).intValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BLANK:
                return "";
            case ERROR:
                return "";
            default:
                return "";
        }
    }
}
