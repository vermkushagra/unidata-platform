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
