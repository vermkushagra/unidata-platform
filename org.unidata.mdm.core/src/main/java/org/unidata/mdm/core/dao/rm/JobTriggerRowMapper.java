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

package org.unidata.mdm.core.dao.rm;

import org.springframework.jdbc.core.RowMapper;
import org.unidata.mdm.core.po.job.JobTriggerPO;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Job row mapper.
 * @author Denis Kostovarov
 */
public class JobTriggerRowMapper implements RowMapper<JobTriggerPO> {
    @Override
    public JobTriggerPO mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final JobTriggerPO jobTrigger = new JobTriggerPO();
        jobTrigger.setCreatedBy(rs.getString(JobTriggerPO.FIELD_CREATED_BY));
        jobTrigger.setUpdatedBy(rs.getString(JobTriggerPO.FIELD_UPDATED_BY));
        jobTrigger.setCreateDate(rs.getDate(JobTriggerPO.FIELD_CREATE_DATE));
        jobTrigger.setUpdateDate(rs.getDate(JobTriggerPO.FIELD_UPDATE_DATE));
        jobTrigger.setId(rs.getLong(JobTriggerPO.FIELD_ID));
        jobTrigger.setFinishJobId(rs.getLong(JobTriggerPO.FIELD_FINISH_JOB_ID));
        jobTrigger.setStartJobId(rs.getLong(JobTriggerPO.FIELD_START_JOB_ID));
        jobTrigger.setSuccessRule(rs.getBoolean(JobTriggerPO.FIELD_SUCCESS_RULE));
        jobTrigger.setName(rs.getString(JobTriggerPO.FIELD_NAME));
        jobTrigger.setDescription(rs.getString(JobTriggerPO.FIELD_DESCRIPTION));

        return jobTrigger;
    }
}
