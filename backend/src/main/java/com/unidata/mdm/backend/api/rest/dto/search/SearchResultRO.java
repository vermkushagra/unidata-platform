/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.search;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;

/**
 * @author Mikhail Mikhailov
 * Search result wrapper, aiming to help CXF.
 */
public class SearchResultRO {

    /**
     * Optional number of all potential hits.
     */
    @JsonProperty(value = "total_count", index = 1)
    private long totalCount;

    /**
     * Fields, participating in a query, if any.
     * Null means '_all'. If set, the same fields will be filled in the 'this.preview' field.
     */
    @JsonProperty(index = 2)
    private List<String> fields;

    /**
     * Search hits.
     */
    @JsonProperty(index = 3)
    private List<SearchResultHitRO> hits = new ArrayList<>();
    @JsonProperty(index = 4)
    private List<ErrorInfo> errors = new ArrayList<>();

    /**
     * Success variable. Temporary. TODO remove afterwards.
     */
    @JsonProperty(required = true, defaultValue = "true")
    private boolean success = true;
    @JsonProperty(required = false, defaultValue = "true")
    private boolean hasRecords = true;
    /**
     * Max score for search
     */
    @JsonProperty(required = false)
    private Float maxScore;

    /**
     * Constructor.
     */
    public SearchResultRO() {
        super();
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the hits
     */
    public List<SearchResultHitRO> getHits() {
        return hits;
    }

    /**
     * Returns the fields, participated in search action. Null for _all.
     * @return the fields or null
     */
    public List<String> getFields() {
        return fields;
    }

    /**
     * Sets the fields, participating in search action. Null for _all.
     * @param fields the fields to set
     */
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /**
     * Gets the total number of potential hits.
     * @return the totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total number of potential hits.
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

	/**
	 * Gets the hsaRecords flag.
	 * @return the hasRecords
	 */
	public boolean isHasRecords() {
		return hasRecords;
	}

	/**
	 * Sets the hsaRecords flag.
	 * @param hasRecords the hasRecords to set
	 */
	public void setHasRecords(boolean hasRecords) {
		this.hasRecords = hasRecords;
	}


    public Float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Float maxScore) {
        this.maxScore = maxScore;
    }

    public List<ErrorInfo> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfo> errors) {
        this.errors = errors;
    }
}
