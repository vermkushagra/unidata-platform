package org.unidata.mdm.search.type.indexing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.ManagedIndexId;

/**
 * @author Mikhail Mikhailov on Oct 9, 2019
 * Indexing info for a type.
 */
public class Indexing implements IndexingRecord {
    /**
     * The type.
     */
    private final IndexType indexType;
    /**
     * This record index id.
     */
    private final ManagedIndexId indexId;
    /**
     * First level fields.
     */
    private final List<IndexingField> fields = new ArrayList<>();
    /**
     * Constructor.
     */
    public Indexing(IndexType type, ManagedIndexId id) {
        super();
        this.indexType = type;
        this.indexId = id;
    }
    /**
     * @return the indexType
     */
    public IndexType getIndexType() {
        return indexType;
    }
    /**
     * @return the indexId
     */
    public ManagedIndexId getIndexId() {
        return indexId;
    }
    /**
     * @return the fields
     */
    @Override
    public List<IndexingField> getFields() {
        return fields;
    }
    /**
     * Adds fields.
     * @param f the fields
     * @return self
     */
    public Indexing withFields(IndexingField... f) {

        for (int i = 0; f != null && i < f.length; i++) {
            fields.add(f[i]);
        }
        return this;
    }
    /**
     * Adds fields.
     * @param f the fields
     * @return self
     */
    public Indexing withFields(Collection<IndexingField> f) {

        if (CollectionUtils.isNotEmpty(f)) {
            fields.addAll(f);
        }
        return this;
    }
    /**
     * Adds fields.
     * @param f the fields
     * @return self
     */
    public Indexing withFields(Supplier<Collection<IndexingField>> f) {
        withFields(f.get());
        return this;
    }
}
