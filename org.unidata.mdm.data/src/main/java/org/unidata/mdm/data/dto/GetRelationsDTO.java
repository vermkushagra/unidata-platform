/**
 *
 */
package org.unidata.mdm.data.dto;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Top level relations DTO.
 */
public class GetRelationsDTO implements RelationsDTO<GetRelationDTO> {
    /**
     * The relations.
     */
    private final Map<RelationStateDTO, List<GetRelationDTO>> relations;
    /**
     * Constructor.
     */
    public GetRelationsDTO(Map<RelationStateDTO, List<GetRelationDTO>> relations) {
        super();
        this.relations = relations;
    }
    /**
     * @return the relations
     */
    @Override
    public Map<RelationStateDTO, List<GetRelationDTO>> getRelations() {
        return relations;
    }
}
