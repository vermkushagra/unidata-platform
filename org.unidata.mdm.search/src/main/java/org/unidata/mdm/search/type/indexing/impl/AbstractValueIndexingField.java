/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
