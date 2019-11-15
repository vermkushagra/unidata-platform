package org.unidata.mdm.core.type.data;

/**
 * @author Mikhail Mikhailov
 * Binary large value.
 */
public interface BinaryLargeValue extends LargeValue {
    /**
     * {@inheritDoc}
     */
    @Override
    default ValueType getValueType() {
        return ValueType.BLOB;
    }
}
