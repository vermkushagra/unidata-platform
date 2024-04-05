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

package com.unidata.mdm.backend.dao.impl;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.dao.JobDao;
import com.unidata.mdm.backend.dao.rm.JobParameterRowMapper;
import com.unidata.mdm.backend.dao.rm.JobRowMapper;
import com.unidata.mdm.backend.dao.rm.JobTriggerRowMapper;
import com.unidata.mdm.backend.dto.job.JobFilter;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplateImpl;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplateImpl;
import com.unidata.mdm.backend.po.job.JobBatchJobInstancePO;
import com.unidata.mdm.backend.po.job.JobPO;
import com.unidata.mdm.backend.po.job.JobParameterPO;
import com.unidata.mdm.backend.po.job.JobTriggerPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * JobDao implementation
 *
 * @author Denis Kostovarov
 */
@Repository
public class JobDaoImpl implements JobDao {
    private final String SELECT_ALL_JOBS;
    private final String SELECT_ALL_JOBS_AND_PARAMS;
    private final String SELECT_JOBS_COUNT_BY_FILTER;
    private final String SEARCH_JOBS_BY_FILTER;
    private final String SELECT_BY_JOB_NAME;
    private final String SELECT_BY_JOB_ID;
    private final String SELECT_BY_JOB_ID_WITH_PARAMETERS;
    private final String SELECT_JOB_PARAMETERS_BY_JOB_ID;
    private final String SELECT_JOB_PARAMETERS_BY_JOB_IDS;
    private final String UPDATE_JOB_BY_ID;
    private final String UPDATE_JOB_ENABLED_BY_ID;
    private final String UPDATE_JOB_ERROR_BY_IDS;
    private final String SELECT_JOB_INSTANCES_BY_JOB_IDS;
    private final String SELECT_JOB_LAST_EXECUTION;
    private final String SELECT_JOB_CHECK_PARAMS;
    private final String SELECT_JOBS_BY_REF_NAME;
    private final String DELETE_JOB_PARAMS;
    private final String DELETE_REMOVED_JOB_PARAMS;
    private final String DELETE_BATCH_JOB_INSTANCE;
    private final String DELETE_JOB;
    private final String UPDATE_JOB_TRIGGER_BY_ID;
    private final String SELECT_JOB_TRIGGERS_BY_ID;
    private final String SELECT_JOB_JOBS_BY_ID_AND_TRIGGER_RULE;
    private final String SELECT_TRIGGER_BY_JOB_ID_TRIGGER_ID;
    private final String DELETE_JOB_TRIGGER;
    private final String SELECT_TRIGGER_BY_TRIGGER_NAME;
    private final String DELETE_JOB_TRIGGERS;

    private final String upsertJobParameter;
    private final String selectJobsByIdsWithParams;
    private final String selectJobsWithParams;

    private UnidataJdbcTemplate jdbcTemplate;
    private UnidataNamedParameterJdbcTemplate namedJdbcTemplate;

    private SimpleJdbcInsert jobTemplateInsertActor;
    private SimpleJdbcInsert jobParametersInsertActor;
    private SimpleJdbcInsert jobBatchJobInstanceInsertActor;
    private SimpleJdbcInsert jobTriggerTemplateInsertActor;

    @Autowired
    private DaoHelper daoHelper;

