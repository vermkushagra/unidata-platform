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
package com.unidata.mdm.backend.common.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Supported facet names.
 */
public enum FacetName {
    /**
     * Return duplicates only.
     */
    FACET_NAME_DUPLICATES_ONLY ("duplicates_only"),
    /**
     * Return manual duplicates only.
     */
    FACET_NAME_MANUAL_DUPLICATES_ONLY ("manual_duplicates_only"),
    /**
     * Return auto duplicates only.
     */
    FACET_NAME_AUTO_DUPLICATES_ONLY ("auto_duplicates_only"),
    /**
     * Show records with errors only.
     */
    FACET_NAME_ERRORS_ONLY("errors_only"),
    /**
     * Inactive only facet name.
     */
    FACET_NAME_INACTIVE_ONLY("inactive_only"),
    /**
     * Pending only facet name.
     */
    FACET_NAME_PENDING_ONLY("pending_only"),
    /**
     * Active only facet name - without pending and deleted
     */
    FACET_NAME_ACTIVE_ONLY("active_only"),
    /**
     * asOf date will not be append to search request
     */
    FACET_UN_RANGED("un_ranged"),
    /**
     * Include inactive periods facet name.
     */
    FACET_NAME_INACTIVE_PERIODS("include_inactive_periods"),
    /**
     * Published only facet name - records with at least one approve
     */
    FACET_NAME_PUBLISHED_ONLY("published_only"),
    /**
     * Return operation type direct.
     */
    FACET_NAME_OPERATION_TYPE_DIRECT ("operation_type_direct"),
    /**
     * Return operation type cascaded.
     */
    FACET_NAME_OPERATION_TYPE_CASCADED ("operation_type_cascaded");

    /**
     * Constructor.
     * @param value
     */
    private FacetName(String value) {
        this.value = value;
    }

    /**
     * Internal value, used kin ES requests.
     */
    private final String value;

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Standard fromValue method.
     * @param v the value
     * @return {@linkplain FacetName} or null
     */
    public static FacetName fromValue(String v) {

        for (FacetName n : values()) {
            if (n.value.equals(v)) {
                return n;
            }
        }

        return null;
    }

    /**
     * Standard fromValue method.
     * @param v the value
     * @return {@linkplain FacetName} or null
     */
    public static List<FacetName> fromValues(List<String> v) {

        if (v != null) {
            List<FacetName> result = new ArrayList<>();
            for (String n : v) {
                FacetName f = fromValue(n);
                if (f != null) {
                    result.add(f);
                }
            }

            return result;
        }

        return Collections.emptyList();
    }
}
