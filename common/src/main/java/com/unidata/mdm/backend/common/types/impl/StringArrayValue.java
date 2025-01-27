package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.types.CodeLinkValue;

/**
 * @author Mikhail Mikhailov
 * String array value.
 */
public class StringArrayValue extends AbstractArrayValue<String> implements CodeLinkValue {

    /**
     * Link etalon id.
     */
    private String linkEtalonId;
    /**
     * Constructor.
     */
    public StringArrayValue() {
        super();
    }
    /**
     * Constructor.
     * @param value
     * @param displayValue
     */
    public StringArrayValue(String value, String displayValue) {
        super(value, displayValue);
    }
    /**
     * Constructor.
     * @param value the value
     * @param displayValue the display value
     * @param linkEtalonId link etalon id
     */
    public StringArrayValue(String value, String displayValue, String linkEtalonId) {
        super(value, displayValue);
        this.linkEtalonId = linkEtalonId;
    }
    /**
     * Constructor.
     * @param value
     */
    public StringArrayValue(String value) {
        super(value);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getLinkEtalonId() {
        return linkEtalonId;
    }
    /**
     * @param linkEtalonId the linkEtalonId to set
     */
    @Override
    public void setLinkEtalonId(String linkEtalonId) {
        this.linkEtalonId = linkEtalonId;
    }

}
