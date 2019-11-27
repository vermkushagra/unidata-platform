/**
 *
 */
package org.unidata.mdm.data.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidata.mdm.system.dto.PipelineExecutionResult;
import org.unidata.mdm.system.dto.ResultFragment;
import org.unidata.mdm.system.dto.ResultFragmentId;

/**
 * @author Mikhail Mikhailov
 *
 */
public class UpsertRelationsDTO implements RelationsDTO<UpsertRelationDTO>, ResultFragment<UpsertRelationsDTO>, PipelineExecutionResult {
    /**
     * This fragment ID.
     */
    public static final ResultFragmentId<UpsertRelationsDTO> ID
        = new ResultFragmentId<>("UPSERT_RELATIONS_RESULT", UpsertRelationsDTO::new);

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
    public ResultFragmentId<UpsertRelationsDTO> getFragmentId() {
        return ID;
    }
}
