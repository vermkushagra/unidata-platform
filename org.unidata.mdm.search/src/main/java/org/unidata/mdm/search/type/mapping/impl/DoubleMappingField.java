package org.unidata.mdm.search.type.mapping.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Double.
 */
public class DoubleMappingField extends AbstractValueMappingField<DoubleMappingField> {
    /**
     * Constructor.
     * @param name
     */
    public DoubleMappingField(String name) {
        super(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.NUMBER;
    }
}
