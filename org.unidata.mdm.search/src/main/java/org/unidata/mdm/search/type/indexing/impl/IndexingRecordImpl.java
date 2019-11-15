package org.unidata.mdm.search.type.indexing.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.search.type.indexing.IndexingField;
import org.unidata.mdm.search.type.indexing.IndexingRecord;

/**
 * @author Mikhail Mikhailov on Oct 10, 2019
 * Default indexing record implementation.
 */
public class IndexingRecordImpl implements IndexingRecord {
    /**
     * First level fields.
     */
    private final List<IndexingField> fields = new ArrayList<>();
    /**
     * Constructor.
     * @param f the fields
     */
    public IndexingRecordImpl(IndexingField... f) {
        this(Arrays.asList(f));
    }
    /**
     * Constructor.
     * @param f the fields
     */
    public IndexingRecordImpl(Collection<IndexingField> f) {
        super();
        if (CollectionUtils.isNotEmpty(f)) {
            fields.addAll(f);
        }
    }
    /**
     * Constructor.
     * @param f the fields
     */
    public IndexingRecordImpl(Supplier<Collection<IndexingField>> f) {
        this(f.get());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<IndexingField> getFields() {
        return fields;
    }
}
