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