    @Autowired
    public JobDaoImpl(final DataSource dataSource, @Qualifier("job-sql") final Properties sql) {
        SELECT_ALL_JOBS = sql.getProperty("SELECT_ALL_JOBS");
        SELECT_ALL_JOBS_AND_PARAMS = sql.getProperty("SELECT_ALL_JOBS_AND_PARAMS");
        SELECT_JOBS_COUNT_BY_FILTER = sql.getProperty("SELECT_JOBS_COUNT_BY_FILTER");
        SEARCH_JOBS_BY_FILTER = sql.getProperty("SEARCH_JOBS_BY_FILTER");
        SELECT_BY_JOB_NAME = sql.getProperty("SELECT_BY_JOB_NAME");
        SELECT_BY_JOB_ID = sql.getProperty("SELECT_BY_JOB_ID");
        SELECT_BY_JOB_ID_WITH_PARAMETERS = sql.getProperty("SELECT_BY_JOB_ID_WITH_PARAMETERS");
        SELECT_JOB_PARAMETERS_BY_JOB_ID = sql.getProperty("SELECT_JOB_PARAMETERS_BY_JOB_ID");
        SELECT_JOB_PARAMETERS_BY_JOB_IDS = sql.getProperty("SELECT_JOB_PARAMETERS_BY_JOB_IDS");
        UPDATE_JOB_BY_ID = sql.getProperty("UPDATE_JOB_BY_ID");
        UPDATE_JOB_ENABLED_BY_ID = sql.getProperty("UPDATE_JOB_ENABLED_BY_ID");
        UPDATE_JOB_ERROR_BY_IDS = sql.getProperty("UPDATE_JOB_ERROR_BY_IDS");
        SELECT_JOB_INSTANCES_BY_JOB_IDS = sql.getProperty("SELECT_JOB_INSTANCES_BY_JOB_IDS");
        SELECT_JOB_LAST_EXECUTION = sql.getProperty("SELECT_JOB_LAST_EXECUTION");
        SELECT_JOB_CHECK_PARAMS = sql.getProperty("SELECT_JOB_CHECK_PARAMS");
        SELECT_JOBS_BY_REF_NAME = sql.getProperty("SELECT_JOBS_BY_REF_NAME");
        DELETE_JOB_PARAMS = sql.getProperty("DELETE_JOB_PARAMS");
        DELETE_REMOVED_JOB_PARAMS = sql.getProperty("DELETE_REMOVED_JOB_PARAMS");
        DELETE_BATCH_JOB_INSTANCE = sql.getProperty("DELETE_BATCH_JOB_INSTANCE");
        DELETE_JOB = sql.getProperty("DELETE_JOB");
        UPDATE_JOB_TRIGGER_BY_ID = sql.getProperty("UPDATE_JOB_TRIGGER_BY_ID");
        SELECT_JOB_TRIGGERS_BY_ID = sql.getProperty("SELECT_JOB_TRIGGERS_BY_ID");
        SELECT_JOB_JOBS_BY_ID_AND_TRIGGER_RULE = sql.getProperty("SELECT_JOB_JOBS_BY_ID_AND_TRIGGER_RULE");
        SELECT_TRIGGER_BY_JOB_ID_TRIGGER_ID = sql.getProperty("SELECT_TRIGGER_BY_JOB_ID_TRIGGER_ID");
        DELETE_JOB_TRIGGER = sql.getProperty("DELETE_JOB_TRIGGER");
        SELECT_TRIGGER_BY_TRIGGER_NAME = sql.getProperty("SELECT_TRIGGER_BY_TRIGGER_NAME");
        DELETE_JOB_TRIGGERS = sql.getProperty("DELETE_JOB_TRIGGERS");

        upsertJobParameter = sql.getProperty("UPSERT_JOB_PARAMETER");
        selectJobsByIdsWithParams = sql.getProperty("SELECT_JOBS_BY_IDS_WITH_PARAMS");
        selectJobsWithParams = sql.getProperty("SELECT_ALL_JOBS_WITH_PARAMS");

        jdbcTemplate = new UnidataJdbcTemplateImpl(dataSource);
        namedJdbcTemplate = new UnidataNamedParameterJdbcTemplateImpl(dataSource);

        jobTemplateInsertActor = new SimpleJdbcInsert(dataSource)
                .withTableName(JobPO.TABLE_NAME)
                .usingColumns(JobPO.FIELD_CREATE_DATE, JobPO.FIELD_CREATED_BY, JobPO.FIELD_NAME,
                        JobPO.FIELD_ENABLED, JobPO.FIELD_CRON_EXPRESSION, JobPO.FIELD_JOB_NAME_REFERENCE,
                        JobPO.FIELD_DESCRIPTION)
                .usingGeneratedKeyColumns(JobPO.FIELD_ID);

        jobParametersInsertActor = new SimpleJdbcInsert(dataSource)
                .withTableName(JobParameterPO.TABLE_NAME)
                .usingColumns(JobParameterPO.FIELD_CREATE_DATE, JobParameterPO.FIELD_CREATED_BY,
                        JobParameterPO.FIELD_JOB_ID, JobParameterPO.FIELD_NAME, JobParameterPO.FIELD_VAL_STRING,
                        JobParameterPO.FIELD_VAL_DATE, JobParameterPO.FIELD_VAL_LONG, JobParameterPO.FIELD_VAL_DOUBLE,
                        JobParameterPO.FIELD_VAL_BOOLEAN)
                .usingGeneratedKeyColumns(JobParameterPO.FIELD_ID);

        jobBatchJobInstanceInsertActor = new SimpleJdbcInsert(dataSource)
                .withTableName(JobBatchJobInstancePO.TABLE_NAME)
                .usingColumns(JobBatchJobInstancePO.FIELD_CREATE_DATE, JobBatchJobInstancePO.FIELD_CREATED_BY,
                        JobBatchJobInstancePO.FIELD_JOB_ID, JobBatchJobInstancePO.FIELD_JOB_INSTANCE_ID);

        jobTriggerTemplateInsertActor = new SimpleJdbcInsert(dataSource)
                .withTableName(JobTriggerPO.TABLE_NAME)
                .usingColumns(JobTriggerPO.FIELD_CREATE_DATE, JobTriggerPO.FIELD_CREATED_BY,
                        JobTriggerPO.FIELD_FINISH_JOB_ID, JobTriggerPO.FIELD_START_JOB_ID,
                        JobTriggerPO.FIELD_SUCCESS_RULE, JobTriggerPO.FIELD_NAME, JobTriggerPO.FIELD_DESCRIPTION)
                .usingGeneratedKeyColumns(JobTriggerPO.FIELD_ID);;
    }

    @Override
    public List<JobPO> getJobs() {
        return jdbcTemplate.query(SELECT_ALL_JOBS, new JobRowMapper());
    }

    @Override
    public int getJobsCount(JobFilter filter) {
        Map<String, Object> params = new HashMap<>();

        if (filter.getEnabled() == null) {
            params.put("activeFilterEnabled", 0);
            params.put("enabled", filter.getEnabled());
        } else {
            params.put("activeFilterEnabled", 1);
            params.put("enabled", filter.getEnabled());
        }

        return namedJdbcTemplate.queryForObject(SELECT_JOBS_COUNT_BY_FILTER, params, Integer.class);
    }

