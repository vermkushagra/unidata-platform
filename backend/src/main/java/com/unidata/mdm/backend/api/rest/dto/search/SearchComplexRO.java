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
     * Fields groups
     */
    private List<SearchFormFieldsGroupRO> formGroups;
    /**
     * supplementary Requests
     */
    private List<SearchComplexRO> supplementaryRequests = Collections.emptyList();

    /**
     * Is sayt search.
     */
    private boolean sayt = false;

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
    public List<SearchComplexRO> getSupplementaryRequests() {
        return supplementaryRequests;
    }

    /**
     * @param supplementaryRequests - supplementary Requests
     */
    public void setSupplementaryRequests(List<SearchComplexRO> supplementaryRequests) {
        this.supplementaryRequests = supplementaryRequests;
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

    /**
     * Form fields array.
     */
    public List<SearchFormFieldsGroupRO> getFormGroups() {
        return formGroups;
    }

    public void setFormGroups(List<SearchFormFieldsGroupRO> formGroups) {
        this.formGroups = formGroups;
    }
}
