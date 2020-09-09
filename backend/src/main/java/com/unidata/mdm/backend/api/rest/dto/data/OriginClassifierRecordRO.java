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

public class OriginClassifierRecordRO extends NestedRecordRO {

    /**
     * Classifier etalon id.
     */
    private String originId;
    /**
     * Classifier Name
     */
    private String classifierName;
    /**
     * Classifier Node id
     */
    private String classifierNodeId;
    /**
     * Status {PENDING|APPROVED|DECLINED}.
     */
    protected String approval;
    /**
     * Status {ACTIVE|INACTIVE|PENDING|MERGED}.
     */
    private String status;

    public String getClassifierName() {
        return classifierName;
    }

    public void setClassifierName(String classifierName) {
        this.classifierName = classifierName;
    }

    public String getClassifierNodeId() {
        return classifierNodeId;
    }

    public void setClassifierNodeId(String classifierNodeId) {
        this.classifierNodeId = classifierNodeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the etalonId
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setOriginId(String originId) {
        this.originId = originId;
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
}