    @Override
    public List<JobPO> searchJobs(JobFilter filter) {
        Map<String, Object> params = new HashMap<>();

        params.put("limit", filter.getItemCount());
        params.put("offset", filter.getFromInd());

        if (filter.getEnabled() == null) {
            params.put("activeFilterEnabled", 0);
            params.put("enabled", filter.getEnabled());
        } else {
            params.put("activeFilterEnabled", 1);
            params.put("enabled", filter.getEnabled());
        }

        return namedJdbcTemplate.query(SEARCH_JOBS_BY_FILTER, params, new JobRowMapper());
    }

    @Override
    public List<JobPO> getJobsWithParameters() {
        final Map<Long, JobPO> jobs = new HashMap<>();
        jdbcTemplate.query(SELECT_ALL_JOBS_AND_PARAMS, rs -> {
            while (rs.next()) {
                JobPO j = jobs.get(rs.getLong("j_" + JobPO.FIELD_ID));
                if (j == null) {
                    j = extractResultFromRs(rs);

                    jobs.put(j.getId(), j);
                }

                final JobParameterPO jobParameterPO = extractJobParameter(rs);
                if (jobParameterPO != null) {
                    j.addParameter(jobParameterPO);
                }
            }
            return null;
        });

        return new ArrayList<>(jobs.values());
    }

    @Override
    public JobPO findJob(long jobId) {
        final List<JobPO> jobs = namedJdbcTemplate.query(SELECT_BY_JOB_ID,
                Collections.singletonMap(JobPO.FIELD_ID, jobId), new JobRowMapper());
        if (jobs.size() == 1) {
            return jobs.get(0);
        }

        return null;
    }

    @Override
    public JobPO findJobWithParameters(long jobId) {
        final JobPO job =  namedJdbcTemplate.query(
                SELECT_BY_JOB_ID_WITH_PARAMETERS,
                Collections.singletonMap(JobPO.FIELD_ID, jobId),
                rs -> {
                    if (!rs.next()) {
                        return null;
                    }

                    final JobPO result = extractResultFromRs(rs);
                    do {
                        final JobParameterPO jobParameterPO = extractJobParameter(rs);
                        if (jobParameterPO != null) {
                            result.addParameter(jobParameterPO);
                        }
                    }
                    while (rs.next());

                    return result;
                }
        );

        return job;
    }

    private JobPO extractResultFromRs(ResultSet rs) throws SQLException {
        final JobPO result = new JobPO();
        result.setId(rs.getLong("j_" + JobPO.FIELD_ID));
        result.setName(rs.getString("j_" + JobPO.FIELD_NAME));
        result.setEnabled(rs.getBoolean("j_" + JobPO.FIELD_ENABLED));
        result.setError(rs.getBoolean("j_" + JobPO.FIELD_ERROR));
        result.setCreatedBy(rs.getString("j_" + JobPO.FIELD_CREATED_BY));
        result.setUpdatedBy(rs.getString("j_" + JobPO.FIELD_UPDATED_BY));
        result.setCreateDate(rs.getTime("j_" + JobPO.FIELD_CREATE_DATE));
        result.setUpdateDate(rs.getTime("j_" + JobPO.FIELD_UPDATE_DATE));
        result.setDescription(rs.getString("j_" + JobPO.FIELD_DESCRIPTION));
        result.setCronExpression(rs.getString("j_" + JobPO.FIELD_CRON_EXPRESSION));
        result.setJobNameReference(rs.getString("j_" + JobPO.FIELD_JOB_NAME_REFERENCE));
        return result;
    }

    private JobParameterPO extractJobParameter(ResultSet rs) throws SQLException {
        final long paramId = rs.getLong("p_" + JobParameterPO.FIELD_ID);
        if (!rs.wasNull()) {
            final String name = rs.getString("p_" + JobParameterPO.FIELD_NAME);
            final String valString = rs.getString("p_" + JobParameterPO.FIELD_VAL_STRING);
            final Timestamp valDate = rs.getTimestamp("p_" + JobParameterPO.FIELD_VAL_DATE);
            Long valLong = rs.getLong("p_" + JobParameterPO.FIELD_VAL_LONG);
            if (rs.wasNull()) {
                valLong = null;
            }
            Double valDouble = rs.getDouble("p_" + JobParameterPO.FIELD_VAL_DOUBLE);
            if (rs.wasNull()) {
                valDouble = null;
            }
            Boolean valBoolean = rs.getBoolean("p_" + JobParameterPO.FIELD_VAL_BOOLEAN);
            if (rs.wasNull()) {
                valBoolean = null;
            }

            String[] valStringArr = null;
            Array stringArr = rs.getArray("p_" + JobParameterPO.FIELD_VAL_ARR_STRING);
            if (stringArr != null) {
                valStringArr = (String[])stringArr.getArray();
            }

            Timestamp[] valDateArr = null;
            Array dateArr = rs.getArray("p_" + JobParameterPO.FIELD_VAL_ARR_DATE);
            if (dateArr != null) {
                valDateArr = (Timestamp[])dateArr.getArray();
            }

            Long[] valLongArr = null;
            Array longArr = rs.getArray("p_" + JobParameterPO.FIELD_VAL_ARR_LONG);
            if (longArr != null) {
                valLongArr = (Long[])longArr.getArray();
            }

            Double[] valDoubleArr = null;
            Array doubleArr = rs.getArray("p_" + JobParameterPO.FIELD_VAL_ARR_DOUBLE);
            if (doubleArr != null) {
                valDoubleArr = (Double[])doubleArr.getArray();
            }

            Boolean[] valBooleanArr = null;
            Array booleanArr = rs.getArray("p_" + JobParameterPO.FIELD_VAL_ARR_BOOLEAN);
            if (booleanArr != null) {
                valBooleanArr = (Boolean[])booleanArr.getArray();
            }

            final JobParameterPO p = JobParameterRowMapper.validateAndCreateJobParameter(name,
                    valString, valDate, valLong, valDouble, valBoolean,
                    valStringArr, valDateArr, valLongArr, valDoubleArr, valBooleanArr);
            if (p != null) {
                p.setId(paramId);
                p.setJobId(rs.getLong("j_" + JobPO.FIELD_ID));
                p.setCreatedBy(rs.getString("p_" + JobParameterPO.FIELD_CREATED_BY));
                p.setUpdatedBy(rs.getString("p_" + JobParameterPO.FIELD_UPDATED_BY));
                p.setCreateDate(rs.getTime("p_" + JobParameterPO.FIELD_CREATE_DATE));
                p.setUpdateDate(rs.getTime("p_" + JobParameterPO.FIELD_UPDATE_DATE));
                return p;
            }
        }
        return null;
    }

