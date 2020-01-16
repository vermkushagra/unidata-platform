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
import org.unidata.mdm.core.po.job.JobPO;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Job row mapper.
 * @author Denis Kostovarov
 */
public class JobRowMapper implements RowMapper<JobPO> {
    @Override
	public JobPO mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		final JobPO job = new JobPO();
		job.setCreatedBy(rs.getString(JobPO.FIELD_CREATED_BY));
		job.setUpdatedBy(rs.getString(JobPO.FIELD_UPDATED_BY));
		job.setCreateDate(rs.getDate(JobPO.FIELD_CREATE_DATE));
		job.setUpdateDate(rs.getDate(JobPO.FIELD_UPDATE_DATE));
		job.setId(rs.getLong(JobPO.FIELD_ID));
		job.setName(rs.getString(JobPO.FIELD_NAME));
		job.setJobNameReference(rs.getString(JobPO.FIELD_JOB_NAME_REFERENCE));
		job.setCronExpression(rs.getString(JobPO.FIELD_CRON_EXPRESSION));
		job.setDescription(rs.getString(JobPO.FIELD_DESCRIPTION));
		job.setEnabled(rs.getBoolean(JobPO.FIELD_ENABLED));
		job.setError(rs.getBoolean(JobPO.FIELD_ERROR));
		Array tags = rs.getArray(JobPO.FIELD_TAGS);
		if (tags != null) {
			job.setTags(Arrays.asList((String[]) tags.getArray()));
		}
		return job;
	}
}
