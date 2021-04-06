package com.unidata.mdm.backend.common.types;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Code attribute values.
 */
public interface CodeAttribute<T> extends Attribute {
    /**
     * @author Mikhail Mikhailov
     * Denotes type of the contained data.
     */
    public enum CodeDataType {
        /**
         * The string type.
         */
        STRING,
        /**
         * The integer type (long 8 bytes).
         */
        INTEGER
    }
    /**
     * {@inheritDoc}
     */
    @Override
    default AttributeType getAttributeType() {
        return AttributeType.CODE;
    }
    /**
     * Gets type of contained data.
     * @return type
     */
    CodeDataType getDataType();
    /**
     * Gets contained value.
     * @return value.
     */
    T getValue();
    /**
     * Sets the value.
     * @param value to set
     */
    void setValue(T value);
    /**
     * Gets contained supplementary values.
     * @return values.
     */
    List<T> getSupplementary();
    /**
     * Sets the supplementary values.
     * @param values to set
     */
    void setSupplementary(List<T> value);
    /**
     * Should not be used without reason.
     * @return
     */
    @SuppressWarnings("unchecked")
    default<V> V castValue() {
        return (V) getValue();
    }
    /**
     * Tries to cast the supplied object value to intenal type and sets it.
     * @param o the object.
     */
    @SuppressWarnings("unchecked")
    default void castValue(Object o) {
        setValue((T) o);
    }
    /**
     * Tells, whether this attribute has supplementary values set.
     * @return true, if so, false otherwise
     */
    default boolean hasSupplementary() {
        return getSupplementary() != null && !getSupplementary().isEmpty();
    }
}