    @Override
    public JobPO findJob(final String jobName) {
        final List<JobPO> jobs = namedJdbcTemplate.query(SELECT_BY_JOB_NAME,
                Collections.singletonMap(JobPO.FIELD_NAME, jobName), new JobRowMapper());
        if (jobs.size() == 1) {
            return jobs.get(0);
        }

        return null;
    }

    @Override
    public List<JobParameterPO> getJobParameters(final long jobId) {
        return namedJdbcTemplate.query(SELECT_JOB_PARAMETERS_BY_JOB_ID,
                Collections.singletonMap(JobParameterPO.FIELD_JOB_ID, jobId),
                new JobParameterRowMapper());
    }

    @Override
    public Map<Long, List<JobParameterPO>> getJobsParameters(final List<Long> jobIds) {
        final Map<Long, List<JobParameterPO>> result = new HashMap<>();

        if (!CollectionUtils.isEmpty(jobIds)) {
            final List<JobParameterPO> params = namedJdbcTemplate.query(SELECT_JOB_PARAMETERS_BY_JOB_IDS,
                    Collections.singletonMap("ids", jobIds), new JobParameterRowMapper());
            if (!CollectionUtils.isEmpty(params)) {
                for (final JobParameterPO p : params) {
                    List<JobParameterPO> jobParams = result.get(p.getJobId());
                    if (jobParams == null) {
                        jobParams = new ArrayList<>();
                        result.put(p.getJobId(), jobParams);
                    }
                    jobParams.add(p);
                }
            }
        }

        return result;
    }

    @Override
    public long insertJob(final JobPO newJob) {
        final Map<String, Object> params = new HashMap<>();
        params.put(JobPO.FIELD_CREATED_BY, newJob.getCreatedBy());
        params.put(JobPO.FIELD_CREATE_DATE, newJob.getCreateDate());
        params.put(JobPO.FIELD_NAME, newJob.getName());
        params.put(JobPO.FIELD_CRON_EXPRESSION, newJob.getCronExpression());
        params.put(JobPO.FIELD_DESCRIPTION, newJob.getDescription());
        params.put(JobPO.FIELD_JOB_NAME_REFERENCE, newJob.getJobNameReference());
        params.put(JobPO.FIELD_ENABLED, newJob.isEnabled());

        final Number jobId = jobTemplateInsertActor.executeAndReturnKey(params);

        newJob.setId(jobId.longValue());
        upsertParameters(newJob);

        newJob.setParameters(getJobParameters(newJob.getId()));

        return newJob.getId();
    }

    @Override
    public boolean updateJob(final JobPO job) {
        Assert.notNull(job);
        Assert.notNull(job.getUpdateDate());
        Assert.notNull(job.getUpdatedBy());

        int updatesCount = jdbcTemplate.execute(UPDATE_JOB_BY_ID, ps -> {
            ps.setString(1, job.getName());
            ps.setTimestamp(2, new Timestamp(job.getUpdateDate().getTime()));
            ps.setString(3, job.getUpdatedBy());
            ps.setBoolean(4, job.isEnabled());
            ps.setString(5, job.getCronExpression());
            ps.setString(6, job.getDescription());
            ps.setString(7, job.getJobNameReference());
            ps.setLong(8, job.getId());
            return ps.executeUpdate();
        });

        if (updatesCount == 0) {
            throw new DataProcessingException("Failed to update job by ID: " + job.getId(),
                    ExceptionId.EX_JOB_UPDATE_ERROR, job);
        }

        if (CollectionUtils.isEmpty(job.getParameters())) {
            // delete all job parameters
            namedJdbcTemplate.execute(DELETE_JOB_PARAMS,
                    Collections.singletonMap(JobParameterPO.FIELD_JOB_ID, job.getId()),
                    PreparedStatement::executeUpdate);
        } else {
            // delete removed parameters
            deleteRemovedParameters(job);

            upsertParameters(job);

            job.setParameters(getJobParameters(job.getId()));
        }

        return updatesCount == 1;
    }

