package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Simple attribute.
 */
public interface SimpleAttribute<T> extends Attribute, DisplayValue {
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
     * Gets contained value.
     * @return value.
     */
    T getValue();
    /**
     *
     * @return value for type
     */
    <V> V narrow(NarrowType type);
    /**
     * Should not be used without reason.
     * @return
     */
    @SuppressWarnings("unchecked")
    default<V> V castValue() {
        return (V) getValue();
    }
    /**
     * Sets the value.
     * @param value to set
     */
    void setValue(T value);
    /**
     * Tries to cast the supplied object value to intenal type and sets it.
     * @param o the object.
     */
    @SuppressWarnings("unchecked")
    default void castValue(Object o) {
        setValue((T) o);
    }
}
