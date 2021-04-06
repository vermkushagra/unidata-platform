package com.unidata.mdm.backend.po;

/**
 * Import error with sql request and index in this sql.
 */
public class ImportErrorPO extends ErrorPO{

    private int index;

    private String sql;

    public ImportErrorPO(String error, String description, String operationId, int index, String sql) {
        super(error, description, operationId);
        this.index = index;
        this.sql = sql;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

}
