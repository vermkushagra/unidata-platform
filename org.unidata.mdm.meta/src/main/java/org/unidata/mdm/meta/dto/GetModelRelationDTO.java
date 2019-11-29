package org.unidata.mdm.meta.dto;

import org.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov on Nov 28, 2019
 */
public class GetModelRelationDTO {
    /**
     * The requested relation and nothing else so far.
     */
    private RelationDef relation;
    /**
     * Constructor.
     */
    public GetModelRelationDTO() {
        super();
    }
    /**
     * Constructor.
     */
    public GetModelRelationDTO(RelationDef relation) {
        super();
        this.relation = relation;
    }
    /**
     * @return the relation
     */
    public RelationDef getRelation() {
        return relation;
    }
    /**
     * @param relation the relation to set
     */
    public void setRelation(RelationDef relation) {
        this.relation = relation;
    }
}
