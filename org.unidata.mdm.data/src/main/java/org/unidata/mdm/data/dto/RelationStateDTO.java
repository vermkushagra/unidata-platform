/**
 *
 */
package org.unidata.mdm.data.dto;

import java.util.Date;

import org.unidata.mdm.data.type.data.RelationType;

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
    private RelationType relationType;
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
    public RelationStateDTO(String relationName, RelationType relationType, Date rangeFrom, Date rangeTo) {
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
    public RelationType getRelationType() {
        return relationType;
    }
    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(RelationType relationType) {
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
