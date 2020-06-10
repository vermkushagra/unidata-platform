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

import java.util.Collection;

/**
 * @author Mikhail Mikhailov
 * Aggregation super type.
 */
public interface AggregationRequestContext extends SearchContext {
    /**
     * Gets type.
     * @return type
     */
    AggregationType getAggregationType();
    /**
     * Gets the aggregation name.
     * @return name
     */
    String getName();
    /**
     * Gets sub aggregations.
     * @return sub aggregations
     */
    Collection<AggregationRequestContext> aggregations();
    /**
     * Gets a sub aggregation by name.
     * @param subAggregationName the name
     * @return sub aggregation
     */
    <T extends AggregationRequestContext> T subAggregation(String subAggregationName);
    /**
     * Narrows type.
     * @return self cast to narrowed type
     */
    @SuppressWarnings("unchecked")
    default <T extends AggregationRequestContext> T narrow() {
        return (T) this;
    }
    /**
     * Type of aggregation
     */
    public enum AggregationType {
        NESTED,
        REVERSE_NESTED,
        FILTER,
        FILTERS {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isMultiBucket() {
                return true;
            }
        },
        TERM {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isMultiBucket() {
                return true;
            }
        },
        CARDINALITY,
        VALUE_COUNT;
        /**
         * Tells whether this type is a multibucket one.
         * @return true if so, false otherwise.
         */
        public boolean isMultiBucket() {
            return false;
        }
    }
}
