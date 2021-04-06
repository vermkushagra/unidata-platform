package com.unidata.mdm.backend.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.unidata.mdm.backend.util.PrintHelper;

/**
 * The Class UnidataJdbcTemplateImpl.
 *
 * @author denis.vinnichek
 */
public class UnidataJdbcTemplateImpl extends JdbcTemplate implements UnidataJdbcTemplate {

    /** The Constant LOG. */
    private static final Logger STATEMENT_LOGGER = LoggerFactory.getLogger(UnidataJdbcTemplateImpl.class.getPackage().getName() + ".SQL");

    /**
	 * Instantiates a new unidata jdbc template impl.
	 *
	 * @param dataSource
	 *            the data source
	 */
    public UnidataJdbcTemplateImpl(DataSource dataSource) {
        super(dataSource);
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#execute(java.lang.String, org.springframework.jdbc.core.PreparedStatementCallback)
     */
    @Override
    public <T> T execute(String sql, PreparedStatementCallback<T> action) {
        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.execute(sql, action);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, null, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#execute(java.lang.String)
     */
    @Override
    public void execute(String sql) {
        //TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            super.execute(sql);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, null, startTime);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#query(java.lang.String, org.springframework.jdbc.core.RowMapper)
     */
    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        List<T> result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.query(sql, rowMapper);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, null, startTime);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) {
        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.query(sql, rse, args);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, args, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#query(java.lang.String, org.springframework.jdbc.core.RowMapper, java.lang.Object[])
     */
    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        List<T> result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.query(sql, rowMapper, args);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, args, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForObject(java.lang.String, org.springframework.jdbc.core.RowMapper)
     */
    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.queryForObject(sql, rowMapper);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, null, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForObject(java.lang.String, org.springframework.jdbc.core.RowMapper, java.lang.Object[])
     */
    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.queryForObject(sql, rowMapper, args);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, args, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForObject(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType) {
        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.queryForObject(sql, requiredType);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, null, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForObject(java.lang.String, java.lang.Class, java.lang.Object[])
     */
    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) {
        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.queryForObject(sql, requiredType, args);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, args, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(java.lang.String, java.lang.Class, java.lang.Object[])
     */
    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) {
        List<T> result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.queryForList(sql, elementType, args);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, args, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForRowSet(java.lang.String)
     */
    @Override
    public SqlRowSet queryForRowSet(String sql) {
        SqlRowSet result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.queryForRowSet(sql);
        }  finally {
            if (isDebugEnabled) {
                logSql(sql, null, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForRowSet(java.lang.String, java.lang.Object[])
     */
    @Override
    public SqlRowSet queryForRowSet(String sql, Object... args) {
        SqlRowSet result = null;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.queryForRowSet(sql, args);
        }  finally {
            if (isDebugEnabled) {
                logSql(sql, args, startTime);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#update(java.lang.String, java.lang.Object[])
     */
    @Override
    public int update(String sql, Object... args) {
        int affectedRows = 0;
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            affectedRows = super.update(sql, args);
            return affectedRows;
        } finally {
            if (isDebugEnabled) {
                logSql(sql, args, startTime);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.jdbc.UnidataJdbcTemplate#updateNoContext(java.lang.String, java.lang.Object[])
     */
    @Override
    public int updateNoContext(String sql, Object... args) {
        int affectedRows = 0;
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            affectedRows = super.update(sql, args);
            return affectedRows;
        } finally {
            if (isDebugEnabled) {
                logSql(sql, args, startTime);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.JdbcTemplate#batchUpdate(java.lang.String, org.springframework.jdbc.core.BatchPreparedStatementSetter)
     */
    @Override
    public int[] batchUpdate(String sql, BatchPreparedStatementSetter pss) {
        // TransactionContextUtils.setTransactionUserContext(this);
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            return super.batchUpdate(sql, pss);
        } finally {
            if (isDebugEnabled) {
                logSql(sql, null, startTime);
            }
        }
    }

    /**
	 * Log sql.
	 *
	 * @param sql
	 *            the sql
	 * @param args
	 *            the args
	 * @param startTime
	 *            the start time
	 */
    protected void logSql(String sql, Object[] args, long startTime) {

        long executionTime = System.currentTimeMillis() - startTime;

        StringBuilder sb = new StringBuilder()
            .append(executionTime)
            .append(" ms")
            .append("; [")
            .append(sql)
            .append("]");

        if(args != null) {
            sb.append(";").append(PrintHelper.printObjectArray(args));
        }

        STATEMENT_LOGGER.debug(sb.toString());
    }
}
