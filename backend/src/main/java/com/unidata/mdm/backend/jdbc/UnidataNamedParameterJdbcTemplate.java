package com.unidata.mdm.backend.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Yashin. Created on 01.04.2015.
 */
public interface UnidataNamedParameterJdbcTemplate {

    /**
     * Issue an update via a prepared statement, binding the given arguments.
     * @param sql SQL containing named parameters
     * @param paramSource container of arguments and SQL types to bind to the query
     * @return the number of rows affected
     * @throws org.springframework.dao.DataAccessException if there is any problem issuing the update
     */
    int update(String sql, SqlParameterSource paramSource) throws DataAccessException;

    /**
     * Issue an update via a prepared statement, binding the given arguments.
     * @param sql SQL containing named parameters
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @return the number of rows affected
     * @throws org.springframework.dao.DataAccessException if there is any problem issuing the update
     */
    int update(String sql, Map<String, ?> paramMap) throws DataAccessException;

    /**
     * Issue an update via a prepared statement, binding the given arguments,
     * returning generated keys.
     * @param sql SQL containing named parameters
     * @param paramSource container of arguments and SQL types to bind to the query
     * @param generatedKeyHolder KeyHolder that will hold the generated keys
     * @return the number of rows affected
     * @throws org.springframework.dao.DataAccessException if there is any problem issuing the update
     * @see org.springframework.jdbc.core.namedparam.MapSqlParameterSource
     * @see org.springframework.jdbc.support.GeneratedKeyHolder
     */
    int update(String sql, SqlParameterSource paramSource, KeyHolder generatedKeyHolder)
            throws DataAccessException;

