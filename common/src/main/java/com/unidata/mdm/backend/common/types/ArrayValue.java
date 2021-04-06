package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Array value.
 */
public interface ArrayValue<T> extends DisplayValue {

    /**
     * Gets the value.
     * @return the inner value.
     */
    T getValue();
    /**
     * Sets the value
     * @param value the value to set
     */
    void setValue(T value);
}
