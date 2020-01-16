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

import org.unidata.mdm.core.dto.ResourceSpecificRightDTO;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 * Single relation version DTO.
 */
public class GetRelationDTO implements RelationDTO, EtalonRelationDTO, PipelineOutput {
    /**
     * The keys.
     */
    private RelationKeys relationKeys;
    /**
     * Relation name.
     */
    private String relName;
    /**
     * Relation relType.
     */
    private RelationType relType;
    /**
     * Relation object, either
     */
    private EtalonRelation etalon;

    private Boolean periodActive;

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
     * Constructor.
     */
    public GetRelationDTO(RelationKeys relationKeys, String relationName, RelationType type) {
        super();
        this.relationKeys = relationKeys;
        this.relName = relationName;
        this.relType = type;
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
     * @param relName the relName to set
     */
    public void setRelName(String relName) {
        this.relName = relName;
    }
    /**
     * Gets the relation object.
     * @return relation object
     */
    @Override
    public EtalonRelation getEtalon() {
        return etalon;
    }
    /**
     * @param etalon the etalon to set
     */
    public void setEtalon(EtalonRelation etalon) {
        this.etalon = etalon;
    }
    /**
     * @return the relType
     */
    @Override
    public RelationType getRelationType() {
        return relType;
    }

    // TODO: @Modules
//    /**
//     * @return the tasks
//     */
//    public List<WorkflowTaskDTO> getTasks() {
//        return tasks;
//    }
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


    public Boolean getPeriodActive() {
        return periodActive;
    }

    public void setPeriodActive(Boolean periodActive) {
        this.periodActive = periodActive;
    }
}
