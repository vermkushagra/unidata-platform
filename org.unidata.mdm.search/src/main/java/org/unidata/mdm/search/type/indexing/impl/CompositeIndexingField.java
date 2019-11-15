package org.unidata.mdm.search.type.indexing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.indexing.IndexingField;
import org.unidata.mdm.search.type.indexing.IndexingRecord;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Composite.
 */
public class CompositeIndexingField extends AbstractIndexingField<CompositeIndexingField> {
    /**
     * First level fields.
     */
    private final List<IndexingRecord> records = new ArrayList<>();
    /**
     * Constructor.
     * @param name
     */
    public CompositeIndexingField(String name) {
        super(name);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.COMPOSITE;
    }
    /**
     * @return the fields
     */
    public List<IndexingRecord> getRecords() {
        return records;
    }
    /**
     * Checks field for being empty.
     * @return true, if no fields were supplied.
     */
    public boolean isEmpty() {
        return records.isEmpty();
    }
    /**
     * Adds fields. The fields will be added at whole AS A RECORD!
     * @param f the fields
     * @return self
     */
    public CompositeIndexingField withRecord(IndexingField... f) {

        if (ArrayUtils.isNotEmpty(f)) {
            records.add(new IndexingRecordImpl(f));
        }
        return self();
    }
    /**
     * Adds fields. The fields will be added at whole AS A RECORD!
     * @param f the fields
     * @return self
     */
    public CompositeIndexingField withRecord(Collection<IndexingField> f) {

        if (CollectionUtils.isNotEmpty(f)) {
            records.add(new IndexingRecordImpl(f));
        }
        return self();
    }
    /**
     * Adds fields. The fields will be added at whole AS A RECORD!
     * @param f the fields
     * @return self
     */
    public CompositeIndexingField withRecord(Supplier<Collection<IndexingField>> f) {
        withRecord(f.get());
        return self();
    }
    /**
     * Adds fields. The fields will be added at whole AS A RECORD!
     * @param f the fields
     * @return self
     */
    public CompositeIndexingField withRecord(IndexingRecord r) {
        if (r != null && !r.isEmpty()) {
            records.add(r);
        }
        return self();
    }
    /**
     * Adds records.
     * @param r the records
     * @return self
     */
    public CompositeIndexingField withRecords(Collection<IndexingRecord> r) {
        if (r != null && !r.isEmpty()) {
            records.addAll(r);
        }
        return self();
    }
}
