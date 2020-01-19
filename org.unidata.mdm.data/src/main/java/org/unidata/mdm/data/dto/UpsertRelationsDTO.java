/**
 *
 */
package org.unidata.mdm.data.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidata.mdm.system.dto.ExecutionResult;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;

/**
 * @author Mikhail Mikhailov
 *
 */
public class UpsertRelationsDTO implements RelationsDTO<UpsertRelationDTO>, OutputFragment<UpsertRelationsDTO>, PipelineOutput, ExecutionResult {
    /**
     * This fragment ID.
     */
    public static final FragmentId<UpsertRelationsDTO> ID
        = new FragmentId<>("UPSERT_RELATIONS_RESULT", UpsertRelationsDTO::new);
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
     * Constructor.
     */
    public UpsertRelationsDTO() {
        super();
        this.relations = new HashMap<>();
    }
    /**
     * @return the relations
     */
    @Override
    public Map<RelationStateDTO, List<UpsertRelationDTO>> getRelations() {
        return relations;
    }

    @Override
    public FragmentId<UpsertRelationsDTO> fragmentId() {
        return ID;
    }
}
