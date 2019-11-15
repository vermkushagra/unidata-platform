package org.unidata.mdm.meta.dto;

import java.util.List;

import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.NestedEntityDef;
import org.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Entity metadata container.
 */
public class GetEntityDTO {

    /**
     * Entity.
     */
    private final EntityDef entity;
    /**
     * References.
     */
    private final List<NestedEntityDef> refs;
    /**
     * Relations.
     */
    private final List<RelationDef> relations;

    /**
     * Constructor.
     */
    public GetEntityDTO(EntityDef entity, List<NestedEntityDef> refs, List<RelationDef> relations) {
        super();
        this.entity = entity;
        this.refs = refs;
        this.relations = relations;
    }
    /**
     * @return the entity
     */
    public EntityDef getEntity() {
        return entity;
    }

    /**
     * @return the refs
     */
    public List<NestedEntityDef> getRefs() {
        return refs;
    }

    /**
     * @return the relations
     */
    public List<RelationDef> getRelations() {
        return relations;
    }
}
