package org.unidata.mdm.search.type.mapping.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.mapping.MappingField;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 * Composite.
 */
public class CompositeMappingField extends AbstractMappingField<CompositeMappingField> {
    /**
     * Index content as nested type.
     */
    private boolean nested;
    /**
     * First level fields.
     */
    private final List<MappingField> fields = new ArrayList<>();
    /**
     * Constructor.
     * @param name
     */
    public CompositeMappingField(String name) {
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
     * @return the nested
     */
    public boolean isNested() {
        return nested;
    }
    /**
     * @param nested the nested to set
     */
    public void setNested(boolean nested) {
        this.nested = nested;
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
    public CompositeMappingField withNested(boolean nested) {
        this.nested = nested;
        return self();
    }
    /**
     * Adds fields.
     * @param f the fields
     * @return self
     */
    public CompositeMappingField withFields(MappingField... f) {

        for (int i = 0; f != null && i < f.length; i++) {
            fields.add(f[i]);
        }
        return self();
    }
    /**
     * Adds fields.
     * @param f the fields
     * @return self
     */
    public CompositeMappingField withFields(Collection<MappingField> f) {

        if (CollectionUtils.isNotEmpty(f)) {
            fields.addAll(f);
        }
        return self();
    }
    /**
     * Adds fields.
     * @param f the fields
     * @return self
     */
    public CompositeMappingField withFields(Supplier<Collection<MappingField>> f) {
        withFields(f.get());
        return self();
    }
}
