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

package com.unidata.mdm.backend.common.search;


import javax.annotation.Nonnull;

public class SortField {

    private final String fieldName;
    private final SortOrder sortOrder;
    private final boolean isAnalyzedAttribute;

    public SortField(@Nonnull String fieldName, @Nonnull SortOrder sortOrder, boolean isAnalyzedAttribute) {
        this.fieldName = fieldName;
        this.sortOrder = sortOrder;
        this.isAnalyzedAttribute = isAnalyzedAttribute;
    }

    @Nonnull
    public String getFieldName() {
        return fieldName;
    }

    @Nonnull
    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public boolean isAnalyzedAttribute() {
        return isAnalyzedAttribute;
    }

    public enum SortOrder {
        /**
         * Ascending order.
         */
        ASC {
            @Override
            public String toString() {
                return "asc";
            }
        },
        /**
         * Descending order.
         */
        DESC {
            @Override
            public String toString() {
                return "desc";
            }
        }
    }

}
