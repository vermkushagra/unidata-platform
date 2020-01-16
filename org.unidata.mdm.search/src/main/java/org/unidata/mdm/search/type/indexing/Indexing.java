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
