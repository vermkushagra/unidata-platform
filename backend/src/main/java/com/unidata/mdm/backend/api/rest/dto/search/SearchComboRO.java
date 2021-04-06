package com.unidata.mdm.backend.api.rest.dto.search;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Has both, form and simple section.
 */
public class SearchComboRO extends SearchRequestRO {

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
     * Constructor.
     */
    public SearchComboRO() {
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
     * @param formFields the formFields to set
     */
    public void setFormFields(List<SearchFormFieldRO> fields) {
        this.formFields = fields;
    }
}
