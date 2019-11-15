package org.unidata.mdm.search.type.indexing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Mikhail Mikhailov on Oct 9, 2019
 */
public abstract class AbstractValueIndexingField<T, X extends AbstractValueIndexingField<T, X>> extends AbstractIndexingField<X> {
    /**
     * The value.
     */
    private List<T> values;
    /**
     * Perform a transformation on values before indexing.
     */
    private Function<T, ?> transform;
    /**
     * Constructor.
     * @param name
     */
    public AbstractValueIndexingField(String name) {
        super(name);
    }
    /**
     * Tells, whether this attribute contains values
     * @return true, if empty, false otherise
     */
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(values);
    }
    /**
     * Returns true, if the attribute contains single value.
     * @return true for singleton, false otherwise
     */
    public boolean isSingleton() {
        return !isEmpty() && values.size() == 1;
    }
    /**
     * Returns true, if the attribute has transformations.
     * @return true for transformable field, false otherwise
     */
    public boolean hasTransform() {
        return transform != null;
    }
    /**
     * @return the value
     */
    public T getValue() {
        return isSingleton() ? values.get(0) : null;
    }
    /**
     * Returns the values collection
     * @return collection
     */
    public Collection<T> getValues() {
        return values == null ? Collections.emptyList() : values;
    }
    /**
     * @param value the value to set
     */
    public void setValue(T value) {
        if (Objects.isNull(this.values)) {
            this.values = new ArrayList<>();
        }
        this.values.add(value);
    }
    /**
     * @param value the value to set
     */
    public void setValues(Collection<T> value) {
        if (Objects.isNull(this.values)) {
            this.values = new ArrayList<>();
        }
        this.values.addAll(value);
    }
    /**
     * @return the transform
     */
    public Function<T, ?> getTransform() {
        return transform;
    }
    /**
     * @param transform the transform to set
     */
    public void setTransform(Function<T, ?> transform) {
        this.transform = transform;
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public X withValue(T value) {
        setValue(value);
        return self();
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public X withValues(Collection<T> value) {
        setValues(value);
        return self();
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public X withTransform(Function<T, ?> value) {
        setTransform(value);
        return self();
    }
}
