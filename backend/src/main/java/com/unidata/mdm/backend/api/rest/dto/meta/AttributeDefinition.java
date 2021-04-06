package com.unidata.mdm.backend.api.rest.dto.meta;

import java.io.Serializable;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;

public abstract class AttributeDefinition extends AbstractAttributeDefinition implements Serializable {

    private static final long serialVersionUID = -5284413340344918080L;
    /**
     * Can be null
     */
    private boolean nullable = true;

    /**
     * Should be unique
     */
    private boolean unique = false;

    /**
     * Attribute is generally searchable.
     */
    private boolean searchable = false;
    /**
     * data type.
     */
    private SimpleDataType simpleDataType;

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public SimpleDataType getSimpleDataType() {
        return simpleDataType;
    }

    public void setSimpleDataType(SimpleDataType simpleDataType) {
        this.simpleDataType = simpleDataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeDefinition)) return false;
        if (!super.equals(o)) return false;

        AttributeDefinition that = (AttributeDefinition) o;

        return simpleDataType == that.simpleDataType;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (simpleDataType != null ? simpleDataType.hashCode() : 0);
        return result;
    }
}
