package org.unidata.mdm.search.type.mapping.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Bool.
 */
public final class BooleanMappingField extends AbstractValueMappingField<BooleanMappingField> {
    /**
     * Constructor.
     * @param name
     */
    public BooleanMappingField(String name) {
        super(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.BOOLEAN;
    }
}
