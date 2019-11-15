package org.unidata.mdm.search.type.indexing.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Bool.
 */
public final class BooleanIndexingField extends AbstractValueIndexingField<Boolean, BooleanIndexingField> {
    /**
     * Constructor.
     * @param name
     */
    public BooleanIndexingField(String name) {
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
