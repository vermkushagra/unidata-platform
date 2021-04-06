package com.unidata.mdm.backend.dao.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.dao.LargeObjectsDao;
import com.unidata.mdm.backend.dao.rm.BinaryLargeObjectRowMapper;
import com.unidata.mdm.backend.dao.rm.CharacterLargeObjectRowMapper;
import com.unidata.mdm.backend.po.BinaryLargeObjectPO;
import com.unidata.mdm.backend.po.CharacterLargeObjectPO;
import com.unidata.mdm.backend.po.LargeObjectPO;

/**
 * @author Mikhail Mikhailov
 * DAO for large objects implementation.
 */
@Repository
public class LargeObjectsDaoImpl extends AbstractDaoImpl implements LargeObjectsDao {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LargeObjectsDaoImpl.class);

    /**
     * Delete unused cdata values
     */
    private final String deleteUnusedCData;

    /**
     * Delete unused bdata values
     */
    private final String deleteUnusedBData;
    /**
     * Check exist cdata value by id
     */
    private final String checkCData;

    /**
     * Check exist bdata value by id
     */
    private final String checkBData;

    /**
     * LOB handler.
     */
    private LobHandler lobHandler = new DefaultLobHandler();

    /**
     * Extracts first result or returns null.
     */
    private static final ResultSetExtractor<BinaryLargeObjectPO> BLOB_SINGLE_RESULT_EXTRACTOR
        = rs -> rs.next() ? BinaryLargeObjectRowMapper.DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Extracts first result or returns null.
     */
    private static final ResultSetExtractor<CharacterLargeObjectPO> CLOB_SINGLE_RESULT_EXTRACTOR
        = rs -> rs.next() ? CharacterLargeObjectRowMapper.DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * External utility support.
     */
    @Autowired
    public LargeObjectsDaoImpl(DataSource dataSource, @Qualifier("binary-data-sql") Properties sql) {
        super(dataSource);
        deleteUnusedBData = sql.getProperty("deleteUnusedBData");
        deleteUnusedCData = sql.getProperty("deleteUnusedCData");
        checkCData = sql.getProperty("checkCData");
        checkBData = sql.getProperty("checkBData");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LargeObjectPO fetchLargeObjectById(String id, boolean isBinary) {
        final String sql = String.format(new StringBuilder()
                .append("select %1$s, %2$s, %3$s, %4$s, %5$s, %6$s, %7$s, %8$s, %9$s, %10$s, %11$s, %12$s, %13$s::text, %14$s::text ")
                .append("from %15$s where %14$s = :%14$s::uuid")
                .toString(),
                LargeObjectPO.FIELD_DATA,
                LargeObjectPO.FIELD_FILE_NAME,
                LargeObjectPO.FIELD_MIME_TYPE,
                LargeObjectPO.FIELD_CREATE_DATE,
                LargeObjectPO.FIELD_UPDATE_DATE,
                LargeObjectPO.FIELD_CREATED_BY,
                LargeObjectPO.FIELD_UPDATED_BY,
                LargeObjectPO.FIELD_ETALON_ID,
                LargeObjectPO.FIELD_ORIGIN_ID,
                LargeObjectPO.FIELD_FIELD,
                LargeObjectPO.FIELD_SIZE,
                LargeObjectPO.FIELD_STATUS,
                LargeObjectPO.FIELD_EVENT_ID,
                LargeObjectPO.FIELD_ID,
                isBinary ? BinaryLargeObjectPO.TABLE_NAME : CharacterLargeObjectPO.TABLE_NAME);

        final Map<String, Object> params = new HashMap<>();
        params.put(LargeObjectPO.FIELD_ID, id);

        if (isBinary) {
            return namedJdbcTemplate.query(sql, params, BLOB_SINGLE_RESULT_EXTRACTOR);
        } else {
            return namedJdbcTemplate.query(sql, params, CLOB_SINGLE_RESULT_EXTRACTOR);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public boolean upsertLargeObject(LargeObjectPO lob, boolean isBinary)
            throws IOException {

        String sql = String.format("select coalesce((select %s::text from %s where %s = ?::uuid and %s = ?), null)",
                LargeObjectPO.FIELD_ID,
                isBinary ? BinaryLargeObjectPO.TABLE_NAME : CharacterLargeObjectPO.TABLE_NAME,
                LargeObjectPO.FIELD_ID,
                LargeObjectPO.FIELD_FIELD);

        int updatesCount = 0;
        final String rid = jdbcTemplate.queryForObject(sql, String.class, lob.getId(), lob.getField());
        if (rid != null) {
            sql = String.format("update %1$s set %2$s = ?, %3$s = ?, %4$s = ?, %5$s = ?, %6$s = ? where %7$s = ?::uuid and %8$s = ?::approval_state and %9$s = ?::uuid",
                    isBinary ? BinaryLargeObjectPO.TABLE_NAME : CharacterLargeObjectPO.TABLE_NAME,
                    LargeObjectPO.FIELD_DATA,
                    LargeObjectPO.FIELD_FILE_NAME,
                    LargeObjectPO.FIELD_MIME_TYPE,
                    LargeObjectPO.FIELD_UPDATE_DATE,
                    LargeObjectPO.FIELD_UPDATED_BY,
                    LargeObjectPO.FIELD_ID,
                    LargeObjectPO.FIELD_STATUS,
                    LargeObjectPO.FIELD_EVENT_ID);

            updatesCount = jdbcTemplate.execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                @Override
                protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
                    try {
                        if (isBinary) {
                            lobCreator.setBlobAsBinaryStream(ps, 1, lob.getData(), lob.getData().available());
                        } else {
                            lobCreator.setClobAsCharacterStream(ps, 1, new InputStreamReader(lob.getData()), lob.getData().available());
                        }
                    } catch (IOException ioe) {
                        LOGGER.error("I/O exception caught, while saving LOB.", ioe);
                        throw new DataAccessResourceFailureException("I/O exception caught, while saving LOB.", ioe);
                    }
                    ps.setString(2, lob.getFileName());
                    ps.setString(3, lob.getMimeType());
                    ps.setTimestamp(4, new Timestamp(lob.getUpdateDate().getTime()));
                    ps.setString(5, lob.getUpdatedBy());
                    ps.setString(6, rid);
                    ps.setString(7, ApprovalState.PENDING.name());
                    ps.setString(8, lob.getEventId());
                }
            });

            if (updatesCount == 0) {
                throw new DataProcessingException(
                        "Large object attachment update failed. The object [{}] has already been activated and has versions.",
                        ExceptionId.EX_DATA_INVALID_LOB_UPDATE,
                        rid);
            }

        } else {
            sql = String.format("insert into %s (%s, %s, %s, %s,  %s, %s, %s, %s, %s, %s) values (?::uuid, ?, ?, ?, ?, ?, ?, ?, ?::approval_state, ?::uuid)",
                    isBinary ? BinaryLargeObjectPO.TABLE_NAME : CharacterLargeObjectPO.TABLE_NAME,
                            LargeObjectPO.FIELD_ID,
                            LargeObjectPO.FIELD_DATA,
                            LargeObjectPO.FIELD_FILE_NAME,
                            LargeObjectPO.FIELD_MIME_TYPE,
                            LargeObjectPO.FIELD_FIELD,
                            LargeObjectPO.FIELD_CREATE_DATE,
                            LargeObjectPO.FIELD_CREATED_BY,
                            LargeObjectPO.FIELD_SIZE,
                            LargeObjectPO.FIELD_STATUS,
                            LargeObjectPO.FIELD_EVENT_ID);


            updatesCount = jdbcTemplate.execute(sql, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                @Override
                protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException, DataAccessException {
                    ps.setString(1, lob.getId());
                    try {
                        if (isBinary) {
                            lobCreator.setBlobAsBinaryStream(ps, 2, lob.getData(), lob.getData().available());
                        } else {
                            lobCreator.setClobAsCharacterStream(ps, 2, new InputStreamReader(lob.getData()), lob.getData().available());
                        }
                    } catch (IOException ioe) {
                        LOGGER.error("I/O exception caught, while saving LOB.", ioe);
                        throw new DataAccessResourceFailureException("I/O exception caught, while saving LOB.", ioe);
                    }
                    ps.setString(3, lob.getFileName());
                    ps.setString(4, lob.getMimeType());
                    ps.setString(5, lob.getField());
                    ps.setTimestamp(6, new Timestamp(lob.getCreateDate().getTime()));
                    ps.setString(7, lob.getCreatedBy());
                    ps.setLong(8, lob.getSize());
                    ps.setString(9, lob.getState().name());
                    ps.setString(10, lob.getEventId());
                }
            });
        }

        return updatesCount == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteLargeObject(String id, String field, boolean isBinary) {

        String sql = String.format("delete from %s where %s = ?::uuid and %s = ?",
                isBinary ? BinaryLargeObjectPO.TABLE_NAME : CharacterLargeObjectPO.TABLE_NAME,
                LargeObjectPO.FIELD_ID,
                LargeObjectPO.FIELD_FIELD);

        return jdbcTemplate.update(sql, id, field) == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkLargeObject(String id, boolean isBinary) {

        final String sql = isBinary ? checkBData : checkCData;

        final Map<String, Object> params = new HashMap<>();
        params.put(LargeObjectPO.FIELD_ID, id);
        return namedJdbcTemplate.queryForObject(sql, params, Boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ensureActive(String id, String parentId, boolean isOrigin, boolean isBinary) {
        String sql = String.format("update %1$s set %2$s = ?::uuid, %3$s = ?::approval_state where %4$s = ?::uuid and %3$s = ?::approval_state",
                isBinary ? BinaryLargeObjectPO.TABLE_NAME : CharacterLargeObjectPO.TABLE_NAME,
                isOrigin ? LargeObjectPO.FIELD_ORIGIN_ID : LargeObjectPO.FIELD_ETALON_ID,
                LargeObjectPO.FIELD_STATUS,
                LargeObjectPO.FIELD_ID);

        return jdbcTemplate.update(sql, parentId, ApprovalState.APPROVED.name(), id, ApprovalState.PENDING.name()) == 1;
    }

    @Override
    public long cleanUnusedBinaryData(long maxLifetime){
        Timestamp dateForDelete = new Timestamp(Instant.now().minus(maxLifetime, ChronoUnit.MINUTES).toEpochMilli());
        long deleted =  jdbcTemplate.update(deleteUnusedBData, ApprovalState.PENDING.name(), dateForDelete);
        deleted +=  jdbcTemplate.update(deleteUnusedCData, ApprovalState.PENDING.name(), dateForDelete);
        return deleted;
    }
}
