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

package com.unidata.mdm.backend.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.unidata.mdm.backend.util.PrintHelper;

/**
 * @author Michael Yashin. Created on 01.04.2015.
 */
public class UnidataNamedParameterJdbcTemplateImpl extends NamedParameterJdbcTemplate implements UnidataNamedParameterJdbcTemplate {
    private static final Logger STATEMENT_LOGGER = LoggerFactory.getLogger(UnidataNamedParameterJdbcTemplateImpl.class.getPackage().getName() + ".SQL");

    public UnidataNamedParameterJdbcTemplateImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int update(String sql, SqlParameterSource paramSource) throws DataAccessException {
        int result = -1;
        // TransactionContextUtils.setTransactionUserContext(this.getJdbcOperations());
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.update(sql, paramSource);
        } finally {
            if (isDebugEnabled) {
                logSql(startTime, sql, paramSource);
            }
        }
        return result;
    }

    @Override
    public int update(
            String sql, SqlParameterSource paramSource, KeyHolder generatedKeyHolder, String[] keyColumnNames)
            throws DataAccessException {

        int result = -1;
        // TransactionContextUtils.setTransactionUserContext(this.getJdbcOperations());
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.update(sql, paramSource, generatedKeyHolder, keyColumnNames);
        } finally {
            if (isDebugEnabled) {
                logSql(startTime, sql, paramSource);
            }
        }
        return result;
    }

    @Override
    public <T> List<T> query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper)
            throws DataAccessException {

        List<T> result = null;
        // TransactionContextUtils.setTransactionUserContext(this.getJdbcOperations());
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.query(sql, paramSource, rowMapper);
        } finally {
            if (isDebugEnabled) {
                logSql(startTime, sql, paramSource);
            }
        }
        return result;
    }

    @Override
    public <T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper)
            throws DataAccessException {

        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this.getJdbcOperations());
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.queryForObject(sql, paramSource, rowMapper);
        } finally {
            if (isDebugEnabled) {
                logSql(startTime, sql, paramSource);
            }
        }
        return result;
    }

    @Override
    public int[] batchUpdate(String sql, SqlParameterSource[] batchArgs) {
        int[] result = null;
        // TransactionContextUtils.setTransactionUserContext(this.getJdbcOperations());
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.batchUpdate(sql, batchArgs);
        } finally {
            if (isDebugEnabled) {
                logSql(startTime, sql, (Object)batchArgs);
            }
        }
        return result;
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, SqlParameterSource paramSource) throws DataAccessException {
        SqlRowSet result = null;
        // TransactionContextUtils.setTransactionUserContext(this.getJdbcOperations());
        long startTime = System.currentTimeMillis();
        try {
            result = super.queryForRowSet(sql, paramSource);
        } finally {
            if (STATEMENT_LOGGER.isDebugEnabled()) {
                logSql(startTime, sql, paramSource);
            }
        }
        return result;
    }

    @Override
    public <T> T execute(String sql, SqlParameterSource paramSource, PreparedStatementCallback<T> action)
            throws DataAccessException {

        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this.getJdbcOperations());
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.execute(sql, paramSource, action);
        } finally {
            if (isDebugEnabled) {
                logSql(startTime, sql, paramSource);
            }
        }
        return result;
    }

    @Override
    public <T> T query(String sql, SqlParameterSource paramSource, ResultSetExtractor<T> rse)
            throws DataAccessException {
        T result = null;
        // TransactionContextUtils.setTransactionUserContext(this.getJdbcOperations());
        boolean isDebugEnabled = STATEMENT_LOGGER.isDebugEnabled();
        long startTime = isDebugEnabled ? System.currentTimeMillis() : 0L;
        try {
            result = super.query(sql, paramSource, rse);
        } finally {
            if (isDebugEnabled) {
                logSql(startTime, sql, paramSource);
            }
        }
        return result;
    }


    protected void logSql(long startTime, String sql, Object... args) {

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
