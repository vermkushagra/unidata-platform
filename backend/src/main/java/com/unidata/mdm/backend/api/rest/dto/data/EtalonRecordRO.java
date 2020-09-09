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

package com.unidata.mdm.backend.api.rest.dto.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.security.ResourceSpecificRightRO;
import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowTaskRO;

/**
 * @author Michael Yashin. Created on 02.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtalonRecordRO extends NestedRecordRO {

    /**
     * List of classifier records.
     */
    private List<EtalonClassifierRecordRO> classifiers = new ArrayList<>();
    /**
     * List of code attributes.
     */
    private List<CodeAttributeRO> codeAttributes = new ArrayList<>();
    /**
     * Entity key.
     */
    protected String etalonId;
    /**
     * Is record modified? (it's needed for restore functionality)
     */
    protected boolean modified;
    /**
     * Entity version.
     */
    protected int version;
    /**
     * Entity name.
     */
    protected String entityName;
    /**
     * Entity type.
     */
    protected String entityType;
    /**
     * Duplicate ids.
     */
    protected List<String> duplicateIds;
    /**
     * Optional validity range start date.
     */
    protected LocalDateTime validFrom;
    /**
     * Optional validity range end date.
     */
    protected LocalDateTime validTo;
    /**
     * Status {ACTIVE|INACTIVE|PENDING|MERGED}.
     */
    protected String status;
    /**
     * Status {PENDING|APPROVED|DECLINED}.
     */
    protected String approval;
    /**
     * Global sequence number.
     */
    protected String gsn;
    /**
     * Pending tasks.
     */
    protected List<WorkflowTaskRO> workflowState = new ArrayList<>();
    /**
     * Create date of the origin (source definition) record.
     */
    @JsonFormat(timezone = "DEFAULT_TIMEZONE")
    protected Date createDate;
    /**
     * Created by.
     */
    protected String createdBy;
    /**
     * Update date of the origin version record.
     */
    @JsonFormat(timezone = "DEFAULT_TIMEZONE")
    protected Date updateDate;
    /**
     * Updated by.
     */
    protected String updatedBy;
    /**
     * Rights.
     */
    protected ResourceSpecificRightRO rights;
    /**
     * Diff table.
     */
    protected List<DiffToPreviousAttributeRO> diffToDraft;
    /**
     * @return the validFrom
     */
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public LocalDateTime getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * @return the approval
     */
    public String getApproval() {
        return approval;
    }


    /**
     * @param approval the approval to set
     */
    public void setApproval(String approval) {
        this.approval = approval;
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param entityKey the entity key to set
     */
    public void setEtalonId(String entityKey) {
        this.etalonId = entityKey;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param entityVersion the version to set
     */
    public void setVersion(int entityVersion) {
        this.version = entityVersion;
    }

    /**
     * @return the duplicateIds
     */
    public List<String> getDuplicateIds() {
        return duplicateIds;
    }

    /**
     * @param duplicateIds the duplicateIds to set
     */
    public void setDuplicateIds(List<String> duplicateIds) {
        this.duplicateIds = duplicateIds;
    }
    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isModified() {
        return modified;
    }


    public void setModified(boolean modified) {
        this.modified = modified;
    }


    /**
	 * @return the entityType
	 */
	public String getEntityType() {
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	/**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return the updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }


    /**
     * @return the pendingTasks
     */
    public List<WorkflowTaskRO> getWorkflowState() {
        return workflowState;
    }


    /**
     * @param pendingTasks the pendingTasks to set
     */
    public void setWorkflowState(List<WorkflowTaskRO> pendingTasks) {
        this.workflowState = pendingTasks;
    }

    public List<EtalonClassifierRecordRO> getClassifiers() {
        return classifiers;
    }

    public void setClassifiers(List<EtalonClassifierRecordRO> classifiers) {
        this.classifiers = classifiers;
    }

    /**
     * @return the rights
     */
    public ResourceSpecificRightRO getRights() {
        return rights;
    }

    /**
     * @param rights the rights to set
     */
    public void setRights(ResourceSpecificRightRO rights) {
        this.rights = rights;
    }

    /**
     * @return the codeAttributes
     */
    public List<CodeAttributeRO> getCodeAttributes() {
        return codeAttributes;
    }

    /**
     * @param codeAttributes the codeAttributes to set
     */
    public void setCodeAttributes(List<CodeAttributeRO> codeAttributes) {
        this.codeAttributes = codeAttributes;
    }

    /**
     * @return gsn.
     */
    public String getGsn() {
        return gsn;
    }
    /**
     * @param gsn the gsn to set
     */
    public void setGsn(String gsn) {
        this.gsn = gsn;
    }

    /**
     * @return the diffTable
     */
    public List<DiffToPreviousAttributeRO> getDiffToDraft() {
        return diffToDraft;
    }

    /**
     * @param diffTable the diffTable to set
     */
    public void setDiffToDraft(List<DiffToPreviousAttributeRO> diffTable) {
        this.diffToDraft = diffTable;
    }
}
