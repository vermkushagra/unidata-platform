/**
 *
 */
package org.unidata.mdm.search.type.search;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Mikhail Mikhailov
 * Type of the search request.
 */
public enum SearchRequestType {
    /**
     * ES match query.
     */
    MATCH,
    /**
     * ES fuzzy search type.
     */
    FUZZY,
    @Deprecated
    TERM,
    QSTRING;

    /**
     * No throw valueOf(..).
     * @param val string value
     * @return {@link SearchRequestType} instance or null
     */
    public static SearchRequestType safeValueOf(String val) {
        if (val != null) {
            for (SearchRequestType type : SearchRequestType.values()) {
                if (StringUtils.equals(type.name(), val)) {
                    return type;
                }
            }
        }

        // Return default if null
        return SearchRequestType.MATCH;
    }
}
