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
