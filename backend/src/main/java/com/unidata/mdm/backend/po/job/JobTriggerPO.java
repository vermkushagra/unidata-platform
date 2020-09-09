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

package com.unidata.mdm.backend.po.job;

import com.unidata.mdm.backend.po.AbstractPO;

/**
 * Job trigger PO.
 * @author Denis Kostovarov
 */
public class JobTriggerPO extends AbstractPO {
    public static final String TABLE_NAME = "job_trigger";
    public static final String FIELD_ID = "id";
    public static final String FIELD_FINISH_JOB_ID = "finish_job_id";
    public static final String FIELD_START_JOB_ID = "start_job_id";
    public static final String FIELD_SUCCESS_RULE = "success_rule";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";

    private Long id;
    private Long finishJobId;
    private Long startJobId;
    private Boolean successRule;
    private String name;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFinishJobId() {
        return finishJobId;
    }

    public void setFinishJobId(Long finishJobId) {
        this.finishJobId = finishJobId;
    }

    public Long getStartJobId() {
        return startJobId;
    }

    public void setStartJobId(Long startJobId) {
        this.startJobId = startJobId;
    }

    public Boolean getSuccessRule() {
        return successRule;
    }

    public void setSuccessRule(Boolean successRule) {
        this.successRule = successRule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
