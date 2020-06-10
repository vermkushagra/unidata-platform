/**
 *
 */
package com.unidata.mdm.backend.common.dto.data.model;

import java.util.List;

import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;

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
