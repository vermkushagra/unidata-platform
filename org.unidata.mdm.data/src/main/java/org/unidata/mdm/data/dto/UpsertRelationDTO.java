/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package org.unidata.mdm.data.dto;

import java.util.Date;
import java.util.List;

import org.unidata.mdm.core.dto.ResourceSpecificRightDTO;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 * Upsert relation DTO.
 */
public class UpsertRelationDTO implements RelationDTO, EtalonRelationDTO, PipelineOutput {
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

    // TODO: @Modules
//    /**
//     * Tasks set
//     */
//    private List<WorkflowTaskDTO> tasks;

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
    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
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
        return relationKeys != null ? relationKeys.getEtalonKey().getStatus() : null;
    }

    /**
     * @return the etalonState
     */
    public ApprovalState getEtalonState() {
        return relationKeys != null ? relationKeys.getEtalonKey().getState() : null;
    }

    /**
     * @return the originStatus
     */
    public RecordStatus getOriginStatus() {
        return relationKeys != null ? relationKeys.getOriginKey().getStatus() : null;
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

    // TODO: @Modules
//    /**
//     * @return the tasks
//     */
//    public List<WorkflowTaskDTO> getTasks() {
//        return tasks;
//    }
//
//    /**
//     * @param tasks the tasks to set
//     */
//    public void setTasks(List<WorkflowTaskDTO> tasks) {
//        this.tasks = tasks;
//    }

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
