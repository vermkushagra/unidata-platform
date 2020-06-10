package com.unidata.mdm.backend.common.dto;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author Mikhail Mikhailov
 * Search result wrapper, aiming to help CXF.
 */
public class SearchResultDTO {

    /**
     * Optional number of all potential hits.
     */
    private long totalCount;

    /**
     * Optional total count limit (max window size).
     */
    private long totalCountLimit;

    /**
     * Fields, participating in a query, if any.
     * Null means '_all'. If set, the same fields will be filled in the 'this.preview' field.
     */
    private List<String> fields;

    /**
     * Search hits.
     */
    private List<SearchResultHitDTO> hits;
    /**
     * Aggregations result.
     */
    private List<AggregationResultDTO> aggregates;
    /**
     * Max score in search result
     */
    private Float maxScore;
    /**
     * list of errors
     */
    private List<ErrorInfoDTO> errors;

    private List<Object> sortValues;

    /**
     * Constructor.
     */
    public SearchResultDTO() {
        super();
    }

    /**
     * @return the hits
     */
    @Nonnull
    public List<SearchResultHitDTO> getHits() {
        return hits == null ? Collections.emptyList() : hits;
    }

    /**
     * Sets the hits.
     * @param hits
     */
    public void setHits(List<SearchResultHitDTO> hits) {
        this.hits = hits;
    }
    /**
     * @return the aggregations
     */
    public List<AggregationResultDTO> getAggregates() {
        return aggregates;
    }

    /**
     * @param aggregations the aggregations to set
     */
    public void setAggregates(List<AggregationResultDTO> aggregations) {
        this.aggregates = aggregations;
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

    public Float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Float maxScore) {
        this.maxScore = maxScore;
    }

    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }

    public List<Object> getSortValues() {
        return sortValues;
    }

    public void setSortValues(List<Object> sortValues) {
        this.sortValues = sortValues;
    }

    public long getTotalCountLimit() {
        return totalCountLimit;
    }

    public void setTotalCountLimit(long totalCountLimit) {
        this.totalCountLimit = totalCountLimit;
    }
}