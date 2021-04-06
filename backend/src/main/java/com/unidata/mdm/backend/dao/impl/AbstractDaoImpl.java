/**
 *
 */
package com.unidata.mdm.backend.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.dao.BaseDao;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplateImpl;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplate;
import com.unidata.mdm.backend.jdbc.UnidataNamedParameterJdbcTemplateImpl;

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
     * Entity name by etalon id.
     */
    protected static final String GET_ENTITY_NAME_BY_ETALON_ID = "select name from etalons where id = ?::uuid";
    /**
     * Entity name by origin id.
     */
    protected static final String GET_ENTITY_NAME_BY_ORIGIN_ID = "select name from origins where id = ?::uuid";
    /**
     * Entity name by etalon id.
     */
    protected static final String GET_ENTITY_NAME_BY_RELATION_FROM_ETALON_ID = "select e.name from etalons e, etalons_relations r where r.id = ?::uuid and e.id = r.etalon_id_from";
    /**
     * Entity name by origin id.
     */
    protected static final String GET_ENTITY_NAME_BY_RELATION_FROM_ORIGIN_ID = "select o.name from origins o, origins_relations r where r.id = ?::uuid and o.id = r.origin_id_from";
    /**
     * JDBC template.
     */
    protected UnidataJdbcTemplate jdbcTemplate;
    /**
     * Named parameter template.
     */
    protected UnidataNamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * Constructor.
     */
    public AbstractDaoImpl(DataSource dataSource) {
        super();
        this.jdbcTemplate = new UnidataJdbcTemplateImpl(dataSource);
        this.namedJdbcTemplate = new UnidataNamedParameterJdbcTemplateImpl(dataSource);
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
    public String getEntityNameByEtalonId(String etalonId) {
        return jdbcTemplate.queryForObject(GET_ENTITY_NAME_BY_ETALON_ID, String.class, etalonId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityNameByOriginId(String originId) {
        return jdbcTemplate.queryForObject(GET_ENTITY_NAME_BY_ORIGIN_ID, String.class, originId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityNameByRelationFromEtalonId(String relationEtalonId) {
        return jdbcTemplate.queryForObject(GET_ENTITY_NAME_BY_RELATION_FROM_ETALON_ID, String.class, relationEtalonId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityNameByRelationFromOriginId(String relationOriginId) {
        return jdbcTemplate.queryForObject(GET_ENTITY_NAME_BY_RELATION_FROM_ORIGIN_ID, String.class, relationOriginId);
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
            throw new SystemRuntimeException(message, e, ExceptionId.EX_SYSTEM_CONNECTION_GET);
        }
    }

}
