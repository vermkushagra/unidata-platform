package com.unidata.mdm.backend.api.rest.dto.table;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class Table {

    private String rowName;

    private String columnName;

    private String rowDisplayName;

    private String columnDisplayName;

    private Set<NameTableCell> rows = new LinkedHashSet<>();

    private Set<NameTableCell> columns = new LinkedHashSet<>();

    private Set<AddressedTableCell> cells = new LinkedHashSet<>();

    public void addRow(NameTableCell row){
        rows.add(row);
    }

    public void addRows(Collection<NameTableCell> rows) {
        this.rows.addAll(rows);
    }

    public void addColumn(NameTableCell column){
        columns.add(column);
    }

    public void addColumns(Collection<NameTableCell> columns) {
        this.columns.addAll(columns);
    }

    public void addCell(AddressedTableCell adressedTableCell){
        cells.add(adressedTableCell);
    }

    public Set<NameTableCell> getRows() {
        return rows;
    }

    public void setRows(Set<NameTableCell> rows) {
        this.rows = rows;
    }

    public Set<NameTableCell> getColumns() {
        return columns;
    }

    public void setColumns(Set<NameTableCell> columns) {
        this.columns = columns;
    }

    public Set<AddressedTableCell> getCells() {
        return cells;
    }

    public void setCells(Set<AddressedTableCell> cells) {
        this.cells = cells;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getRowName() {
        return rowName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getRowDisplayName() {
        return rowDisplayName;
    }

    public void setRowDisplayName(String rowDisplayName) {
        this.rowDisplayName = rowDisplayName;
    }

    public String getColumnDisplayName() {
        return columnDisplayName;
    }

    public void setColumnDisplayName(String columnDisplayName) {
        this.columnDisplayName = columnDisplayName;
    }
}