    /**
     * Issue an update via a prepared statement, binding the given arguments,
     * returning generated keys.
     * @param sql SQL containing named parameters
     * @param paramSource container of arguments and SQL types to bind to the query
     * @param generatedKeyHolder KeyHolder that will hold the generated keys
     * @param keyColumnNames names of the columns that will have keys generated for them
     * @return the number of rows affected
     * @throws org.springframework.dao.DataAccessException if there is any problem issuing the update
     * @see org.springframework.jdbc.core.namedparam.MapSqlParameterSource
     * @see org.springframework.jdbc.support.GeneratedKeyHolder
     */
    int update(String sql, SqlParameterSource paramSource, KeyHolder generatedKeyHolder, String[] keyColumnNames)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping each row to a Java object
     * via a RowMapper.
     * @param sql SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @param rowMapper object that will map one object per row
     * @return the result List, containing mapped objects
     * @throws org.springframework.dao.DataAccessException if the query fails
     */
    <T> List<T> query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping each row to a Java object
     * via a RowMapper.
     * @param sql SQL query to execute
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @param rowMapper object that will map one object per row
     * @return the result List, containing mapped objects
     * @throws org.springframework.dao.DataAccessException if the query fails
     */
    <T> List<T> query(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL,
     * mapping each row to a Java object via a RowMapper.
     * <p>Note: In contrast to the JdbcOperations method with the same signature,
     * this query variant always uses a PreparedStatement. It is effectively
     * equivalent to a query call with an empty parameter Map.
     * @param sql SQL query to execute
     * @param rowMapper object that will map one object per row
     * @return the result List, containing mapped objects
     * @throws org.springframework.dao.DataAccessException if the query fails
     */
    <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result list.
     * <p>The results will be mapped to a List (one entry for each row) of
     * result objects, each of them matching the specified element type.
     * @param sql SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @param elementType the required type of element in the result list
     * (for example, {@code Integer.class})
     * @return a List of objects that match the specified element type
     * @throws org.springframework.dao.DataAccessException if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(String, Class)
     * @see org.springframework.jdbc.core.SingleColumnRowMapper
     */
    <T> List<T> queryForList(String sql, SqlParameterSource paramSource, Class<T> elementType)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result list.
     * <p>The results will be mapped to a List (one entry for each row) of
     * result objects, each of them matching the specified element type.
     * @param sql SQL query to execute
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @param elementType the required type of element in the result list
     * (for example, {@code Integer.class})
     * @return a List of objects that match the specified element type
     * @throws org.springframework.dao.DataAccessException if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(String, Class)
     * @see org.springframework.jdbc.core.SingleColumnRowMapper
     */
    <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result list.
     * <p>The results will be mapped to a List (one entry for each row) of
     * Maps (one entry for each column, using the column name as the key).
     * Each element in the list will be of the form returned by this interface's
     * {@code queryForMap} methods.
     * @param sql SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @return a List that contains a Map per row
     * @throws org.springframework.dao.DataAccessException if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(String)
     */
    List<Map<String, Object>> queryForList(String sql, SqlParameterSource paramSource) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result list.
     * <p>The results will be mapped to a List (one entry for each row) of
     * Maps (one entry for each column, using the column name as the key).
     * Each element in the list will be of the form returned by this interface's
     * {@code queryForMap} methods.
     * @param sql SQL query to execute
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @return a List that contains a Map per row
     * @throws org.springframework.dao.DataAccessException if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForList(String)
     */
    List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping a single result row to a
     * Java object via a RowMapper.
     * @param sql SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @param rowMapper object that will map one object per row
     * @return the single mapped object
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     * if the query does not return exactly one row, or does not return exactly
     * one column in that row
     * @throws org.springframework.dao.DataAccessException if the query fails
     */
    <T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, mapping a single result row to a
     * Java object via a RowMapper.
     * @param sql SQL query to execute
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @param rowMapper object that will map one object per row
     * @return the single mapped object
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     * if the query does not return exactly one row, or does not return exactly
     * one column in that row
     * @throws org.springframework.dao.DataAccessException if the query fails
     */
    <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result object.
     * <p>The query is expected to be a single row/single column query; the returned
     * result will be directly mapped to the corresponding object type.
     * @param sql SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @param requiredType the type that the result object is expected to match
     * @return the result object of the required type, or {@code null} in case of SQL NULL
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     * if the query does not return exactly one row, or does not return exactly
     * one column in that row
     * @throws org.springframework.dao.DataAccessException if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForObject(String, Class)
     */
    <T> T queryForObject(String sql, SqlParameterSource paramSource, Class<T> requiredType)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result object.
     * <p>The query is expected to be a single row/single column query; the returned
     * result will be directly mapped to the corresponding object type.
     * @param sql SQL query to execute
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @param requiredType the type that the result object is expected to match
     * @return the result object of the required type, or {@code null} in case of SQL NULL
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     * if the query does not return exactly one row, or does not return exactly
     * one column in that row
     * @throws org.springframework.dao.DataAccessException if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForObject(String, Class)
     */
    <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result Map.
     * <p>The query is expected to be a single row query; the result row will be
     * mapped to a Map (one entry for each column, using the column name as the key).
     * @param sql SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @return the result Map (one entry for each column, using the column name as the key)
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     * if the query does not return exactly one row
     * @throws org.springframework.dao.DataAccessException if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForMap(String)
     * @see org.springframework.jdbc.core.ColumnMapRowMapper
     */
    Map<String, Object> queryForMap(String sql, SqlParameterSource paramSource) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a
     * list of arguments to bind to the query, expecting a result Map.
     * The queryForMap() methods defined by this interface are appropriate
     * when you don't have a domain model. Otherwise, consider using
     * one of the queryForObject() methods.
     * <p>The query is expected to be a single row query; the result row will be
     * mapped to a Map (one entry for each column, using the column name as the key).
     * @param sql SQL query to execute
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @return the result Map (one entry for each column, using the column name as the key)
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     * if the query does not return exactly one row
     * @throws org.springframework.dao.DataAccessException if the query fails
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForMap(String)
     * @see org.springframework.jdbc.core.ColumnMapRowMapper
     */
    Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws DataAccessException;

    /**
     * Executes a batch using the supplied SQL statement with the batch of supplied arguments.
     * @param sql the SQL statement to execute
     * @param batchValues the array of Maps containing the batch of arguments for the query
     * @return an array containing the numbers of rows affected by each update in the batch
     */
    int[] batchUpdate(String sql, Map<String, ?>[] batchValues);

    /**
     * Execute a batch using the supplied SQL statement with the batch of supplied arguments.
     * @param sql the SQL statement to execute
     * @param batchArgs the array of {@link SqlParameterSource} containing the batch of arguments for the query
     * @return an array containing the numbers of rows affected by each update in the batch
     */
    int[] batchUpdate(String sql, SqlParameterSource[] batchArgs);

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
     * @param paramSource container of arguments to bind to the query
     * @return a SqlRowSet representation (possibly a wrapper around a
     * {@code javax.sql.rowset.CachedRowSet})
     * @throws org.springframework.dao.DataAccessException if there is any problem executing the query
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForRowSet(String)
     * @see org.springframework.jdbc.core.SqlRowSetResultSetExtractor
     * @see javax.sql.rowset.CachedRowSet
     */
    SqlRowSet queryForRowSet(String sql, SqlParameterSource paramSource) throws DataAccessException;

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
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @return a SqlRowSet representation (possibly a wrapper around a
     * {@code javax.sql.rowset.CachedRowSet})
     * @throws org.springframework.dao.DataAccessException if there is any problem executing the query
     * @see org.springframework.jdbc.core.JdbcTemplate#queryForRowSet(String)
     * @see org.springframework.jdbc.core.SqlRowSetResultSetExtractor
     * @see javax.sql.rowset.CachedRowSet
     */
    SqlRowSet queryForRowSet(String sql, Map<String, ?> paramMap) throws DataAccessException;

    /**
     * Execute a JDBC data access operation, implemented as callback action
     * working on a JDBC PreparedStatement. This allows for implementing arbitrary
     * data access operations on a single Statement, within Spring's managed
     * JDBC environment: that is, participating in Spring-managed transactions
     * and converting JDBC SQLExceptions into Spring's DataAccessException hierarchy.
     * <p>The callback action can return a result object, for example a
     * domain object or a collection of domain objects.
     * @param sql SQL to execute
     * @param paramSource container of arguments to bind to the query
     * @param action callback object that specifies the action
     * @return a result object returned by the action, or {@code null}
     * @throws DataAccessException if there is any problem
     */
    <T> T execute(String sql, SqlParameterSource paramSource, PreparedStatementCallback<T> action)
            throws DataAccessException;

    /**
     * Execute a JDBC data access operation, implemented as callback action
     * working on a JDBC PreparedStatement. This allows for implementing arbitrary
     * data access operations on a single Statement, within Spring's managed
     * JDBC environment: that is, participating in Spring-managed transactions
     * and converting JDBC SQLExceptions into Spring's DataAccessException hierarchy.
     * <p>The callback action can return a result object, for example a
     * domain object or a collection of domain objects.
     * @param sql SQL to execute
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @param action callback object that specifies the action
     * @return a result object returned by the action, or {@code null}
     * @throws DataAccessException if there is any problem
     */
    <T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action)
            throws DataAccessException;

