package org.unidata.mdm.search.type.indexing.impl;

import java.time.LocalDateTime;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Time.
 */
public class TimestampIndexingField extends AbstractValueIndexingField<LocalDateTime, TimestampIndexingField> {
    /**
     * Constructor.
     * @param name
     */
    public TimestampIndexingField(String name) {
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
