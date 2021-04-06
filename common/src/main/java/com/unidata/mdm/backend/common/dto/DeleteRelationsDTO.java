/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Mass delete result DTO.
 */
public class DeleteRelationsDTO implements RelationsDTO<DeleteRelationDTO> {

    /**
     * Deleted relations.
     */
    private final Map<RelationStateDTO, List<DeleteRelationDTO>> relations;

    /**
     * Constructor.
     */
    public DeleteRelationsDTO(Map<RelationStateDTO, List<DeleteRelationDTO>> relations) {
        super();
        this.relations = relations;
    }

    /**
     * @return the deleted
     */
    @Override
    public Map<RelationStateDTO, List<DeleteRelationDTO>> getRelations() {
        return relations;
    }

}
