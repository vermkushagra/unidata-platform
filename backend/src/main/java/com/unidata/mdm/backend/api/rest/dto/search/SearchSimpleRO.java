/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.search;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Simple search request.
 */
public class SearchSimpleRO extends SearchRequestRO {

    /**
     * Search fields.
     */
    private List<String> searchFields;
    /**
     * Search text.
     */
    private String text;

    /**
     * Is sayt search.
     */
    private boolean sayt = false;

    /**
     * Constructor.
     */
    public SearchSimpleRO() {
        super();
    }

    /**
     * @return the searchFields
     */
    public List<String> getSearchFields() {
        return searchFields;
    }

    /**
     * @param searchFields the searchFields to set
     */
    public void setSearchFields(List<String> searchFields) {
        this.searchFields = searchFields;
    }


    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return is sayt
     */
    public boolean isSayt() {
        return sayt;
    }

    /**
     * @param sayt sayt
     */
    public void setSayt(boolean sayt) {
        this.sayt = sayt;
    }
}
