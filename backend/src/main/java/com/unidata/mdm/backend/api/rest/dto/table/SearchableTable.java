package com.unidata.mdm.backend.api.rest.dto.table;

public class SearchableTable extends Table{

    private String entityName;
    //todo add search type!

    private String rowSearchName;

    private String columnSearchName;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getRowSearchName() {
        return rowSearchName;
    }

    public void setRowSearchName(String rowSearchName) {
        this.rowSearchName = rowSearchName;
    }

    public String getColumnSearchName() {
        return columnSearchName;
    }

    public void setColumnSearchName(String columnSearchName) {
        this.columnSearchName = columnSearchName;
    }
}
