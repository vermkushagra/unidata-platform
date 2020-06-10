/**
 *
 */
package com.unidata.mdm.backend.common.search;

/**
 * @author Mikhail Mikhailov
 * Simple logical operator.
 */
public enum SearchRequestOperator {
    /**
     * Simple AND.
     */
    OP_OR,
    /**
     * Simple OR.
     */
    OP_AND;

    /**
     * No throw valueOf(..).
     * @param val string value
     * @return {@link SearchRequestOperator} instance or null
     */
    public static SearchRequestOperator safeValueOf(String val) {

        if (val != null) {
            switch (val) {
            case "OR":
                return OP_OR;
            case "AND":
                return OP_AND;
            default:
                break;
            }
        }

        return SearchRequestOperator.OP_AND;
    }
}
