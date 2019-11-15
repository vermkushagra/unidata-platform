package org.unidata.mdm.search.type.indexing.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * String.
 */
public final class StringIndexingField extends AbstractValueIndexingField<String, StringIndexingField> {
    /**
     * Constructor.
     */
    public StringIndexingField(String name) {
        super(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.STRING;
    }
}
