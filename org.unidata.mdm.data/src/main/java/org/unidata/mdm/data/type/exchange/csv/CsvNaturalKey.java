/**
 *
 */
package org.unidata.mdm.data.type.exchange.csv;

import java.util.List;

import org.unidata.mdm.data.type.exchange.NaturalKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsvNaturalKey extends NaturalKey {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 7960701886127924429L;
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
    public CsvNaturalKey() {
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
