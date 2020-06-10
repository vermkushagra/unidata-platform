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

package com.unidata.mdm.backend.common.search.types;

import javax.annotation.Nonnull;

/**
 * Search type for service indexes.
 */
public enum ServiceSearchType implements SearchType {
    /**
     * Model elements for search
     */
    MODEL("model_element", "model"),
    /**
     * Audit
     */
    AUDIT("audit_element", "audit"),
    /**
     * Classifier type
     */
    CLASSIFIER("clsf_element", "classifier");
    /**
     * Name of type
     */
    private final String type;

    /**
     * Index name
     */
    private final String indexName;

    ServiceSearchType(String type, String indexName) {
        this.type = type;
        this.indexName = indexName;
    }

    /**
     * @return index name;
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * @return name of type
     */
    @Nonnull
    @Override
    public String getName() {
        return type;
    }

    @Override
    public boolean isRelatedWith(SearchType searchType) {
        return false;
    }
}
