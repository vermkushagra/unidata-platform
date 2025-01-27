/**
 * Date: 31.03.2016
 */

package com.unidata.mdm.backend.service.job.batch.core;

import com.unidata.mdm.backend.dao.impl.DaoHelper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.dao.JdbcExecutionContextDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.serializer.Serializer;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class CustomJdbcExecutionContextDao extends JdbcExecutionContextDao {
    private static final String FIND_JOB_EXECUTION_CONTEXT_BY_JOB_EXECUTION_IDS =
        "SELECT SHORT_CONTEXT, SERIALIZED_CONTEXT, JOB_EXECUTION_ID " +
            "FROM %PREFIX%JOB_EXECUTION_CONTEXT " +
            "WHERE JOB_EXECUTION_ID in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?)";

    private static final String FIND_STEP_EXECUTION_CONTEXT_BY_STEP_EXECUTION_IDS =
        "SELECT SHORT_CONTEXT, SERIALIZED_CONTEXT, STEP_EXECUTION_ID "+
            "FROM %PREFIX%STEP_EXECUTION_CONTEXT " +
            "WHERE STEP_EXECUTION_ID in (select tmp.id from t_tmp_id tmp where tmp.list_id = ?)";

    private DaoHelper daoHelper;
    private ExecutionContextSerializer serializer;


    public void setDaoHelper(DaoHelper daoHelper) {
        this.daoHelper = daoHelper;
    }

    /**
   	 * Setter for {@link Serializer} implementation
   	 *
   	 * @param serializer
   	 */
   	public void setSerializer(ExecutionContextSerializer serializer) {
        super.setSerializer(serializer);
   		this.serializer = serializer;
   	}

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(daoHelper, "The daoHelper must not be null.");
    }

    public Map<Long, ExecutionContext> loadJobExecutionContexts(Collection<Long> jobExecutionIds) {
        Assert.notNull(jobExecutionIds, "Job execution IDs cannot be null.");

        long listId = daoHelper.insertLongsToTemp(jobExecutionIds);

        final Map<Long, ExecutionContext> result = new LinkedHashMap<>();

        getJdbcTemplate().query(getQuery(FIND_JOB_EXECUTION_CONTEXT_BY_JOB_EXECUTION_IDS),
            new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    long jobExecutionId = rs.getLong("JOB_EXECUTION_ID");

                    result.put(jobExecutionId, new ExecutionContextRowMapper().mapRow(rs, 0));
                }
            },
            listId);

        return result;
    }

    public Map<Long, ExecutionContext> loadStepExecutionContexts(Collection<Long> stepExecutionIds) {
        Assert.notNull(stepExecutionIds, "Step execution IDs cannot be null.");

        long listId = daoHelper.insertLongsToTemp(stepExecutionIds);

        final Map<Long, ExecutionContext> result = new LinkedHashMap<>();

        getJdbcTemplate().query(getQuery(FIND_STEP_EXECUTION_CONTEXT_BY_STEP_EXECUTION_IDS),
            new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet rs) throws SQLException {
                    long stepExecutionId = rs.getLong("STEP_EXECUTION_ID");

                    result.put(stepExecutionId, new ExecutionContextRowMapper().mapRow(rs, 0));
                }
            },
            listId);

        return result;
    }

    private class ExecutionContextRowMapper implements RowMapper<ExecutionContext> {
        @Override
        public ExecutionContext mapRow(ResultSet rs, int i) throws SQLException {
            ExecutionContext executionContext = new ExecutionContext();
            String serializedContext = rs.getString("SERIALIZED_CONTEXT");
            if (serializedContext == null) {
                serializedContext = rs.getString("SHORT_CONTEXT");
            }

            Map<String, Object> map;
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(serializedContext.getBytes("ISO-8859-1"));
                map = serializer.deserialize(in);
            }
            catch (IOException ioe) {
                throw new IllegalArgumentException("Unable to deserialize the execution context", ioe);
            }
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                executionContext.put(entry.getKey(), entry.getValue());
            }
            return executionContext;
        }
    }
}
