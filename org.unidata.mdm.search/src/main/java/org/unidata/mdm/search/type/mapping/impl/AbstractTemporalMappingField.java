package org.unidata.mdm.search.type.mapping.impl;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 */
public abstract class AbstractTemporalMappingField<X extends AbstractTemporalMappingField<X>> extends AbstractValueMappingField<X> {
    /**
     * Accepted format.
     */
    private String format;
    /**
     * Constructor.
     * @param name
     */
    public AbstractTemporalMappingField(String name) {
        super(name);
    }
    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }
    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public X withFormat(String format) {
        setFormat(format);
        return self();
    }
}
