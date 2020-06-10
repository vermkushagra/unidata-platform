package com.unidata.mdm.backend.common.types.impl;

import java.util.Objects;

import com.unidata.mdm.backend.common.types.BinaryLargeValue;

/**
 * @author Mikhail Mikhailov
 * BLOB simple attribute.
 */
public class BlobSimpleAttributeImpl extends AbstractSimpleAttribute<BinaryLargeValue> {
    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected BlobSimpleAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public BlobSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name
     * @param value
     */
    public BlobSimpleAttributeImpl(String name, BinaryLargeValue value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.BLOB;
    }

    /**
     * Fluent part for compatibility.
     * @param value the value
     * @return self
     */
    public BlobSimpleAttributeImpl withValue(BinaryLargeValue value) {
        setValue(value);
        return this;
    }

    /**
     * @return hash code
     */
    @Override public int hashCode() {
        BinaryLargeValue bv = getValue();
        return Objects.hash(DataType.BLOB,
                bv != null ? bv.getFileName() : null,
                bv != null ? bv.getSize() : null,
                bv != null ? bv.getMimeType() : null,
                bv != null ? bv.getId() : null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V narrow(NarrowType type) {
        if(type == NarrowType.ES){
            return getValue() == null ? null : (V) getValue().getFileName();
        }
        return super.narrow(type);
    }
}
