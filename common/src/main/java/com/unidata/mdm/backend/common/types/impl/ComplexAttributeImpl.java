package com.unidata.mdm.backend.common.types.impl;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ComplexAttributeImpl extends AbstractAttribute implements ComplexAttribute {

    /**
     * Keys attributes.
     */
    private List<SimpleAttribute<?>> keyAttributes = new ArrayList<>();

    /**
     * Nested records.
     */
    private List<DataRecord> records = new ArrayList<>();
    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected ComplexAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public ComplexAttributeImpl(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataRecord> getRecords() {
        return records;
    }

    /**
     * TODO: implement and sync.
     * {@inheritDoc}
     */
    @Override
    public List<SimpleAttribute<?>> getKeyAttributes() {
        return keyAttributes;
    }

}
