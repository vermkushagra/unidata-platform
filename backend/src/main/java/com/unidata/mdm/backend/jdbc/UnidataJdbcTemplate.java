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

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * The Interface UnidataJdbcTemplate.
 *
 * @author denis.vinnichek
 */
public interface UnidataJdbcTemplate {

    /**
     * Gets the underlaying data source.
     * @return data source
     */
    public DataSource getDataSource();

    /**
	 * Execute a query given static SQL, mapping each row to a Java object via a
	 * RowMapper.
	 * <p>
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to execute a
	 * static query with a PreparedStatement, use the overloaded {@code query}
	 * method with {@code null} as argument array.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            SQL query to execute
	 * @param rowMapper
	 *            object that will map one object per row
	 * @return the result List, containing mapped objects
	 * @throws DataAccessException
	 *             the data access exception
	 * @throws org.springframework.dao.DataAccessException
	 *             if there is any problem executing the query
	 * @see #query(String, org.springframework.jdbc.core.RowMapper, Object...)
	 */
    <T> List<T> query(String sql, RowMapper<T> rowMapper);

    /**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, mapping each row to a Java object via a
	 * RowMapper.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            SQL query to execute
	 * @param rowMapper
	 *            object that will map one object per row
	 * @param args
	 *            arguments to bind to the query (leaving it to the
	 *            PreparedStatement to guess the corresponding SQL type); may
	 *            also contain
	 *            {@link org.springframework.jdbc.core.SqlParameterValue}
	 *            objects which indicate not only the argument value but also
	 *            the SQL type and optionally the scale
	 * @return the result List, containing mapped objects
	 * @throws DataAccessException
	 *             if the query fails
	 */
    <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args);

    /**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, mapping each row to a Java object via a
	 * ResultSetExtractor.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            SQL query to execute
	 * @param rse
	 *            object that will map single result
	 * @param args
	 *            arguments to bind to the query (leaving it to the
	 *            PreparedStatement to guess the corresponding SQL type); may
	 *            also contain
	 *            {@link org.springframework.jdbc.core.SqlParameterValue}
	 *            objects which indicate not only the argument value but also
	 *            the SQL type and optionally the scale
	 * @return the result, containing mapped object
	 * @throws DataAccessException
	 *             if the query fails
	 */
    <T> T query(String sql, ResultSetExtractor<T> rse, Object... args);

    /**
	 * Execute a query given static SQL, mapping a single result row to a Java
	 * object via a RowMapper.
	 * <p>
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to execute a
	 * static query with a PreparedStatement, use the overloaded
	 * {@link #queryForObject(String, RowMapper, Object...)} method with
	 * {@code null} as argument array.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            SQL query to execute
	 * @param rowMapper
	 *            object that will map one object per row
	 * @return the single mapped object
	 * @throws DataAccessException
	 *             if there is any problem executing the query
	 * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
	 *             if the query does not return exactly one row
	 * @see #queryForObject(String, RowMapper, Object...)
	 */
    <T> T queryForObject(String sql, RowMapper<T> rowMapper);

    /**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, mapping a single result row to a Java
	 * object via a RowMapper.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            SQL query to execute
	 * @param rowMapper
	 *            object that will map one object per row
	 * @param args
	 *            arguments to bind to the query (leaving it to the
	 *            PreparedStatement to guess the corresponding SQL type); may
	 *            also contain
	 *            {@link org.springframework.jdbc.core.SqlParameterValue}
	 *            objects which indicate not only the argument value but also
	 *            the SQL type and optionally the scale
	 * @return the single mapped object
	 * @throws DataAccessException
	 *             if the query fails
	 * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
	 *             if the query does not return exactly one row
	 */
    <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args);

    /**
	 * Execute a query for a result object, given static SQL.
	 * <p>
	 * Uses a JDBC Statement, not a PreparedStatement. If you want to execute a
	 * static query with a PreparedStatement, use the overloaded
	 * {@link #queryForObject(String, Class, Object...)} method with
	 * {@code null} as argument array.
	 * <p>
	 * This method is useful for running static SQL with a known outcome. The
	 * query is expected to be a single row/single column query; the returned
	 * result will be directly mapped to the corresponding object type.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            SQL query to execute
	 * @param requiredType
	 *            the type that the result object is expected to match
	 * @return the result object of the required type, or {@code null} in case
	 *         of SQL NULL
	 * @throws DataAccessException
	 *             if there is any problem executing the query
	 * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
	 *             if the query does not return exactly one row, or does not
	 *             return exactly one column in that row
	 * @see #queryForObject(String, Class, Object...)
	 */
    <T> T queryForObject(String sql, Class<T> requiredType);

    /**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, expecting a result object.
	 * <p>
	 * The query is expected to be a single row/single column query; the
	 * returned result will be directly mapped to the corresponding object type.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            SQL query to execute
	 * @param requiredType
	 *            the type that the result object is expected to match
	 * @param args
	 *            arguments to bind to the query (leaving it to the
	 *            PreparedStatement to guess the corresponding SQL type); may
	 *            also contain
	 *            {@link org.springframework.jdbc.core.SqlParameterValue}
	 *            objects which indicate not only the argument value but also
	 *            the SQL type and optionally the scale
	 * @return the result object of the required type, or {@code null} in case
	 *         of SQL NULL
	 * @throws DataAccessException
	 *             if the query fails
	 * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
	 *             if the query does not return exactly one row, or does not
	 *             return exactly one column in that row
	 * @see #queryForObject(String, Class)
	 */
    <T> T queryForObject(String sql, Class<T> requiredType, Object... args);

    /**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, expecting a result list.
	 * <p>
	 * The results will be mapped to a List (one entry for each row) of result
	 * objects, each of them matching the specified element type.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            SQL query to execute
	 * @param elementType
	 *            the required type of element in the result list (for example,
	 *            {@code Integer.class})
	 * @param args
	 *            arguments to bind to the query (leaving it to the
	 *            PreparedStatement to guess the corresponding SQL type); may
	 *            also contain {@link SqlParameterValue} objects which indicate
	 *            not only the argument value but also the SQL type and
	 *            optionally the scale
	 * @return a List of objects that match the specified element type
	 * @throws DataAccessException
	 *             if the query fails
	 * @see #queryForList(String, Class)
	 * @see SingleColumnRowMapper
	 */
    <T> List<T> queryForList(String sql, Class<T> elementType, Object... args);

    /**
     * Execute a query for a SqlRowSet, given static SQL.
     * <p>Uses a JDBC Statement, not a PreparedStatement. If you want to
     * execute a static query with a PreparedStatement, use the overloaded
     * {@code queryForRowSet} method with {@code null} as argument array.
     * <p>The results will be mapped to an SqlRowSet which holds the data in a
     * disconnected fashion. This wrapper will translate any SQLExceptions thrown.
     * <p>Note that that, for the default implementation, JDBC RowSet support needs to
     * be available at runtime: by default, Sun's {@code com.sun.rowset.CachedRowSetImpl}
     * class is used, which is part of JDK 1.5+ and also available separately as part of
     * Sun's JDBC RowSet Implementations download (rowset.jar).
     * @param sql SQL query to execute
     * @return a SqlRowSet representation (possibly a wrapper around a
     * {@code javax.sql.rowset.CachedRowSet})
     * @throws DataAccessException if there is any problem executing the query
     * @see #queryForRowSet(String, Object[])
     * @see org.springframework.jdbc.core.SqlRowSetResultSetExtractor
     * @see javax.sql.rowset.CachedRowSet
     */
    SqlRowSet queryForRowSet(String sql);

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a SqlRowSet.
     * <p>The results will be mapped to an SqlRowSet which holds the data in a
     * disconnected fashion. This wrapper will translate any SQLExceptions thrown.
     * <p>Note that that, for the default implementation, JDBC RowSet support needs to
     * be available at runtime: by default, Sun's {@code com.sun.rowset.CachedRowSetImpl}
     * class is used, which is part of JDK 1.5+ and also available separately as part of
     * Sun's JDBC RowSet Implementations download (rowset.jar).
     * @param sql SQL query to execute
     * @param args arguments to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type);
     * may also contain {@link org.springframework.jdbc.core.SqlParameterValue} objects which indicate not
     * only the argument value but also the SQL type and optionally the scale
     * @return a SqlRowSet representation (possibly a wrapper around a
     * {@code javax.sql.rowset.CachedRowSet})
     * @throws DataAccessException if there is any problem executing the query
     * @see #queryForRowSet(String)
     * @see org.springframework.jdbc.core.SqlRowSetResultSetExtractor
     * @see javax.sql.rowset.CachedRowSet
     */
    SqlRowSet queryForRowSet(String sql, Object... args);

    /**
     * Issue a single SQL update operation (such as an insert, update or delete statement)
     * via a prepared statement, binding the given arguments.
     * @param sql SQL containing bind parameters
     * @param args arguments to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type);
     * may also contain {@link org.springframework.jdbc.core.SqlParameterValue} objects which indicate not
     * only the argument value but also the SQL type and optionally the scale
     * @return the number of rows affected
     * @throws DataAccessException if there is any problem issuing the update
     */
    int update(String sql, Object... args);

    /**
     * Issue an update statement using a PreparedStatementSetter to set bind parameters, with given SQL.
     * Simpler than using a PreparedStatementCreator as this method will create the PreparedStatement: The PreparedStatementSetter just needs to set parameters.
     * @param sql SQL containing bind parameters
     * @param pss helper that sets bind parameters. If this is null we run an update with static SQL.
     * @return the number of rows affected
     */
    int update(String sql, PreparedStatementSetter pss);

    /**
     * Does simple batch update (without batch size).
     * @param sql the sql
     * @param pss the setter
     * @return keys
     * @throws DataAccessException from JdbcTemplate
     */
    int[] batchUpdate(String sql, final BatchPreparedStatementSetter pss);

    /**
     * Execute multiple batches using the supplied SQL statement with the collect of supplied arguments.
     * The arguments' values will be set using the ParameterizedPreparedStatementSetter.
     * Each batch should be of size indicated in 'batchSize'.
     *
     * @param sql the SQL statement to execute
     * @param batchArgs the List of Object arrays containing the batch of arguments for the query
     * @param batchSize batch size
     * @param pss ParameterizedPreparedStatementSetter to use
     * @return an array containing for each batch another array containing the numbers of rows affected by each update in the batch
     */
    <T> int[][] batchUpdate(String sql, Collection<T> batchArgs, int batchSize, ParameterizedPreparedStatementSetter<T> pss);

    /**
	 * Update no context.
	 *
	 * @param sql
	 *            the sql
	 * @param args
	 *            the args
	 * @return the int
	 * @throws DataAccessException
	 *             the data access exception
	 */
    int updateNoContext(String sql, Object... args);

    /**
	 * Calls {@link JdbcTemplate#execute(String, PreparedStatementCallback)},
	 * possibly setting transaction context and doing log output.
	 *
	 * @param <T>
	 *            the generic type
	 * @param sql
	 *            sql to execute
	 * @param action
	 *            callback to call
	 * @return result
	 * @throws DataAccessException
	 *             the data access exception
	 */
    <T> T execute(String sql, PreparedStatementCallback<T> action);

    /**
	 * Execute.
	 *
	 * @param sql
	 *            the sql
	 * @throws DataAccessException
	 *             the data access exception
	 */
    void execute(String sql);
}
