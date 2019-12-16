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
 * Mass delete result DTO.
 */
public class DeleteRelationsDTO implements RelationsDTO<DeleteRelationDTO>, OutputFragment<DeleteRelationsDTO>, PipelineOutput {
    /**
     * This fragment ID.
     */
    public static final FragmentId<DeleteRelationsDTO> ID
        = new FragmentId<>("DELETE_RELATIONS_RESULT", DeleteRelationsDTO::new);
    /**
     * Deleted relations.
     */
    private Map<RelationStateDTO, List<DeleteRelationDTO>> relations;
    /**
     * Constructor.
     */
    public DeleteRelationsDTO() {
        super();
    }
    /**
     * Constructor.
     */
    public DeleteRelationsDTO(Map<RelationStateDTO, List<DeleteRelationDTO>> relations) {
        this();
        this.relations = relations;
    }

    /**
     * @return the deleted
     */
    @Override
    public Map<RelationStateDTO, List<DeleteRelationDTO>> getRelations() {
        return relations;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<DeleteRelationsDTO> fragmentId() {
        return ID;
    }
}
