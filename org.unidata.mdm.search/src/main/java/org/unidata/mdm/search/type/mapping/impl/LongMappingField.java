package org.unidata.mdm.search.type.mapping.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Long.
 */
public final class LongMappingField extends AbstractValueMappingField<LongMappingField> {
    /**
     * Constructor.
     * @param name
     */
    public LongMappingField(String name) {
        super(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.INTEGER;
    }
}
