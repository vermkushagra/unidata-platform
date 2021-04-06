package com.unidata.mdm.backend.dao.rm;

import com.unidata.mdm.backend.po.job.JobParameterPO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Denis Kostovarov
 */
public class JobParameterRowMapper implements RowMapper<JobParameterPO> {
    @Override
    public JobParameterPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        final String name = rs.getString(JobParameterPO.FIELD_NAME);
        final String valStr = rs.getString(JobParameterPO.FIELD_VAL_STRING);
        Long valLong = rs.getLong(JobParameterPO.FIELD_VAL_LONG);
        if (rs.wasNull()) {
            valLong = null;
        }
        Double valDouble = rs.getDouble(JobParameterPO.FIELD_VAL_DOUBLE);
        if (rs.wasNull()) {
            valDouble = null;
        }
        final Timestamp sqlTimestamp = rs.getTimestamp(JobParameterPO.FIELD_VAL_DATE);
        Boolean valBoolean = rs.getBoolean(JobParameterPO.FIELD_VAL_BOOLEAN);
        if (rs.wasNull()) {
            valBoolean = null;
        }
        final long id = rs.getLong(JobParameterPO.FIELD_ID);
        final long jobId = rs.getLong(JobParameterPO.FIELD_JOB_ID);
        final String createdBy = rs.getString(JobParameterPO.FIELD_CREATED_BY);
        final String updatedBy = rs.getString(JobParameterPO.FIELD_UPDATED_BY);
        final Date createDate = rs.getDate(JobParameterPO.FIELD_CREATE_DATE);
        final Date updateDate = rs.getDate(JobParameterPO.FIELD_UPDATE_DATE);

        final JobParameterPO param = validateAndCreateJobParameter(name, valStr, sqlTimestamp, valLong, valDouble, valBoolean);

        if (param != null) {
            param.setCreatedBy(createdBy);
            param.setUpdatedBy(updatedBy);
            param.setCreateDate(createDate);
            param.setUpdateDate(updateDate);
            param.setId(id);
            param.setJobId(jobId);
        }

        return param;
    }

    public static JobParameterPO validateAndCreateJobParameter(final String name, final String valStr,
                                                               final Timestamp sqlTimestamp, final Long valLong,
                                                               final Double valDouble, final Boolean valBoolean) {
        JobParameterPO param = null;
        int identified = 0;
        if (valStr != null) {
            param = new JobParameterPO(name, valStr);
            ++identified;
        }
        if (valLong != null && ++identified == 1) {
            param = new JobParameterPO(name, valLong);
        }
        if (valDouble != null && ++identified == 1) {
            param = new JobParameterPO(name, valDouble);
        }
        if (sqlTimestamp != null && ++identified == 1) {
            final ZonedDateTime zonedTime = ZonedDateTime.ofInstant(sqlTimestamp.toInstant(), ZoneId.systemDefault());
            param = new JobParameterPO(name, zonedTime);
        }
        if (valBoolean != null && ++identified == 1) {
            param = new JobParameterPO(name, valBoolean);
        }

        if (identified > 1) {
            param = null;
        }

        return param;
    }
}
