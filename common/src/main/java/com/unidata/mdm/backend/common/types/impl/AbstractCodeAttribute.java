package com.unidata.mdm.backend.common.types.impl;

import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.common.types.CodeAttribute;

/**
 * @author Mikhail Mikhailov
 * Abstract code attribute.
 */
public abstract class AbstractCodeAttribute<T> extends AbstractAttribute implements CodeAttribute<T> {
    /**
     * Primary value.
     */
    private T value;
    /**
     * Supplementary values.
     */
    private List<T> supplementary;
    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected AbstractCodeAttribute() {
        super();
    }
    /**
     * Constructor.
     * @param name the name of the attribute
     */
    protected AbstractCodeAttribute(String name) {
        super(name);
    }
    /**
     * Constructor.
     * @param name
     */
    protected AbstractCodeAttribute(String name, T value) {
        this(name);
        this.value = value;
    }
    /**
     * Constructor.
     * @param name
     */
    protected AbstractCodeAttribute(String name, T value, List<T> supplementary) {
        this(name, value);
        this.supplementary = supplementary;
    }
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.types.CodeAttribute#getValue()
     */
    @Override
    public T getValue() {
        return value;
    }
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.types.CodeAttribute#setValue(java.lang.Object)
     */
    @Override
    public void setValue(T value) {
        this.value = value;
    }
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.types.CodeAttribute#getSupplementary()
     */
    @Override
    public List<T> getSupplementary() {
        return supplementary;
    }
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.types.CodeAttribute#setSupplementary(java.util.List)
     */
    @Override
    public void setSupplementary(List<T> value) {
        this.supplementary = value;
    }
    /**
     * @see java.lang.Object#hashCode()
     * TODO re-write this crap asap. Introduce solid value identity system instead.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getDataType(), getValue());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!getClass().isInstance(obj)) {
            return false;
        }

        AbstractCodeAttribute<?> other = (AbstractCodeAttribute<?>) obj;
        if (getDataType() != other.getDataType()) {
            return false;
        }

        return Objects.equals(getValue(), other.getValue());
    }

    /**
     * Creates code attribute.
     * @param type the type of the attribute
     * @param name the name of the attribute
     * @return attribute
     */
    public static CodeAttribute<?> of(CodeDataType type, String name) {

        if (Objects.isNull(name) || Objects.isNull(type)) {
            return null;
        }

        switch (type) {
        case STRING:
            return new StringCodeAttributeImpl(name);
        case INTEGER:
            return new IntegerCodeAttributeImpl(name);
        }

        return null;
    }

    /**
     * Creates code attribute
     * @param type the type of the attribut
     * @param name the name of the attribute
     * @param value the value to set
     * @return attribute
     */
    public static CodeAttribute<?> of(CodeDataType type, String name, Object value) {

        if (Objects.isNull(name) || Objects.isNull(type)) {
            return null;
        }

        switch (type) {
        case STRING:
            return new StringCodeAttributeImpl(name, (String) value);
        case INTEGER:
            return new IntegerCodeAttributeImpl(name, (Long) value);
        }

        return null;
    }
}
