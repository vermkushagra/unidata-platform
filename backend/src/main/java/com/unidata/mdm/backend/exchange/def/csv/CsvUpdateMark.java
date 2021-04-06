package com.unidata.mdm.backend.exchange.def.csv;

import com.unidata.mdm.backend.exchange.def.UpdateMark;

/**
 * @author Mikhail Mikhailov
 * CSV mark update.
 */
public class CsvUpdateMark extends UpdateMark {
    /**
     * Cell address.
     */
    private String cellAddress;
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 244872148278447210L;
    /**
     * @return the cellAddress
     */
    public String getCellAddress() {
        return cellAddress;
    }
    /**
     * @param cellAddress the cellAddress to set
     */
    public void setCellAddress(String cellAddress) {
        this.cellAddress = cellAddress;
    }

}
