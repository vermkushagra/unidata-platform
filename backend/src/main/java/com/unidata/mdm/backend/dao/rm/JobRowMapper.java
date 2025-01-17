package com.unidata.mdm.backend.dao.rm;

import com.unidata.mdm.backend.po.job.JobPO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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

        return job;
    }
}
