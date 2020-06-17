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
package com.unidata.mdm.backend.common.dto;

import java.util.Date;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Timeline contributor DTO.
 */
public class ContributorDTO {

    /**
     * Origin ID.
     */
    private final String originId;
    /**
     * Revision.
     */
    private final int revision;
    /**
     * Source system.
     */
    private final String sourceSystem;
    /**
     * Status.
     */
    private final String status;
    /**
     * Approval.
     */
    private final String approval;
    /**
     * Owner string.
     */
    private final String owner;
    /**
     * Last update date.
     */
    private final Date lastUpdate;
    /**
     * Type name of the contributor.
     */
    private final String typeName;

    /**
     * Constructor.
     * @param originId contributor's origin id
     * @param revision contributor's revision
     * @param sourceSystem contributor's source system
     * @param status contributor's status
     * @param approval contributor's approval state
     * @param owner records creator/owner
     * @param lastUpdate date of the last update
     * @param typeName the name of the type of this contributor
     */
    public ContributorDTO(String originId, int revision, String sourceSystem, String status, String approval, String owner, Date lastUpdate, String typeName) {
        super();
        this.originId = originId;
        this.revision = revision;
        this.sourceSystem = sourceSystem;
        this.status = status;
        this.approval = approval;
        this.owner = owner;
        this.lastUpdate = lastUpdate;
        this.typeName = typeName;
    }


    /**
     * @return the status
     */
    public RecordStatus getStatus() {
        return status == null ? null : RecordStatus.valueOf(status);
    }


    /**
     * @return the approval
     */
    public String getApproval() {
        return approval;
    }


    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }


    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }


    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }



    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }


	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}


    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

}
