
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

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;

/**
 * @author Mikhail Mikhailov
 * Base class for search requests.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SearchRequestRO {

    /**
     * Entity (index / type) name.
     */
    private String entity;

    /**
     * data type of requested documents
     */
    private SearchRequestDataType dataType = SearchRequestDataType.ETALON_DATA;
    /**
     * Query type.
     */
    private SearchRequestType qtype;
    /**
     * Combination operator.
     */
    private SearchRequestOperator operator;
    /**
     * Fields to return.
     */
    private List<String> returnFields;
    /**
     * Apply the following facets.
     */
    private List<String> facets;
    /**
     * set default sort if sortFields is empty
     */
    private boolean defaultSort = true;

    /**
     * Apply the following sorting settings.
     */
    private List<SearchSortFieldRO> sortFields;
    /**
     * Search after values
     */
    private List<Object> searchAfter;

    /**
     * Return count.
     */
    private int count = 10;
    /**
     * Return page.
     */
    private int page = 0;
    /**
     * Fetch source or not.
     */
    private boolean source;
    /**
     * Return total count.
     */
    private boolean totalCount = true;
    /**
     * Count only or return results too.
     */
    private boolean countOnly = false;
    /**
     * Fetch all without applying a query.
     */
    private boolean fetchAll = false;
    /**
     * Shortcut for all searchable fields.
     */
    private boolean returnAllFields = false;
    /**
     * Date hint.
     */
    private Date asOf;
    /**
     * Constructor.
     */
    public SearchRequestRO() {
        super();
    }

    /**
     * @return the entity
     */
    public String getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * @return - data type
     */
    public SearchRequestDataType getDataType() {
        return dataType;
    }

    /**
     * @param dataType - search data type
     */
    public void setDataType(SearchRequestDataType dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the qtype
     */
    public SearchRequestType getQtype() {
        return qtype;
    }

    /**
     * @param qtype the qtype to set
     */
    public void setQtype(String qtype) {
        this.qtype = SearchRequestType.safeValueOf(qtype);
    }

    /**
     * @return the operator
     */
    public SearchRequestOperator getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = SearchRequestOperator.safeValueOf(operator);
    }


    /**
     * @return the returnFields
     */
    public List<String> getReturnFields() {
        return returnFields;
    }


    /**
     * @param returnFields the returnFields to set
     */
    public void setReturnFields(List<String> returnFields) {
        this.returnFields = returnFields;
    }

    /**
     * @return the facets
     */
    public List<String> getFacets() {
        return facets;
    }

    /**
     * @param facets the facets to set
     */
    public void setFacets(List<String> facets) {
        this.facets = facets;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the source
     */
    public boolean isSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(boolean source) {
        this.source = source;
    }

    /**
     * @return the totalCount
     */
    public boolean isTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(boolean totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the countOnly
     */
    public boolean isCountOnly() {
        return countOnly;
    }

    /**
     * @param countOnly the countOnly to set
     */
    public void setCountOnly(boolean countOnly) {
        this.countOnly = countOnly;
    }

    /**
     * @return the fetchAll
     */
    public boolean isFetchAll() {
        return fetchAll;
    }

    /**
     * @param fetchAll the fetchAll to set
     */
    public void setFetchAll(boolean fetchAll) {
        this.fetchAll = fetchAll;
    }

    /**
     * @return the returnAllFields
     */
    public boolean isReturnAllFields() {
        return returnAllFields;
    }

    /**
     * @param returnAllFields the returnAllFields to set
     */
    public void setReturnAllFields(boolean returnAllFields) {
        this.returnAllFields = returnAllFields;
    }

    /**
     * @return sortFields
     */
    public List<SearchSortFieldRO> getSortFields() {
        return sortFields;
    }

    /**
     *
     * @param sortFields set of sort fields.
     */
    public void setSortFields(List<SearchSortFieldRO> sortFields) {
        this.sortFields = sortFields;
    }


    /**
     * @return the asOf
     */
    public Date getAsOf() {
        return asOf;
    }


    /**
     * @param asOf the asOf to set
     */
    public void setAsOf(Date asOf) {
        this.asOf = asOf;
    }


    public List<Object> getSearchAfter() {
        return searchAfter;
    }

    public void setSearchAfter(List<Object> searchAfter) {
        this.searchAfter = searchAfter;
    }

    public boolean isDefaultSort() {
        return defaultSort;
    }

    public void setDefaultSort(boolean defaultSort) {
        this.defaultSort = defaultSort;
    }
}
