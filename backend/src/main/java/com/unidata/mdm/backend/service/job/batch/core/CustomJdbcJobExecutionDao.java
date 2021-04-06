/**
 * Date: 31.03.2016
 */

package com.unidata.mdm.backend.service.job.batch.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameter.ParameterType;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import com.unidata.mdm.backend.common.dto.PaginatedResultDTO;
import com.unidata.mdm.backend.common.dto.job.JobExecutionPaginatedResultDTO;
import com.unidata.mdm.backend.dao.impl.DaoHelper;
import com.unidata.mdm.backend.dto.job.JobExecutionFilter;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class CustomJdbcJobExecutionDao extends JdbcJobExecutionDao {
    private static final String FIND_JOB_EXECUTIONS_BY_JOB_INSTANCE_IDS =
        "SELECT JOB_EXECUTION_ID, START_TIME, END_TIME, STATUS," +
            " EXIT_CODE, EXIT_MESSAGE, CREATE_TIME, LAST_UPDATED, VERSION, JOB_CONFIGURATION_LOCATION, JOB_INSTANCE_ID" +
            " from %PREFIX%JOB_EXECUTION" +
            " where JOB_INSTANCE_ID in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?)" +
            " order by JOB_EXECUTION_ID desc";

    private static final String SEARCH_JOB_EXECUTIONS_BY_FILTER =
        "SELECT JOB_EXECUTION_ID, START_TIME, END_TIME, STATUS," +
            " EXIT_CODE, EXIT_MESSAGE, CREATE_TIME, LAST_UPDATED, VERSION, JOB_CONFIGURATION_LOCATION, JOB_INSTANCE_ID" +
            " from %PREFIX%JOB_EXECUTION" +
            " where JOB_INSTANCE_ID in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?)" +
            " order by CREATE_TIME desc" +
            " limit ? offset ?";

    private static final String GET_JOB_EXECUTIONS_COUNT_BY_FILTER =
        "SELECT count(JOB_EXECUTION_ID)" +
            " from %PREFIX%JOB_EXECUTION" +
            " where JOB_INSTANCE_ID in (:job_instance_ids)";

    private static final String FIND_LAST_JOB_EXECUTION_BY_JOB_INSTANCE_IDS =
        "SELECT DISTINCT ON (JOB_INSTANCE_ID) " +
            "JOB_EXECUTION_ID, START_TIME, END_TIME, STATUS," +
            " EXIT_CODE, EXIT_MESSAGE, CREATE_TIME, LAST_UPDATED, VERSION, JOB_CONFIGURATION_LOCATION, JOB_INSTANCE_ID" +
            " from %PREFIX%JOB_EXECUTION" +
            " where JOB_INSTANCE_ID in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?)" +
            " order by JOB_INSTANCE_ID, JOB_EXECUTION_ID desc";

    private static final String FIND_LAST_JOB_EXECUTION_BY_JOB_INSTANCE_IDS_AND_STATUS =
            "SELECT DISTINCT ON (JOB_INSTANCE_ID) " +
                    "JOB_EXECUTION_ID, START_TIME, END_TIME, STATUS," +
                    " EXIT_CODE, EXIT_MESSAGE, CREATE_TIME, LAST_UPDATED, VERSION, JOB_CONFIGURATION_LOCATION, JOB_INSTANCE_ID" +
                    " from %PREFIX%JOB_EXECUTION" +
                    " where JOB_INSTANCE_ID in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?) AND STATUS = ?" +
                    " order by JOB_INSTANCE_ID desc, JOB_EXECUTION_ID desc";

    private static final String FIND_LAST_JOB_EXECUTION_ID_BY_JOB_INSTANCE_IDS =
        "SELECT DISTINCT ON (JOB_INSTANCE_ID) " +
            "JOB_EXECUTION_ID, JOB_INSTANCE_ID" +
            " from %PREFIX%JOB_EXECUTION" +
            " where JOB_INSTANCE_ID in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?)" +
            " order by JOB_INSTANCE_ID, JOB_EXECUTION_ID desc";

    private static final String FIND_JOB_EXECUTIONS_PARAMS_BY_JOB_INSTANCE_IDS =
        "SELECT jep.JOB_EXECUTION_ID, KEY_NAME, TYPE_CD, STRING_VAL, DATE_VAL, LONG_VAL, DOUBLE_VAL, IDENTIFYING " +
            "from %PREFIX%JOB_EXECUTION_PARAMS jep" +
            "  inner join %PREFIX%JOB_EXECUTION je on je.JOB_EXECUTION_ID = jep.JOB_EXECUTION_ID " +
            "where je.job_instance_id in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?)";

    private static final String FIND_LAST_JOB_EXECUTION_PARAMS_BY_JOB_INSTANCE_IDS =
        "SELECT jep.JOB_EXECUTION_ID, KEY_NAME, TYPE_CD, STRING_VAL, DATE_VAL, LONG_VAL, DOUBLE_VAL, IDENTIFYING " +
            "from %PREFIX%JOB_EXECUTION_PARAMS jep " +
            "where jep.JOB_EXECUTION_ID in (" +
            "  SELECT DISTINCT ON (JOB_INSTANCE_ID) JOB_EXECUTION_ID " +
            "  from %PREFIX%JOB_EXECUTION " +
            "  where JOB_INSTANCE_ID in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?) " +
            "  order by JOB_INSTANCE_ID, JOB_EXECUTION_ID desc )";

    private DaoHelper daoHelper;

    private NamedParameterJdbcTemplate namedJdbcOperations;

    public void setDaoHelper(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    public void setNamedJdbcOperations(NamedParameterJdbcTemplate namedJdbcOperations) {
        this.namedJdbcOperations = namedJdbcOperations;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(daoHelper, "The daoHelper must not be null.");
        Assert.notNull(namedJdbcOperations, "The named JDBC template must not be null.");
    }

    /**
     *
     * @param jobInstanceIds
     * @return
     */
    Map<Long, List<JobExecution>> findJobExecutions(Collection<Long> jobInstanceIds) {
        Assert.notNull(jobInstanceIds, "Job IDs cannot be null.");

        long listId = daoHelper.insertLongsToTemp(jobInstanceIds);

        final Map<Long, JobParameters> jobParameters = loadJobExecutionParameters(listId);

        final Map<Long, List<JobExecution>> result = new HashMap<>();

        getJdbcTemplate().query(getQuery(FIND_JOB_EXECUTIONS_BY_JOB_INSTANCE_IDS),
            new ResultSetExtractor<Object>() {
                @Override
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    while (rs.next()) {
                        long jobInstanceId = rs.getLong("JOB_INSTANCE_ID");

                        List<JobExecution> executions = result.get(jobInstanceId);
                        if (executions == null) {
                            executions = new ArrayList<>();
                            result.put(jobInstanceId, executions);
                        }

                        JobExecution jobExecution = new JobExecutionRowMapper(jobParameters).mapRow(rs, 0);

                        executions.add(jobExecution);
                    }

                    return null;
                }
            },
            listId);

        return result;
    }

    /**
     *
     * @param filter
     * @return
     */
    PaginatedResultDTO<JobExecution> searchJobExecutions(JobExecutionFilter filter) {

        Assert.notNull(filter, "Filter cannot be null.");
        Assert.notNull(filter.getJobInstanceIds(), "Job IDs cannot be null.");

        long listId = daoHelper.insertLongsToTemp(filter.getJobInstanceIds());

        final Map<Long, JobParameters> jobParameters = loadJobExecutionParameters(listId);

        final List<JobExecution> jobExecutions = new ArrayList<>();

        getJdbcTemplate().query(getQuery(SEARCH_JOB_EXECUTIONS_BY_FILTER),
            new ResultSetExtractor<Object>() {
                @Override
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    while (rs.next()) {
                        JobExecution jobExecution = new JobExecutionRowMapper(jobParameters).mapRow(rs, 0);

                        jobExecutions.add(jobExecution);
                    }

                    return null;
                }
            },
            listId, filter.getItemCount(), filter.getFromInd());

        JobExecutionPaginatedResultDTO<JobExecution> paginatedResult = new JobExecutionPaginatedResultDTO<>();
        paginatedResult.setPage(jobExecutions);

        int count = namedJdbcOperations.queryForObject(getQuery(GET_JOB_EXECUTIONS_COUNT_BY_FILTER),
                Collections.singletonMap("job_instance_ids", filter.getJobInstanceIds()),
                Integer.class);

        paginatedResult.setTotalCount(count);

        return paginatedResult;
    }

    /**
     * // TODO: copy-past code refactoring
     *
     * @param jobInstanceIds
     * @return
     */
    Map<Long, JobExecution> findLastJobExecutions(Collection<Long> jobInstanceIds) {
        Assert.notNull(jobInstanceIds, "Job IDs cannot be null.");

        long listId = daoHelper.insertLongsToTemp(jobInstanceIds);

        final Map<Long, JobParameters> jobParameters = loadLastJobExecutionParameters(listId);

        final Map<Long, JobExecution> result = new HashMap<>();

        getJdbcTemplate().query(getQuery(FIND_LAST_JOB_EXECUTION_BY_JOB_INSTANCE_IDS),
            new ResultSetExtractor<Object>() {
                @Override
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    while (rs.next()) {
                        long jobInstanceId = rs.getLong("JOB_INSTANCE_ID");

                        JobExecution jobExecution = new JobExecutionRowMapper(jobParameters).mapRow(rs, 0);

                        result.put(jobInstanceId, jobExecution);
                    }

                    return null;
                }
            },
            listId);

        return result;
    }

    /**
     * // TODO: copy-past code refactoring
     *
     * @param jobInstanceIds
     * @return
     */
    public Map<Long, JobExecution> findLastJobSuccessExecutions(Collection<Long> jobInstanceIds) {
        Assert.notNull(jobInstanceIds, "Job IDs cannot be null.");

        long listId = daoHelper.insertLongsToTemp(jobInstanceIds);

        final Map<Long, JobParameters> jobParameters = loadLastJobExecutionParameters(listId);

        final Map<Long, JobExecution> result = new HashMap<>();

        getJdbcTemplate().query(getQuery(FIND_LAST_JOB_EXECUTION_BY_JOB_INSTANCE_IDS_AND_STATUS),
                rs -> {
                    while (rs.next()) {
                        long jobInstanceId = rs.getLong("JOB_INSTANCE_ID");
                        JobExecution jobExecution = new JobExecutionRowMapper(jobParameters).mapRow(rs, 0);
                        result.put(jobInstanceId, jobExecution);
                        break;
                    }

                    return null;
                },
                listId, BatchStatus.COMPLETED.toString());

        return result;
    }

    /**
     *
     * @param jobInstanceIds
     * @return
     */
    Map<Long, Long> findLastJobExecutionIds(Collection<Long> jobInstanceIds) {
        Assert.notNull(jobInstanceIds, "Job IDs cannot be null.");

        long listId = daoHelper.insertLongsToTemp(jobInstanceIds);

        final Map<Long, Long> result = new HashMap<>();

        getJdbcTemplate().query(getQuery(FIND_LAST_JOB_EXECUTION_ID_BY_JOB_INSTANCE_IDS),
            new ResultSetExtractor<Object>() {
                @Override
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    while (rs.next()) {
                        long jobInstanceId = rs.getLong("JOB_INSTANCE_ID");
                        long jobExecutionId = rs.getLong("JOB_EXECUTION_ID");

                        result.put(jobInstanceId, jobExecutionId);
                    }

                    return null;
                }
            },
            listId);

        return result;
    }

    /**
     *
     * @param listId
     * @return
     */
    private Map<Long, JobParameters> loadJobExecutionParameters(long listId){
        Map<Long, Map<String, JobParameter>> jobExecutionParamsMap = new HashMap<>();

        getJdbcTemplate().query(getQuery(FIND_JOB_EXECUTIONS_PARAMS_BY_JOB_INSTANCE_IDS),
            new Object[]{listId},
                rs -> {
                    while (rs.next()) {
                        long jobExecutionId = rs.getLong("JOB_EXECUTION_ID");

                        Map<String, JobParameter> params = jobExecutionParamsMap.get(jobExecutionId);

                        if (params == null) {
                            params = new HashMap<>();
                            jobExecutionParamsMap.put(jobExecutionId, params);
                        }

                        ParameterType type = ParameterType.valueOf(rs.getString(3));
                        JobParameter value = null;

                        if (type == ParameterType.STRING) {
                            value = new JobParameter(rs.getString(4), rs.getString(8).equalsIgnoreCase("Y"));
                        } else if (type == ParameterType.LONG) {
                            value = new JobParameter(rs.getLong(6), rs.getString(8).equalsIgnoreCase("Y"));
                        } else if (type == ParameterType.DOUBLE) {
                            value = new JobParameter(rs.getDouble(7), rs.getString(8).equalsIgnoreCase("Y"));
                        } else if (type == ParameterType.DATE) {
                            value = new JobParameter(rs.getTimestamp(5), rs.getString(8).equalsIgnoreCase("Y"));
                        }

                        params.put(rs.getString(2), value);
                    }

                    return null;
                });

        Map<Long, JobParameters> result = new HashMap<>();

        jobExecutionParamsMap.entrySet().stream().forEach(entry -> {
            result.put(entry.getKey(), new JobParameters(entry.getValue()));
        });

        return result;
    }

    /**
     * Load parameters only for last execution for every jobInstanceId.
     *
     * @param listId
     * @return
     */
    private Map<Long, JobParameters> loadLastJobExecutionParameters(long listId){
        Map<Long, Map<String, JobParameter>> jobExecutionParamsMap = new HashMap<>();

        getJdbcTemplate().query(getQuery(FIND_LAST_JOB_EXECUTION_PARAMS_BY_JOB_INSTANCE_IDS),
            new Object[]{listId},
                rs -> {
                    while (rs.next()) {
                        long jobExecutionId = rs.getLong("JOB_EXECUTION_ID");

                        Map<String, JobParameter> params = jobExecutionParamsMap.get(jobExecutionId);

                        if (params == null) {
                            params = new HashMap<>();
                            jobExecutionParamsMap.put(jobExecutionId, params);
                        }

                        ParameterType type = ParameterType.valueOf(rs.getString(3));
                        JobParameter value = null;

                        if (type == ParameterType.STRING) {
                            value = new JobParameter(rs.getString(4), rs.getString(8).equalsIgnoreCase("Y"));
                        } else if (type == ParameterType.LONG) {
                            value = new JobParameter(rs.getLong(6), rs.getString(8).equalsIgnoreCase("Y"));
                        } else if (type == ParameterType.DOUBLE) {
                            value = new JobParameter(rs.getDouble(7), rs.getString(8).equalsIgnoreCase("Y"));
                        } else if (type == ParameterType.DATE) {
                            value = new JobParameter(rs.getTimestamp(5), rs.getString(8).equalsIgnoreCase("Y"));
                        }

                        params.put(rs.getString(2), value);
                    }

                    return null;
                });

        Map<Long, JobParameters> result = new HashMap<>();

        jobExecutionParamsMap.entrySet().stream().forEach(entry -> {
            result.put(entry.getKey(), new JobParameters(entry.getValue()));
        });

        return result;
    }

    /**
   	 * Re-usable mapper for {@link JobExecution} instances.
   	 *
   	 * @author Dave Syer
   	 *
   	 */
    private final class JobExecutionRowMapper implements RowMapper<JobExecution> {
        private Map<Long, JobParameters> jobExecutionParameters;

        JobExecutionRowMapper(Map<Long, JobParameters> jobExecutionParameters) {
            this.jobExecutionParameters = jobExecutionParameters;
        }

        @Override
        public JobExecution mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong(1);
            String jobConfigurationLocation = rs.getString(10);

            JobExecution jobExecution = new JobExecution(id, jobExecutionParameters.get(id), jobConfigurationLocation);

            jobExecution.setStartTime(rs.getTimestamp(2));
            jobExecution.setEndTime(rs.getTimestamp(3));
            jobExecution.setStatus(BatchStatus.valueOf(rs.getString(4)));
            jobExecution.setExitStatus(new ExitStatus(rs.getString(5), rs.getString(6)));
            jobExecution.setCreateTime(rs.getTimestamp(7));
            jobExecution.setLastUpdated(rs.getTimestamp(8));
            jobExecution.setVersion(rs.getInt(9));
            return jobExecution;
        }
    }
}
