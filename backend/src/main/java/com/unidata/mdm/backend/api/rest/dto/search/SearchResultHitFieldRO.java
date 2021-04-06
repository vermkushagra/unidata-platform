/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.search;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Hit fields container.
 */
public class SearchResultHitFieldRO {

    /**
     * The field name.
     */
    private final String field;
    /**
     * Field value. First value from the search hits array.
     */
    private final Object value;
    /**
     * Multiple values.
     */
    private final List<Object> values;
    /**
     * Constructor.
     */
    public SearchResultHitFieldRO(final String field, final Object value, final List<Object> values) {
        super();
        this.field = field;
        this.value = value;
        this.values = values;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return the values
     */
    public List<Object> getValues() {
        return values;
    }

}
