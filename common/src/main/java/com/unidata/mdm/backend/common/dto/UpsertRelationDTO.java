/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.dto.security.ResourceSpecificRightDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTaskDTO;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.UpsertAction;

/**
 * @author Mikhail Mikhailov
 * Upsert relation DTO.
 */
public class UpsertRelationDTO implements RelationDTO, EtalonRelationDTO {
    /**
     * Relation keys.
     */
    private RelationKeys relationKeys;
    /**
     * Relation name.
     */
    private String relName;
    /**
     * Relation type.
     */
    private RelationType relationType;
    /**
     * The relation itself.
     */
    private EtalonRelation etalon;
    /**
     * Period from date.
     */
    private Date validFrom;
    /**
     * Period to date.
     */
    private Date validTo;
    /**
     * Tasks set
     */
    private List<WorkflowTaskDTO> tasks;
    /**
     * Rights.
     */
    private ResourceSpecificRightDTO rights;
    /**
     * Actual action.
     */
    private UpsertAction action;
    /**
     * list of errors
     */
    private List<ErrorInfoDTO> errors;
    /**
     * Constructor.
     */
    public UpsertRelationDTO() {
        super();
    }
    /**
     * Constructor.
     */
    public UpsertRelationDTO(RelationKeys relationKeys, String relationName, RelationType type) {
        super();
        this.relationKeys = relationKeys;
        this.relName = relationName;
        this.relationType = type;
    }

    /**
     * @return the etalonKey
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
     * @param relName the relName to set
     */
    public void setRelName(String relName) {
        this.relName = relName;
    }

    /**
     * @return the relationType
     */
    @Override
    public RelationType getRelationType() {
        return relationType;
    }

    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(RelationType relType) {
        this.relationType = relType;
    }

    /**
     * @return the relation
     */
    @Override
    public EtalonRelation getEtalon() {
        return etalon;
    }

    /**
     * @param relation the relation to set
     */
    public void setEtalon(EtalonRelation relation) {
        this.etalon = relation;
    }

    /**
     * @return the etalonStatus
     */
    public RecordStatus getEtalonStatus() {
        return relationKeys != null ? relationKeys.getEtalonStatus() : null;
    }

    /**
     * @return the etalonState
     */
    public ApprovalState getEtalonState() {
        return relationKeys != null ? relationKeys.getEtalonState() : null;
    }

    /**
     * @return the originStatus
     */
    public RecordStatus getOriginStatus() {
        return relationKeys != null ? relationKeys.getOriginStatus() : null;
    }

    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * @return the tasks
     */
    public List<WorkflowTaskDTO> getTasks() {
        return tasks;
    }

    /**
     * @param tasks the tasks to set
     */
    public void setTasks(List<WorkflowTaskDTO> tasks) {
        this.tasks = tasks;
    }

    /**
     * @return the rights
     */
    public ResourceSpecificRightDTO getRights() {
        return rights;
    }

    /**
     * @param rights the rights to set
     */
    public void setRights(ResourceSpecificRightDTO rights) {
        this.rights = rights;
    }

    /**
     * @return the action
     */
    public UpsertAction getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(UpsertAction action) {
        this.action = action;
    }


    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }
}
