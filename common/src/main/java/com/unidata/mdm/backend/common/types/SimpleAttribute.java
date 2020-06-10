package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Simple attribute.
 */
public interface SimpleAttribute<T> extends SingleValueAttribute<T>, DisplayValue {
    /**
     * @author Mikhail Mikhailov
     * Denotes type of the contained data.
     */
    public enum DataType {
        /**
         * The string type.
         */
        STRING,
        /**
         * The integer type (long 8 bytes).
         */
        INTEGER,
        /**
         * The floating point type (double 8 bytes).
         */
        NUMBER,
        /**
         * The boolean type.
         */
        BOOLEAN,
        /**
         * Binary large object.
         */
        BLOB,
        /**
         * Character large object.
         */
        CLOB,
        /**
         * The date type.
         */
        DATE,
        /**
         * The time type.
         */
        TIME,
        /**
         * The timestamp type.
         */
        TIMESTAMP,
        /**
         * Link to a enum value.
         */
        ENUM,
        /**
         * Special href template, processed by get post-processor, type.
         */
        LINK,
        /**
         * Special type of number.
         */
        MEASURED;
    }
    enum NarrowType {
        /**
         * Value for ES
         */
        ES,
        /**
         * return the same value as {#link getValue}
         */
        DEFAULT
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default AttributeType getAttributeType() {
        return AttributeType.SIMPLE;
    }
    /**
     * Gets type of contained data.
     * @return type
     */
    DataType getDataType();
    /**
     *
     * @return value for type
     */
    <V> V narrow(NarrowType type);
}
