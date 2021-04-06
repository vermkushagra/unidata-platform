package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Concat strategy mapped type descriptor.
 */
public class ConcatExternalIdGenerationStrategyRO extends ExternalIdGenerationStrategyRO {
    /**
     * Attribute names.
     */
    private List<String> attributes;
    /**
     * Separator char.
     */
    private String separator;
    /**
     * Constructor.
     */
    public ConcatExternalIdGenerationStrategyRO() {
        super();
    }
    /**
     * @return the sttributes
     */
    public List<String> getAttributes() {
        return attributes;
    }
    /**
     * @param sttributes the sttributes to set
     */
    public void setAttributes(List<String> sttributes) {
        this.attributes = sttributes;
    }
    /**
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }
    /**
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalIdGenerationTypeRO getStrategyType() {
        return ExternalIdGenerationTypeRO.CONCAT;
    }
}
