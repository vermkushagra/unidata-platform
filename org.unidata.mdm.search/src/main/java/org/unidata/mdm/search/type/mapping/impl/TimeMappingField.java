package org.unidata.mdm.search.type.mapping.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Time.
 */
public class TimeMappingField extends AbstractTemporalMappingField<TimeMappingField> {
    /**
     * Constructor.
     * @param name
     */
    public TimeMappingField(String name) {
        super(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.TIME;
    }
}
