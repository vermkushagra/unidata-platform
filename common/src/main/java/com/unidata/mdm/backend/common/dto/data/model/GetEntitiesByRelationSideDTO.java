/**
 *
 */
package com.unidata.mdm.backend.common.dto.data.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Entities and relations view, filtered by side ('to' or 'from').
 */
public class GetEntitiesByRelationSideDTO {

    /**
     * Filtered entities view.
     */
    final Map<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> entities;

    /**
     * Constructor.
     */
    public GetEntitiesByRelationSideDTO(
            Map<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> entities) {
        this.entities = entities;
    }

    /**
     * @return the entities
     */
    public Map<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> getEntities() {
        return entities;
    }

}
