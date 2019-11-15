package org.unidata.mdm.search.type.indexing.impl;

import java.time.LocalTime;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Time.
 */
public class TimeIndexingField extends AbstractValueIndexingField<LocalTime, TimeIndexingField> {
    /**
     * Constructor.
     * @param name
     */
    public TimeIndexingField(String name) {
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
