package org.unidata.mdm.search.type.indexing.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Long.
 */
public final class LongIndexingField extends AbstractValueIndexingField<Long, LongIndexingField> {
    /**
     * Constructor.
     * @param name
     */
    public LongIndexingField(String name) {
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
