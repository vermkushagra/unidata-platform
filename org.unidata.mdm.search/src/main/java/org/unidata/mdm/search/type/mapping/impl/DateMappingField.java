package org.unidata.mdm.search.type.mapping.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Date (TS).
 */
public class DateMappingField extends AbstractTemporalMappingField<DateMappingField> {
    /**
     * Constructor.
     * @param name
     */
    public DateMappingField(String name) {
        super(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.DATE;
    }
}
