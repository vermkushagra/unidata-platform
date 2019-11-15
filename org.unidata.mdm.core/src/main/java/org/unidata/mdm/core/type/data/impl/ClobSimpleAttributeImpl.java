package org.unidata.mdm.core.type.data.impl;

import java.util.Objects;

import org.unidata.mdm.core.type.data.CharacterLargeValue;

/**
 * @author Mikhail Mikhailov
 *  CLOB simple attribute.
 */
public class ClobSimpleAttributeImpl extends AbstractSimpleAttribute<CharacterLargeValue> {

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected ClobSimpleAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public ClobSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name
     * @param value
     */
    public ClobSimpleAttributeImpl(String name, CharacterLargeValue value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.CLOB;
    }

    /**
     * Fluent part for compatibility.
     * @param value the value
     * @return self
     */
    public ClobSimpleAttributeImpl withValue(CharacterLargeValue value) {
        setValue(value);
        return this;
    }

    /**
     * @return hash code
     */
    @Override public int hashCode() {
        CharacterLargeValue cv = getValue();
        return Objects.hash(DataType.CLOB,
                cv != null ? cv.getFileName() : null,
                cv != null ? cv.getSize() : null,
                cv != null ? cv.getMimeType() : null,
                cv != null ? cv.getId() : null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V narrow(NarrowType type) {
        if (type == NarrowType.ES) {
            return getValue() == null ? null : (V) getValue().getFileName();
        }
        return super.narrow(type);
    }
}