    private void upsertParameters(final JobPO job) {
        try (Connection con = jdbcTemplate.getDataSource().getConnection()) {
            List<SqlParameterSource> parameterSources = new ArrayList<>();
            for (JobParameterPO record : job.getParameters()) {
                MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                        .addValue(JobParameterPO.FIELD_JOB_ID, job.getId())
                        .addValue(JobParameterPO.FIELD_NAME, record.getName())
                        .addValue(
                                JobParameterPO.FIELD_CREATE_DATE,
                                record.getCreateDate() != null ? record.getCreateDate() : new Date()
                        )
                        .addValue(
                                JobParameterPO.FIELD_CREATED_BY,
                                record.getCreatedBy() != null ? record.getCreatedBy() : SecurityUtils.getCurrentUserName()
                        )
                        .addValue(JobParameterPO.FIELD_UPDATE_DATE, record.getUpdateDate())
                        .addValue(JobParameterPO.FIELD_UPDATED_BY, record.getUpdatedBy())
                        .addValue(JobParameterPO.FIELD_VAL_STRING, record.getStringValue())
                        .addValue(JobParameterPO.FIELD_VAL_DATE, record.getDateValue())
                        .addValue(JobParameterPO.FIELD_VAL_LONG, record.getLongValue())
                        .addValue(JobParameterPO.FIELD_VAL_DOUBLE, record.getDoubleValue())
                        .addValue(JobParameterPO.FIELD_VAL_BOOLEAN, record.getBooleanValue())

                        .addValue(JobParameterPO.FIELD_VAL_ARR_STRING, record.getStringArrayValue() == null ?
                                null : con.createArrayOf("text", record.getStringArrayValue()))

                        .addValue(JobParameterPO.FIELD_VAL_ARR_DATE, record.getDateArrayValue() == null ?
                                null :
                                con.createArrayOf("timestamp",
                                        convertZonedDateTime(record.getDateArrayValue())))

                        .addValue(JobParameterPO.FIELD_VAL_ARR_LONG, record.getLongArrayValue() == null ?
                                null : con.createArrayOf("bigint", record.getLongArrayValue()))

                        .addValue(JobParameterPO.FIELD_VAL_ARR_DOUBLE, record.getDoubleArrayValue() == null ?
                                null : con.createArrayOf("double", record.getDoubleArrayValue()))

                        .addValue(JobParameterPO.FIELD_VAL_ARR_BOOLEAN, record.getBooleanArrayValue() == null ?
                                null : con.createArrayOf("boolean", record.getBooleanArrayValue()));
                parameterSources.add(parameterSource);
            }
            SqlParameterSource[] batchArgs = parameterSources.toArray(new SqlParameterSource[parameterSources.size()]);

            namedJdbcTemplate.batchUpdate(upsertJobParameter, batchArgs);
        } catch (SQLException e) {
            throw new JobException("Failed to prepare parameters for save job with id: " + job.getId(), e,
                    ExceptionId.EX_JOB_PARAMETER_PREPARE_UPSERT_ERROR, job.getId());
        }
    }

    private java.sql.Timestamp[] convertZonedDateTime(ZonedDateTime[] dateTimes) {
        java.sql.Timestamp[] result = new Timestamp[dateTimes.length];

        for (int i = 0; i < dateTimes.length; i++) {
            result[i] = Timestamp.from(dateTimes[i].toInstant());
        }

        return result;
    }

    @Override
    public boolean removeJob(long jobId) {
        namedJdbcTemplate.execute(DELETE_JOB_PARAMS, Collections.singletonMap("job_id", jobId), PreparedStatement::executeUpdate);

        jdbcTemplate.update(DELETE_BATCH_JOB_INSTANCE, jobId);

        jdbcTemplate.update(DELETE_JOB_TRIGGERS, jobId, jobId);

        int jobAffectedRows = jdbcTemplate.update(DELETE_JOB, jobId);

        if (jobAffectedRows != 1) {
            throw new JobException("Failed to delete job with id: " + jobId, ExceptionId.EX_JOB_DELETE_FAILED, jobId);
        }
        return true;
    }

    @Override
    public void markJobEnabled(long jobId, boolean enabled) {
        final Map<String, Object> params = new HashMap<>();
        params.put("enabled", enabled);
        params.put("id", jobId);

        namedJdbcTemplate.update(UPDATE_JOB_ENABLED_BY_ID, params);
    }

    @Override
    public void markJobError(Collection<Long> jobIds, boolean error) {
        final Map<String, Object> params = new HashMap<>();
        params.put("error", error);
        params.put("ids", jobIds);

        namedJdbcTemplate.update(UPDATE_JOB_ERROR_BY_IDS, params);
    }

    @Override
    public void saveBatchJobInstance(final long jobId, final long batchJobId, final String username,
                                     final Date dateCreated) {
        final JobBatchJobInstancePO jobInstance = new JobBatchJobInstancePO();
        jobInstance.setJobId(jobId);
        jobInstance.setJobInstanceId(batchJobId);
        jobInstance.setCreateDate(dateCreated);
        jobInstance.setCreatedBy(username);

        final Map<String, Object> params = new HashMap<>();
        params.put(JobBatchJobInstancePO.FIELD_CREATED_BY, jobInstance.getCreatedBy());
        params.put(JobBatchJobInstancePO.FIELD_CREATE_DATE, jobInstance.getCreateDate());
        params.put(JobBatchJobInstancePO.FIELD_JOB_ID, jobInstance.getJobId());
        params.put(JobBatchJobInstancePO.FIELD_JOB_INSTANCE_ID, jobInstance.getJobInstanceId());

        jobBatchJobInstanceInsertActor.execute(params);
    }

