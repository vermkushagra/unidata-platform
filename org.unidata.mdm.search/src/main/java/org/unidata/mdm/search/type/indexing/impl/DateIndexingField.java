package org.unidata.mdm.search.type.indexing.impl;

import java.time.LocalDate;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Date (TS).
 */
public class DateIndexingField extends AbstractValueIndexingField<LocalDate, DateIndexingField> {
    /**
     * Constructor.
     * @param name
     */
    public DateIndexingField(String name) {
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
