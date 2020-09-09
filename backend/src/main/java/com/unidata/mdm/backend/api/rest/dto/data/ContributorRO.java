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
package com.unidata.mdm.backend.api.rest.dto.data;

/**
 * @author Mikhail Mikhailov
 * Contributor REST object.
 */
public class ContributorRO {
    /**
     * Origin ID.
     */
    private String originId;
    /**
     * Version.
     */
    private int version;
    /**
     * Source system.
     */
    private String sourceSystem;
    /**
     * Status.
     */
    private String status;
    /**
     * Approval state.
     */
    private String approval;
    /**
     * Owner string.
     */
    private String owner;
    /**
     * Date from.
     */
    // private Date dateFrom;
    /**
     * Date to.
     */
    // private Date dateTo;

    /**
     * Constructor.
     */
    public ContributorRO() {
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
     * @return the version
     */
    public int getVersion() {
        return version;
    }


    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
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

}