    @Override
    public Map<Long, List<Long>> findAllBatchJobIds(final List<Long> jobIds) {
        final Map<Long, List<Long>> result = new HashMap<>();

        if (!CollectionUtils.isEmpty(jobIds)) {
            namedJdbcTemplate.query(SELECT_JOB_INSTANCES_BY_JOB_IDS, Collections.singletonMap("ids", jobIds),
                    rs -> {
                        while (rs.next()) {
                            final Long jobId = rs.getLong(1);
                            final Long jobInstanceId = rs.getLong(2);

                            List<Long> instanceList = result.get(jobId);
                            if (instanceList == null) {
                                instanceList = new ArrayList<>();
                                result.put(jobId, instanceList);
                            }
                            instanceList.add(jobInstanceId);
                        }
                        return null;
                    });
        }

        return result;
    }

    @Override
    public Map<Long, Long> findLastBatchJobIds(List<Long> jobIds) {
        final Map<Long, Long> result = new HashMap<>();

        if (!CollectionUtils.isEmpty(jobIds)) {

            namedJdbcTemplate.query(SELECT_JOB_LAST_EXECUTION, Collections.singletonMap("ids", jobIds), rs -> {
                while (rs.next()) {
                    result.put(rs.getLong(1), rs.getLong(2));
                }
                return null;
            });
        }

        return result;
    }

    @Override
    public String checkJobByParams(final JobPO jobPo) {
        Assert.notNull(jobPo);
        if (!CollectionUtils.isEmpty(jobPo.getParameters())) {
            try (Connection con = jdbcTemplate.getDataSource().getConnection()) {
                final Map<String, Object> paramMap = new HashMap<>();
                final StringBuilder paramSb = new StringBuilder();
                final List<JobParameterPO> parameters = jobPo.getParameters();
                for (int i = 0; i < parameters.size(); i++) {
                    final JobParameterPO p = parameters.get(i);
                    final String paramName = "param" + i;
                    final String valName = "val" + i;
                    paramSb.append("p.name = :").append(paramName).append(" and p.").append(p.getType().getFieldName())
                            .append(" = :").append(valName).append(" or ");
                    paramMap.put(paramName, p.getName());

                    Object paramValue = null;
                    switch (p.getType()) {
                        case STRING_ARR:{
                            paramValue = con.createArrayOf("text", p.getStringArrayValue());
                            break;
                        }
                        case DATE_ARR:{
                            // FIXME: time API support in jdbc 4.2 (http://openjdk.java.net/jeps/170)
                            // FIXME: but postgres driver 9.4-1206-jdbc42 is not yet compatible
                            paramValue = con.createArrayOf("timestamp",
                                    convertZonedDateTime(p.getDateArrayValue()));
                            break;
                        }
                        case LONG_ARR:{
                            paramValue = con.createArrayOf("bigint", p.getLongArrayValue());
                            break;
                        }
                        case DOUBLE_ARR:{
                            paramValue = con.createArrayOf("double", p.getDoubleArrayValue());
                            break;
                        }
                        case BOOLEAN_ARR:{
                            paramValue = con.createArrayOf("boolean", p.getBooleanArrayValue());
                            break;
                        }
                        case DATE: {
                            // FIXME: time API support in jdbc 4.2 (http://openjdk.java.net/jeps/170)
                            // FIXME: but postgres driver 9.4-1206-jdbc42 is not yet compatible
                            final ZonedDateTime zdt = (ZonedDateTime) p.getValueObject();
                            paramValue = Timestamp.from(zdt.toInstant());
                            break;
                        }
                        case STRING:
                        case LONG:
                        case DOUBLE:
                        case BOOLEAN: {
                            paramValue = p.getValueObject();
                            break;
                        }
                    }

                    paramMap.put(valName, paramValue);
                }

                paramSb.delete(paramSb.lastIndexOf(" or "), paramSb.length());

                final String sql = String.format(SELECT_JOB_CHECK_PARAMS, paramSb.toString());
                paramMap.put("jobNameRef", jobPo.getJobNameReference());
                paramMap.put("jobId", jobPo.getId());

                return namedJdbcTemplate.query(sql, paramMap, rs -> {
                    while (rs.next()) {
                        final int paramCount = rs.getInt("param_count");
                        if (paramCount == parameters.size()) {
                            return rs.getString("job_name");
                        }
                    }
                    return null;
                });
            } catch (SQLException e) {
                throw new JobException("Failed to prepare job parameters for job validation check", e,
                        ExceptionId.EX_JOB_PARAMETER_PREPARE_VALIDATION_ERROR);
            }
        } else {
            final Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(JobPO.FIELD_JOB_NAME_REFERENCE, jobPo.getJobNameReference());
            paramMap.put(JobPO.FIELD_ID, jobPo.getId());
            return namedJdbcTemplate.query(SELECT_JOBS_BY_REF_NAME, paramMap, rs -> {
                if (rs.next()) {
                    return rs.getString("job_name");
                }
                return null;
            });
        }
    }

