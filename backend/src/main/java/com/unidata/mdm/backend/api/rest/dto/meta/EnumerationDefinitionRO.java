package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class EnumerationDefinition.
 *
 * @author ilya.bykov
 */
public class EnumerationDefinitionRO {

    /** The name. */
    @JsonProperty(index = 1, value = "name")
    private String name;
    /** The description. */
    @JsonProperty(index = 2, value = "displayName")
    private String displayName;

    /** The value. */
    private List<EnumerationValueRO> values;

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public List<EnumerationValueRO> getValues() {

        if (this.values == null) {
            this.values = new ArrayList<>();
        }

        return values;
    }

    /**
     * Sets the value.
     *
     * @param values
     *            the value to set
     */
    public void setValues(List<EnumerationValueRO> values) {

        this.values = values;
    }

    /**
     * Adds the value.
     *
     * @param enumerationValue
     *            the enumeration value
     */
    public void addValue(EnumerationValueRO enumerationValue) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        values.add(enumerationValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EnumerationDefinitionRO other = (EnumerationDefinitionRO) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EnumerationDefinitionRO [name=");
        builder.append(name);
        builder.append(", value=");
        builder.append(values);
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
