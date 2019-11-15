package org.unidata.mdm.search.type.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.search.type.IndexType;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * Mapping container for simple fields and other mappinged objects.
 */
public class Mapping {
    /**
     * The type.
     */
    private final IndexType indexType;
    /**
     * First level fields.
     */
    private final List<MappingField> fields = new ArrayList<>();
    /**
     * Constructor.
     */
    public Mapping(IndexType type) {
        super();
        this.indexType = type;
    }
    /**
     * @return the indexType
     */
    public IndexType getIndexType() {
        return indexType;
    }
    /**
     * @return the fields
     */
    public List<MappingField> getFields() {
        return fields;
    }
    /**
     * Adds fields.
     * @param f the fields
     * @return self
     */
    public Mapping withFields(MappingField... f) {

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
    public Mapping withFields(Collection<MappingField> f) {

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
    public Mapping withFields(Supplier<Collection<MappingField>> f) {
        withFields(f.get());
        return this;
    }
}
