package org.unidata.mdm.core.type.data;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Code attribute values.
 */
public interface CodeAttribute<T> extends SingleValueAttribute<T> {
    /**
     * @author Mikhail Mikhailov
     * Denotes type of the contained data.
     */
    enum CodeDataType {
        /**
         * The string type.
         */
        STRING,
        /**
         * The integer type (long 8 bytes).
         */
        INTEGER;
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
     * Gets contained supplementary values.
     * @return values.
     */
    List<T> getSupplementary();
    /**
     * Gets contained supplementary values.
     * @return values.
     */
    @SuppressWarnings("unchecked")
    default<V> List<V> castSupplementary() {
        return (List<V>) getSupplementary();
    }
    /**
     * Sets the supplementary values.
     * @param value to set
     */
    void setSupplementary(List<T> value);
    /**
     * Tells, whether this attribute has supplementary values set.
     * @return true, if so, false otherwise
     */
    default boolean hasSupplementary() {
        return getSupplementary() != null && !getSupplementary().isEmpty();
    }
}
