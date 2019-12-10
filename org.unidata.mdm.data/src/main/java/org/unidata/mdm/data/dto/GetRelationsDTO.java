/**
 *
 */
package org.unidata.mdm.data.dto;

import java.util.List;
import java.util.Map;

import org.unidata.mdm.system.dto.ResultFragment;
import org.unidata.mdm.system.dto.ResultFragmentId;

/**
 * @author Mikhail Mikhailov
 * Top level relations DTO.
 */
public class GetRelationsDTO implements RelationsDTO<GetRelationDTO>, ResultFragment<GetRelationsDTO> {
    /**
     * This fragment ID.
     */
    public static final ResultFragmentId<GetRelationsDTO> ID
        = new ResultFragmentId<>("GET_RELATIONS_RESULT", GetRelationsDTO::new);
    /**
     * The relations.
     */
    private Map<RelationStateDTO, List<GetRelationDTO>> relations;
    /**
     * Constructor.
     */
    public GetRelationsDTO() {
        super();
    }
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
    /**
     * {@inheritDoc}
     */
    @Override
    public ResultFragmentId<GetRelationsDTO> getFragmentId() {
        return ID;
    }
}
