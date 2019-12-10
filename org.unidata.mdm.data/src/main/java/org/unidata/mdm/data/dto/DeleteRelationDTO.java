package org.unidata.mdm.data.dto;

import java.util.List;

import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.dto.PipelineExecutionResult;

/**
 * @author Mikhail Mikhailov
 * Delete relation DTO.
 */
public class DeleteRelationDTO implements RelationDTO, PipelineExecutionResult {
    /**
     * The keys.
     */
    private RelationKeys relationKeys;
    /**
     * Relation name.
     */
    private final String relName;
    /**
     * Relation type.
     */
    private final RelationType type;
    /**
     * list of errors
     */
    private List<ErrorInfoDTO> errors;

    /**
     * Constructor.
     */
    public DeleteRelationDTO(RelationKeys relationKeys, String relName, RelationType relType) {
        super();
        this.relationKeys = relationKeys;
        this.relName = relName;
        this.type = relType;
    }

    /**
     * @return the relationKeys
     */
    @Override
    public RelationKeys getRelationKeys() {
        return relationKeys;
    }

    /**
     * @param relationKeys the relationKeys to set
     */
    public void setRelationKeys(RelationKeys relationKeys) {
        this.relationKeys = relationKeys;
    }

    /**
     * @return the relName
     */
    public String getRelName() {
        return relName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationType getRelationType() {
        return type;
    }

    /**
     * list of errors
     */
    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }
}