    @Override
    public long insertJobTrigger(final JobTriggerPO jobTrigger) {
        final Map<String, Object> params = new HashMap<>();
        params.put(JobTriggerPO.FIELD_CREATED_BY, jobTrigger.getCreatedBy());
        params.put(JobTriggerPO.FIELD_CREATE_DATE, jobTrigger.getCreateDate());
        params.put(JobTriggerPO.FIELD_FINISH_JOB_ID, jobTrigger.getFinishJobId());
        params.put(JobTriggerPO.FIELD_START_JOB_ID, jobTrigger.getStartJobId());
        params.put(JobTriggerPO.FIELD_SUCCESS_RULE, jobTrigger.getSuccessRule());
        params.put(JobTriggerPO.FIELD_NAME, jobTrigger.getName());
        params.put(JobTriggerPO.FIELD_DESCRIPTION, jobTrigger.getDescription());

        final Number jobId = jobTriggerTemplateInsertActor.executeAndReturnKey(params);

        jobTrigger.setId(jobId.longValue());

        return jobTrigger.getId();
    }

    @Override
    public boolean updateJobTrigger(final JobTriggerPO jobTrigger) {
        Assert.notNull(jobTrigger);
        Assert.notNull(jobTrigger.getUpdateDate());
        Assert.notNull(jobTrigger.getUpdatedBy());

        int updatesCount = jdbcTemplate.execute(UPDATE_JOB_TRIGGER_BY_ID, ps -> {
            ps.setLong(1, jobTrigger.getStartJobId());
            ps.setBoolean(2, jobTrigger.getSuccessRule());
            ps.setString(3, jobTrigger.getName());
            ps.setString(4, jobTrigger.getDescription());
            ps.setString(5, jobTrigger.getUpdatedBy());
            ps.setDate(6, new java.sql.Date(jobTrigger.getUpdateDate().getTime()));
            ps.setLong(7, jobTrigger.getId());
            return ps.executeUpdate();
        });

        if (updatesCount == 0) {
            throw new DataProcessingException("Failed to update job trigger by ID: " + jobTrigger.getId(),
                    ExceptionId.EX_JOB_TRIGGER_UPDATE_ERROR, jobTrigger);
        }

        return updatesCount == 1;
    }

    @Override
    public List<JobTriggerPO> findAllJobTriggers(final long jobId) {
        return namedJdbcTemplate.query(SELECT_JOB_TRIGGERS_BY_ID,
                Collections.singletonMap(JobTriggerPO.FIELD_FINISH_JOB_ID, jobId), new JobTriggerRowMapper());
    }

    @Override
    public List<Long> getTriggerSuccessfulJobIds(final long jobId) {
        final Map<String, Object> params = new HashMap<>();
        params.put(JobTriggerPO.FIELD_FINISH_JOB_ID, jobId);
        params.put(JobTriggerPO.FIELD_SUCCESS_RULE, true);

        return namedJdbcTemplate.queryForList(SELECT_JOB_JOBS_BY_ID_AND_TRIGGER_RULE, params, Long.class);
    }

    @Override
    public List<Long> getTriggerFailedJobIds(final long jobId) {
        final Map<String, Object> params = new HashMap<>();
        params.put(JobTriggerPO.FIELD_FINISH_JOB_ID, jobId);
        params.put(JobTriggerPO.FIELD_SUCCESS_RULE, false);

        return namedJdbcTemplate.queryForList(SELECT_JOB_JOBS_BY_ID_AND_TRIGGER_RULE, params, Long.class);
    }

    @Override
    public JobTriggerPO findJobTrigger(final Long jobId, final Long triggerId) {
        final Map<String, Object> params = new HashMap<>();
        params.put(JobTriggerPO.FIELD_ID, triggerId);
        params.put(JobTriggerPO.FIELD_FINISH_JOB_ID, jobId);
        final List<JobTriggerPO> jobs = namedJdbcTemplate.query(SELECT_TRIGGER_BY_JOB_ID_TRIGGER_ID,
                params, new JobTriggerRowMapper());
        if (jobs.size() == 1) {
            return jobs.get(0);
        }

        return null;
    }

    @Override
    public JobTriggerPO findJobTrigger(final String name) {
        final List<JobTriggerPO> jobs = namedJdbcTemplate.query(SELECT_TRIGGER_BY_TRIGGER_NAME,
                Collections.singletonMap(JobTriggerPO.FIELD_NAME, name), new JobTriggerRowMapper());
        if (jobs.size() == 1) {
            return jobs.get(0);
        }

        return null;
    }

    @Override
    public boolean removeJobTrigger(Long jobId, Long triggerId) {
        int jobAffectedRows = jdbcTemplate.update(DELETE_JOB_TRIGGER, jobId, triggerId);

        if (jobAffectedRows != 1) {
            throw new JobException("Failed to delete job trigger with job id: " + jobId
                    + " and trigger id: " + triggerId, ExceptionId.EX_JOB_TRIGGER_DELETE_FAILED, triggerId);
        }
        return true;
    }

