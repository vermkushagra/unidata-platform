package com.unidata.mdm.backend.exchange.def.csv;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.exchange.def.SystemKey;

/**
 * @author Mikhail Mikhailov
 * UD etalon id field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsvSystemKey extends SystemKey {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 6816590112282793103L;
    /**
     * Indices for the external ID (normally single index).
     */
    private List<Integer> indices;
    /**
     * Optional join with element.
     */
    private String joinWith;
    /**
     * Constructor.
     */
    public CsvSystemKey() {
        super();
    }

    /**
     * @return the indices
     */
    public List<Integer> getIndices() {
        return indices;
    }


    /**
     * @param indices the indices to set
     */
    public void setIndices(List<Integer> indices) {
        this.indices = indices;
    }


    /**
     * @return the joinWith
     */
    public String getJoinWith() {
        return joinWith;
    }


    /**
     * @param joinWith the joinWith to set
     */
    public void setJoinWith(String joinWith) {
        this.joinWith = joinWith;
    }

}
