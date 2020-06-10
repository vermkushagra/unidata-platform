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
 * Search type for entity indexes.
 */
public enum EntitySearchType implements HierarchicalSearchType {

    /**
     * Etalon type
     */
    ETALON("etalon"){
        @Override
        public boolean isTopType() {
            return true;
        }
    },
    /**
     * Data type
     */
    ETALON_DATA("data"),
    /**
     * Relation type
     */
    ETALON_RELATION("relation"),
    /**
     * Classifier data.
     */
    CLASSIFIER("classifier"),
    /**
     * Matching.
     */
    MATCHING_HEAD("matching_head"){
        @Override
        public boolean isTopType() {
            return true;
        }
    },
    /**
     * Matching.
     */
    MATCHING("matching"){
        @Override
        public HierarchicalSearchType getTopType() {
            return EntitySearchType.MATCHING_HEAD;
        }
    };
    /**
     * Name of type
     */
    private final String type;

    EntitySearchType(String type) {
        this.type = type;
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
        return searchType instanceof EntitySearchType;
    }

    @Nonnull
    @Override
    public HierarchicalSearchType getTopType() {
        return EntitySearchType.ETALON;
    }

    @Override
    public boolean isTopType() {
        return false;
    }
}
