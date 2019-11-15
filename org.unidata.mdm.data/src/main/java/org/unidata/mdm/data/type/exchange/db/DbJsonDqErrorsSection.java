package org.unidata.mdm.data.type.exchange.db;

import org.unidata.mdm.data.type.exchange.DqErrorsSection;

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
