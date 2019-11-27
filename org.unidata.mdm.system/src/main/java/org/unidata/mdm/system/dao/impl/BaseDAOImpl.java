/**
 *
 */
package org.unidata.mdm.system.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.unidata.mdm.system.dao.BaseDao;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.exception.SystemExceptionIds;

/**
 * @author Mikhail Mikhailov
 * Abstract DAO parent for various utility methods.
 */
public abstract class BaseDAOImpl implements BaseDao {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAOImpl.class);
    /**
     * JDBC template.
     */
    protected JdbcTemplate jdbcTemplate;
    /**
     * Named parameter template.
     */
    protected NamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * Constructor.
     */
    public BaseDAOImpl(DataSource dataSource) {
        super();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDefaultDataSource() {
        return jdbcTemplate.getDataSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getBareConnection() {
        try {
            return jdbcTemplate.getDataSource().getConnection();
        } catch (SQLException e) {
            final String message = "Cannot get bare connection from JDBC template.";
            LOGGER.error(message);
            throw new PlatformFailureException(message, SystemExceptionIds.EX_SYSTEM_CONNECTION_GET, e);
        }
    }

}
