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

/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 *
 */
public class BulkUpsertRequestContext extends CommonRequestContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1381962967125886493L;
    /**
     * The bulk set.
     */
    private final List<UpsertRequestContext> bulkSet;
    /**
     * Constructor.
     * @param b builder instance
     */
    private BulkUpsertRequestContext(BulkUpsertRequestContextBuilder b) {
        super();
        this.bulkSet = b.bulkSet;

    }
    /**
     * @return the bulkSet
     */
    public List<UpsertRequestContext> getBulkSet() {
        return bulkSet;
    }

    /**
     * Checks if the bulk set is a valid one.
     * @return true if the bulk set valid, false otherwise
     */
    public boolean isBulkSet() {
        return bulkSet != null && !bulkSet.isEmpty();
    }

    /**
     * @author Mikhail Mikhailov
     * The builder class.
     */
    public static class BulkUpsertRequestContextBuilder {

        /**
         * The bulk set.
         */
        private List<UpsertRequestContext> bulkSet;

        /**
         * Constructor.
         */
        public BulkUpsertRequestContextBuilder() {
            super();
        }
        /**
         * Sets the bulk set to use.
         * @param bulkSet the bulk set
         * @return self
         */
        public BulkUpsertRequestContextBuilder bulkSet(List<UpsertRequestContext> bulkSet) {
            this.bulkSet = bulkSet;
            return this;
        }
        /**
         * Builder method.
         * @return new context
         */
        public BulkUpsertRequestContext build() {
            return new BulkUpsertRequestContext(this);
        }
    }
}
