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
 * Mass delete result DTO.
 */
public class DeleteRelationsDTO implements RelationsDTO<DeleteRelationDTO>, ResultFragment<DeleteRelationsDTO> {
    /**
     * This fragment ID.
     */
    public static final ResultFragmentId<DeleteRelationsDTO> ID
        = new ResultFragmentId<>("DELETE_RELATIONS_RESULT", DeleteRelationsDTO::new);
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
    public ResultFragmentId<DeleteRelationsDTO> getFragmentId() {
        return ID;
    }
}
