package com.unidata.mdm.backend.common.types;

import java.util.List;
import java.util.Objects;

/**
 * @author Mikhail Mikhailov
 * Complex attribute.
 */
public interface ComplexAttribute extends Attribute {
    /**
     * {@inheritDoc}
     */
    @Override
    default AttributeType getAttributeType() {
        return AttributeType.COMPLEX;
    }
    /**
     * {@inheritDoc}
     */
    default boolean isEmpty() {
        return Objects.isNull(getRecords()) || getRecords().isEmpty();
    }
    /**
     * Gets nested records.
     * @return record list
     */
    List<DataRecord> getRecords();
    /**
     * Gets key attributes of the nested records.
     * @return list of key attributes.
     */
    List<SimpleAttribute<?>> getKeyAttributes();
}
