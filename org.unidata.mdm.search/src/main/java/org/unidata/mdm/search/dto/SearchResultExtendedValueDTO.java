package org.unidata.mdm.search.dto;

/**
 * Extended value for search result.
 * Can contains special name for display and other additional information.
 */
public class SearchResultExtendedValueDTO {
    /**
     * Original value
     */
    private Object value;
    /**
     * Value for display
     */
    private String displayValue;
    /**
     * Linked etalon id
     */
    private String linkedEtalonId;

    public SearchResultExtendedValueDTO(Object value, String displayValue, String linkedEtalonId) {
        this.value = value;
        this.displayValue = displayValue;
        this.linkedEtalonId = linkedEtalonId;
    }

    public SearchResultExtendedValueDTO(Object value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
        this.linkedEtalonId = null;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getLinkedEtalonId() {
        return linkedEtalonId;
    }

    public void setLinkedEtalonId(String linkedEtalonId) {
        this.linkedEtalonId = linkedEtalonId;
    }
}