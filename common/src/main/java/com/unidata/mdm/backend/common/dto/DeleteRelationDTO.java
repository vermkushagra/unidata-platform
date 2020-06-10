package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.RelationType;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Delete relation DTO.
 */
public class DeleteRelationDTO implements RelationDTO {
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
