/**
 *
 */
package org.unidata.mdm.core.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.unidata.mdm.core.dao.BaseDao;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov
 * Abstract DAO parent for various utility methods.
 */
public abstract class AbstractDaoImpl implements BaseDao {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDaoImpl.class);
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
    public AbstractDaoImpl(DataSource dataSource) {
        super();
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Converts status list to SQL suitable string.
     * @param values list
     * @return string
     */
    protected String enumToString(List<? extends Enum<?>> values) {

        if (values == null) {
            return null;
        }

        final List<String> strings = values.stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        strings.stream().forEach(s -> StringUtils.wrap(s, "'"));
        return String.join(",", strings);
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
            throw new PlatformFailureException(message, CoreExceptionIds.EX_SYSTEM_CONNECTION_GET, e);
        }
    }

}
