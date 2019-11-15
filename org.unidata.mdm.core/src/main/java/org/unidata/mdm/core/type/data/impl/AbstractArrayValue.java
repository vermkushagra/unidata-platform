package org.unidata.mdm.core.type.data.impl;

import org.unidata.mdm.core.type.data.ArrayValue;

/**
 * @author Mikhail Mikhailov
 * Array value.
 */
public abstract class AbstractArrayValue<T> implements ArrayValue<T> {
    /**
     * The value
     */
    private T value;
    /**
     * Display value.
     */
    private String displayValue;
    /**
     * Constructor.
     */
    public AbstractArrayValue() {
        super();
    }

    /**
     * Constructor.
     */
    public AbstractArrayValue(T value) {
        this();
        this.value = value;
    }
    /**
     * Constructor.
     */
    public AbstractArrayValue(T value, String displayValue) {
        this(value);
        this.displayValue = displayValue;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(T value) {
        this.value = value;
    }

	@Override
	public String toString() {
		return  value.toString();
	}
}
