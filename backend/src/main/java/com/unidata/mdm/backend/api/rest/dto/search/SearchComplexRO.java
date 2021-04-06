package com.unidata.mdm.backend.api.rest.dto.search;

import java.util.Collections;
import java.util.List;

public class SearchComplexRO extends SearchRequestRO {
    /**
     * Search fields.
     */
    private List<String> searchFields;
    /**
     * Search text.
     */
    private String text;
    /**
     * Fields.
     */
    private List<SearchFormFieldRO> formFields;
    /**
     * supplementary Requests
     */
    private List<SearchFormRO> supplementaryRequests = Collections.emptyList();

    /**
     * Constructor.
     */
    public SearchComplexRO() {
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
     * @return the formFields
     */
    public List<SearchFormFieldRO> getFormFields() {
        return formFields;
    }

    /**
     * @param fields the formFields to set
     */
    public void setFormFields(List<SearchFormFieldRO> fields) {
        this.formFields = fields;
    }

    /**
     * @return - supplementary Requests
     */
    public List<SearchFormRO> getSupplementaryRequests() {
        return supplementaryRequests;
    }

    /**
     * @param supplementaryRequests - supplementary Requests
     */
    public void setSupplementaryRequests(List<SearchFormRO> supplementaryRequests) {
        this.supplementaryRequests = supplementaryRequests;
    }
}