    /**
     * Execute a JDBC data access operation, implemented as callback action
     * working on a JDBC PreparedStatement. This allows for implementing arbitrary
     * data access operations on a single Statement, within Spring's managed
     * JDBC environment: that is, participating in Spring-managed transactions
     * and converting JDBC SQLExceptions into Spring's DataAccessException hierarchy.
     * <p>The callback action can return a result object, for example a
     * domain object or a collection of domain objects.
     * @param sql SQL to execute
     * @param action callback object that specifies the action
     * @return a result object returned by the action, or {@code null}
     * @throws DataAccessException if there is any problem
     */
    <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, reading the ResultSet with a
     * ResultSetExtractor.
     * @param sql SQL query to execute
     * @param paramSource container of arguments to bind to the query
     * @param rse object that will extract results
     * @return an arbitrary result object, as returned by the ResultSetExtractor
     * @throws DataAccessException if the query fails
     */
    <T> T query(String sql, SqlParameterSource paramSource, ResultSetExtractor<T> rse)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL and a list
     * of arguments to bind to the query, reading the ResultSet with a
     * ResultSetExtractor.
     * @param sql SQL query to execute
     * @param paramMap map of parameters to bind to the query
     * (leaving it to the PreparedStatement to guess the corresponding SQL type)
     * @param rse object that will extract results
     * @return an arbitrary result object, as returned by the ResultSetExtractor
     * @throws org.springframework.dao.DataAccessException if the query fails
     */
    <T> T query(String sql, Map<String, ?> paramMap, ResultSetExtractor<T> rse)
            throws DataAccessException;

    /**
     * Query given SQL to create a prepared statement from SQL,
     * reading the ResultSet with a ResultSetExtractor.
     * <p>Note: In contrast to the JdbcOperations method with the same signature,
     * this query variant always uses a PreparedStatement. It is effectively
     * equivalent to a query call with an empty parameter Map.
     * @param sql SQL query to execute
     * @param rse object that will extract results
     * @return an arbitrary result object, as returned by the ResultSetExtractor
     * @throws org.springframework.dao.DataAccessException if the query fails
     */
    <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException;

}
