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
