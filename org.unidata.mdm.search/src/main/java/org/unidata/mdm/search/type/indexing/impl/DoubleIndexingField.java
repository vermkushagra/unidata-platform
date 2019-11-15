package org.unidata.mdm.search.type.indexing.impl;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Double.
 */
public class DoubleIndexingField extends AbstractValueIndexingField<Double, DoubleIndexingField> {
    /**
     * Constructor.
     * @param name
     */
    public DoubleIndexingField(String name) {
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
