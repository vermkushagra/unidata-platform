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
