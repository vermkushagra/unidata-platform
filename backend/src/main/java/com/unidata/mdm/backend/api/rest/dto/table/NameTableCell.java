package com.unidata.mdm.backend.api.rest.dto.table;

public class NameTableCell extends TableCell {

    public NameTableCell() {
    }

    public NameTableCell(String name) {
        this.name = name;
        this.displayName = name;
    }

    public NameTableCell(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    private String name;

    private String displayName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NameTableCell that = (NameTableCell) o;

        if (!name.equals(that.name))
            return false;
        return displayName.equals(that.displayName);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + displayName.hashCode();
        return result;
    }
}
