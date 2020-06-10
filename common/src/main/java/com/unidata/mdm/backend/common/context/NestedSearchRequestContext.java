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

package com.unidata.mdm.backend.common.context;

/**
 * @author Dmitry Kopin on 02.04.2018.
 */
public class NestedSearchRequestContext  {
    /**
     * @author Mikhail Mikhailov
     * Type of the nested search, supported by this context.
     */
    public enum NestedSearchType {
        /**
         * Has child joined queries.
         */
        HAS_CHILD,
        /**
         * Has parent joined queries.
         */
        HAS_PARENT,
        /**
         * Nested objects withing the same type.
         */
        NESTED_OBJECTS
    }

    private final SearchRequestContext nestedSearch;

    private final Integer minDocCount;

    private final NestedSearchType nestedSearchType;

    private final String nestedQueryName;

    private final boolean positive;

    public NestedSearchRequestContext(NestedSearchRequestContextBuilder builder){
        this.nestedSearch = builder.nestedSearch;
        this.minDocCount = builder.minDocCount;
        this.nestedSearchType = builder.nestedSearchType;
        this.nestedQueryName = builder.nestedQueryName;
        this.positive = builder.positive;
    }

    public static NestedSearchRequestContextBuilder builder(SearchRequestContext nestedSearch){
        NestedSearchRequestContextBuilder builder = new NestedSearchRequestContextBuilder();
        builder.nestedSearch = nestedSearch;
        return builder;
    }

    public SearchRequestContext getNestedSearch() {
        return nestedSearch;
    }

    /**
     * @return the nestedSearchType
     */
    public NestedSearchType getNestedSearchType() {
        return nestedSearchType;
    }

    /**
     * @return the nestedQueryName
     */
    public String getNestedQueryName() {
        return nestedQueryName;
    }

    public Integer getMinDocCount() {
        return minDocCount;
    }

    public boolean isPositive() {
        return positive;
    }

    public static class NestedSearchRequestContextBuilder{

        private SearchRequestContext nestedSearch;

        private Integer minDocCount;

        private NestedSearchType nestedSearchType;

        private String nestedQueryName;

        private boolean positive = true;

        private NestedSearchRequestContextBuilder(){
            super();
        }

        public NestedSearchRequestContext build(){
            return new NestedSearchRequestContext(this);
        }

        public NestedSearchRequestContextBuilder nestedSearchType(NestedSearchType nestedSearchType) {
            this.nestedSearchType = nestedSearchType;
            return this;
        }

        public NestedSearchRequestContextBuilder nestedQueryName(String nestedQueryName) {
            this.nestedQueryName = nestedQueryName;
            return this;
        }

        public NestedSearchRequestContextBuilder minDocCount(Integer minDocCount){
            this.minDocCount = minDocCount;
            return this;
        }

        public NestedSearchRequestContextBuilder positive(boolean positive){
            this.positive = positive;
            return this;
        }
    }
}
