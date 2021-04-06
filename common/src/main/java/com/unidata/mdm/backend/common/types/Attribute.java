package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Basic attribute properties.
 */
public interface Attribute {
    /**
     * @author Mikhail Mikhailov
     * Denotes type of an attribute.
     */
    public enum AttributeType {
        /**
         * Simple attribute. Can contain single value.
         * Can not contain other attributes.
         */
        SIMPLE,
        /**
         * Array attribute. Can contain multiple values of the same type.
         * Can not contain other attributes.
         */
        ARRAY,
        /**
         * Complex attribute. Cannot contain simple values.
         * Contains other nested records and thus attributes.
         */
        COMPLEX,
        /**
         * Code attribute. Key attribute for lookup entity records.
         * Can not contain other attributes, but can contain supplementary values.
         */
        CODE
    }

    /**
     * Gets type of this attribute.
     * @return type
     */
    AttributeType getAttributeType();
    /**
     * Gets name of this attribute.
     * @return name
     */
    String getName();
    /**
     * Gets the parent record point, the simple attribute is currently associated with.
     * @return parent link or null
     */
    DataRecord getRecord();
    /**
     * Sets the current association with a record.
     * @param record the record holding the attribute
     */
    void setRecord(DataRecord record);
    /**
     * Saves a couple of ugly casts.
     * @return self as a cast type
     */
    @SuppressWarnings("unchecked")
    default <T extends Attribute> T narrow() {
        return (T) this;
    }
}
