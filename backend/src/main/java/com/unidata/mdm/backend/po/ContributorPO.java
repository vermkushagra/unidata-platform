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
package com.unidata.mdm.backend.po;

import java.util.Date;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.VistoryOperationType;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ContributorPO {

    /**
     * Origin id.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
    /**
     * Revision number.
     */
    public static final String FIELD_REVISION = "revision";
    /**
     * Origin name.
     */
    public static final String FIELD_SOURCE_SYSTEM = "source_system";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Approval.
     */
    public static final String FIELD_APPROVAL = "approval";
    /**
     * Owner.
     */
    public static final String FIELD_OWNER = "owner";
    /**
     * Create date.
     */
    public static final String FIELD_LAST_UPDATE = "last_update";
    /**
     * Operation type.
     */
    public static final String FIELD_OPERATION_TYPE = "operation_type";
    /**
     * Origin id field.
     */
    private String originId;
    /**
     * Revision number.
     */
    private int revision;
    /**
     * Status of the record.
     */
    private RecordStatus status;
    /**
     * Approval state.
     */
    private ApprovalState approval;
    /**
     * Name of the source system.
     */
    private String sourceSystem;
    /**
     * Owner string.
     */
    private String owner;
    /**
     * Last update (create date of the version record).
     */
    private Date lastUpdate;
    /**
     * Origins vistory OperationType
     */
    private VistoryOperationType operationType;
    /**
     * Constructor.
     */
    public ContributorPO() {
        super();
    }


    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }


    /**
     * @param originId the originId to set
     */
    public void setOriginId(String originId) {
        this.originId = originId;
    }


    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }


    /**
     * @param revision the revision to set
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }


    /**
     * @return the status
     */
    public RecordStatus getStatus() {
        return status;
    }


    /**
     * @param status the status to set
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
    }



    /**
     * @return the approval
     */
    public ApprovalState getApproval() {
        return approval;
    }



    /**
     * @param approval the approval to set
     */
    public void setApproval(ApprovalState approval) {
        this.approval = approval;
    }


    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }


    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }



    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }



    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }


	/**
	 * @return the createDate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}


	/**
	 * @param createDate the createDate to set
	 */
	public void setLastUpdate(Date createDate) {
		this.lastUpdate = createDate;
	}


	/**
     * @return the VistoryOperationType
     */
    public VistoryOperationType getOperationType() {
        return operationType;
    }

    /**
     * @param operationType
     */
    public void setOperationType(VistoryOperationType operationType) {
        this.operationType = operationType;
    }

}
