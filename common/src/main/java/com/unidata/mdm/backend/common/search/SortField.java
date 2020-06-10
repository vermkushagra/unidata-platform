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
