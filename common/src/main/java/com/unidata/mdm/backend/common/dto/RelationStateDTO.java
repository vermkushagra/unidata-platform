/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.Date;

import com.unidata.mdm.meta.RelType;

/**
 * @author Mikhail Mikhailov
 * Relation state DTO.
 */
public class RelationStateDTO {

    /**
     * Relation name.
     */
    private String relationName;
    /**
     * Relation type.
     */
    private RelType relationType;
    /**
     * Minimum lower bound.
     */
    private Date rangeFrom;
    /**
     * Maximum upper bound.
     */
    private Date rangeTo;
    /**
     * Constructor.
     */
    public RelationStateDTO() {
        super();
    }
    /**
     * Constructor.
     */
    public RelationStateDTO(String relationName, RelType relationType, Date rangeFrom, Date rangeTo) {
        super();
        this.relationName = relationName;
        this.relationType = relationType;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }
    /**
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }
    /**
     * @param relationName the relationName to set
     */
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
    /**
     * @return the relationType
     */
    public RelType getRelationType() {
        return relationType;
    }
    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(RelType relationType) {
        this.relationType = relationType;
    }
    /**
     * @return the rangeFrom
     */
    public Date getRangeFrom() {
        return rangeFrom;
    }
    /**
     * @param rangeFrom the rangeFrom to set
     */
    public void setRangeFrom(Date rangeFrom) {
        this.rangeFrom = rangeFrom;
    }
    /**
     * @return the rangeTo
     */
    public Date getRangeTo() {
        return rangeTo;
    }
    /**
     * @param rangeTo the rangeTo to set
     */
    public void setRangeTo(Date rangeTo) {
        this.rangeTo = rangeTo;
    }
}
