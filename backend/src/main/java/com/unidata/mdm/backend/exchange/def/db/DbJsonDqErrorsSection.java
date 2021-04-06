package com.unidata.mdm.backend.exchange.def.db;

import com.unidata.mdm.backend.exchange.def.DqErrorsSection;

public class DbJsonDqErrorsSection extends DqErrorsSection {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6121640813210053268L;

    /**
     * table
     */
    private String table;
    /**
     * Source system column.
     */
    private String jsonColumn;


    public String getJsonColumn() {
        return jsonColumn;
    }

    public void setJsonColumn(String jsonColumn) {
        this.jsonColumn = jsonColumn;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
