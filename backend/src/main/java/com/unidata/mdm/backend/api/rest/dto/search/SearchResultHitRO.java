/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.search;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * @author Mikhail Mikhailov
 * DTO, implementing a search hit.
 */
public class SearchResultHitRO {

    /**
     * Object id. The object identifier, supplied by the DB during create.
     */
    private final String id;
    /**
     * Record status.
     */
    @JsonProperty(value = "status", required = true, defaultValue = "ACTIVE")
    @JsonInclude(content = Include.ALWAYS)
    private String status;
    /**
     * Preview map, containing search keys alone with their values as strings.
     */
    @JsonProperty(value = "preview", required = true, defaultValue = "[]")
    @JsonInclude(content = Include.ALWAYS)
    private final List<SearchResultHitFieldRO> preview = new ArrayList<SearchResultHitFieldRO>();
    
    /**
     * JSON object at whole as raw value.
     */
    @JsonProperty(value = "source", required = true, defaultValue = "{}")
    @JsonInclude(content = Include.ALWAYS)
    private Object source;

    /**
     * score for hit
     */
    @JsonProperty(required = false)
    private Float score;
    /**
     * Constructor.
     */
    public SearchResultHitRO(final String id) {
        super();
        this.id = id;
    }

    /**
     * Gets the object, found by search.
     * @return the source
     */
    @JsonRawValue
    public Object getSource() {
        return source;
    }

    /**
     * Sets the object, found by search.
     * @param source the source to set
     */
    public void setSource(Object source) {
        this.source = source;
    }

    /**
     * Gets the object's id.
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the preview as key = value pairs.
     * @return the preview
     */
    public List<SearchResultHitFieldRO> getPreview() {
        return preview;
    }

    public String getStatus() {
        return status;
    }
    

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
}