    private void deleteRemovedParameters(final JobPO job) {
        final List<JobParameterPO> jobParamList = getJobParameters(job.getId());
        final List<Long> originalParams = new ArrayList<>(jobParamList.size());
        originalParams.addAll(jobParamList.stream().map(JobParameterPO::getId).collect(Collectors.toList()));
        final long originalParamsListId = daoHelper.insertLongsToTemp(originalParams);

        final List<Long> paramsToUpdate = new ArrayList<>(job.getParameters().size());
        paramsToUpdate.addAll(job.getParameters().stream().filter(p -> p.getId() != null)
                .map(JobParameterPO::getId).collect(Collectors.toList()));
        final long toUpdateParamsListId = daoHelper.insertLongsToTemp(paramsToUpdate);

        final Map<String, Object> sqlParams = new HashMap<>(2);
        sqlParams.put("list_id_original", originalParamsListId);
        sqlParams.put("list_id_to_update", toUpdateParamsListId);

        namedJdbcTemplate.execute(DELETE_REMOVED_JOB_PARAMS, sqlParams, PreparedStatement::executeUpdate);
    }

    @Override
    public List<JobPO> findJobWithParameters(final List<Long> jobsIds) {
        final List<Map<String, Object>> jobs =
                namedJdbcTemplate.queryForList(
                        CollectionUtils.isEmpty(jobsIds) ? selectJobsWithParams : selectJobsByIdsWithParams,
                        Collections.singletonMap("jobsIds", jobsIds)
                );
        return new ArrayList<>(
                jobs.stream()
                        .reduce(new HashMap<>(), this::appendJobWithParameter, this::mergerJobs)
                        .values()
        );
    }

    private Map<Long, JobPO> appendJobWithParameter(final Map<Long, JobPO> jobs, final Map<String, Object> row) {
        final Long jobId = (Long) row.get("jobId");
        if (!jobs.containsKey(jobId)) {
            jobs.put(
                    jobId,
                    new JobPO(
                            jobId,
                            (String) row.get("jobName"),
                            (Boolean) row.get("jobEnabled"),
                            (String) row.get("jobCronExpr"),
                            (String) row.get("jobNameRef"),
                            (String) row.get("jobDescr")
                    )
            );
        }
        final JobParameterPO param = extractJobParameter(jobId, row);
        if (param != null) {
            jobs.get(jobId).addParameter(param);
        }
        return jobs;
    }

    private JobParameterPO extractJobParameter(final Long jobId, Map<String, Object> row) {
        try {
            final String name = (String) row.get("jobParameterName");
            final String valString = (String) row.get("jobParameterValString");
            final Timestamp valDate = (Timestamp) row.get("jobParameterValDate");
            final Long valLong = (Long) row.get("jobParameterValLong");
            final Double valDouble = (Double) row.get("jobParameterValDouble");
            final Boolean valBoolean = (Boolean) row.get("jobParameterValBoolean");

            String[] valStringArr = null;
            final Array stringArr = (Array)row.get("jobParameterValArrString");
            if (stringArr != null) {
                valStringArr = (String[])stringArr.getArray();
            }

            Timestamp[] valDateArr = null;
            final Array dateArr = (Array)row.get("jobParameterValArrDate");
            if (dateArr != null) {
                valDateArr = (Timestamp[])dateArr.getArray();
            }

            Long[] valLongArr = null;
            final Array longArr = (Array)row.get("jobParameterValArrLong");
            if (longArr != null) {
                valLongArr = (Long[])longArr.getArray();
            }

            Double[] valDoubleArr = null;
            final Array doubleArr = (Array)row.get("jobParameterValArrDouble");
            if (doubleArr != null) {
                valDoubleArr = (Double[])doubleArr.getArray();
            }

            Boolean[] valBooleanArr = null;
            final Array booleanArr = (Array)row.get("jobParameterValArrBoolean");
            if (booleanArr != null) {
                valBooleanArr = (Boolean[])booleanArr.getArray();
            }

            final JobParameterPO jobParameter = JobParameterRowMapper.validateAndCreateJobParameter(
                    name, valString, valDate, valLong, valDouble, valBoolean,
                    valStringArr, valDateArr, valLongArr, valDoubleArr, valBooleanArr);
            if (jobParameter != null) {
                jobParameter.setId((Long) row.get("jobParameterId"));
                jobParameter.setJobId(jobId);
                jobParameter.setCreatedBy((String) row.get("jobParameterCreatedBy"));
                jobParameter.setUpdatedBy((String) row.get("jobParameterUpdatedBy"));
                jobParameter.setCreateDate((Date) row.get("jobParameterCreateDate"));
                jobParameter.setUpdateDate((Date) row.get("jobParameterUpdateDate"));
                return jobParameter;
            }
            return null;
        } catch (SQLException e) {
            throw new JobException("Failed to extract job parameters for job with id: " + jobId, e,
                    ExceptionId.EX_JOB_PARAMETER_EXTRACT_ERROR, jobId);
        }
    }


    private Map<Long, JobPO> mergerJobs(final Map<Long, JobPO> jobs1, final Map<Long, JobPO> jobs2) {
        jobs2.forEach((key, value) -> {
            if (jobs1.containsKey(key)) {
                value.getParameters().forEach(p -> jobs1.get(key).addParameter(p));
            }
            else {
                jobs1.put(key, value);
            }
        });
        return jobs1;
    }

    @Transactional
    @Override
    public void saveJobs(List<JobPO> jobs) {
        jobs.forEach(job -> {
            removeJob(job.getName());
            insertJob(job);
        });
    }

    private void removeJob(String name) {
        final JobPO job = findJob(name);
        if (job != null) {
            removeJob(job.getId());
        }
    }
}