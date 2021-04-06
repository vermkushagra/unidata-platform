package com.unidata.mdm.backend.api.rest.dto.table;

public class AddressedTableCell extends TableCell {

    private String value;

    private String displayValue;

    private String row;

    private String column;

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AddressedTableCell that = (AddressedTableCell) o;

        if (!value.equals(that.value))
            return false;
        if (!displayValue.equals(that.displayValue))
            return false;
        if (!row.equals(that.row))
            return false;
        return column.equals(that.column);

    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + displayValue.hashCode();
        result = 31 * result + row.hashCode();
        result = 31 * result + column.hashCode();
        return result;
    }
}
