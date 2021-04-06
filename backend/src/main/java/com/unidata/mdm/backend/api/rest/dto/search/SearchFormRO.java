package com.unidata.mdm.backend.api.rest.dto.search;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * REST search form.
 */
public class SearchFormRO extends SearchRequestRO {

    /**
     * Fields.
     */
    private List<SearchFormFieldRO> formFields;

    /**
     * Constructor.
     */
    public SearchFormRO() {
        super();
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

}
