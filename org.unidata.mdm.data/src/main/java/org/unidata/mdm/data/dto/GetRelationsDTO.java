/**
 *
 */
package org.unidata.mdm.data.dto;

import java.util.List;
import java.util.Map;

import org.unidata.mdm.system.type.pipeline.PipelineOutput;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;

/**
 * @author Mikhail Mikhailov
 * Top level relations DTO.
 */
public class GetRelationsDTO implements RelationsDTO<GetRelationDTO>, OutputFragment<GetRelationsDTO>, PipelineOutput {
    /**
     * This fragment ID.
     */
    public static final FragmentId<GetRelationsDTO> ID
        = new FragmentId<>("GET_RELATIONS_RESULT", GetRelationsDTO::new);
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
    public FragmentId<GetRelationsDTO> fragmentId() {
        return ID;
    }
}
