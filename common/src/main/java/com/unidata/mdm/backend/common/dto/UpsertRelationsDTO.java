/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 *
 */
public class UpsertRelationsDTO implements RelationsDTO<UpsertRelationDTO> {

    /**
     * Relations upsert result.
     */
    private final Map<RelationStateDTO, List<UpsertRelationDTO>> relations;

    /**
     * Constructor.
     */
    public UpsertRelationsDTO(Map<RelationStateDTO, List<UpsertRelationDTO>> relations) {
        super();
        this.relations = relations;
    }

    /**
     * @return the relations
     */
    @Override
    public Map<RelationStateDTO, List<UpsertRelationDTO>> getRelations() {
        return relations;
    }
}
