package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Large value type.
 */
public interface LargeValue {
    /**
     * @author Mikhail Mikhailov
     * Large value type.
     */
    public enum ValueType {
        /**
         * Binary large value.
         */
        BLOB,
        /**
         * Charcter large value.
         */
        CLOB
    }
    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    String getId();
    /**
     * Gets the possibly supplied file name.
     * @return name
     */
    String getFileName();
    /**
     * Gets the possibly supplied MIME type.
     * @return MIME type
     */
    String getMimeType();
    /**
     * Gets the size in bytes.
     * @return size in bytes
     */
    long getSize();
    /**
     * Gets the value type.
     * @return type
     */
    ValueType getValueType();
}
