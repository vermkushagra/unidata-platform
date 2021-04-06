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
     * Published only facet name - records with at least one approve
     */
    FACET_NAME_PUBLISHED_ONLY("published_only");
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

            return result.isEmpty() ? null : result;
        }

        return Collections.emptyList();
    }
}
