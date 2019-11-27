package org.unidata.mdm.core.dao.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import org.unidata.mdm.core.dao.rm.BinaryLargeObjectRowMapper;
import org.unidata.mdm.core.dao.rm.CharacterLargeObjectRowMapper;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.po.BinaryLargeObjectPO;
import org.unidata.mdm.core.po.CharacterLargeObjectPO;
import org.unidata.mdm.core.po.LargeObjectPO;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.dao.LargeObjectsDao;
import org.unidata.mdm.system.dao.impl.BaseDAOImpl;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov
 * DAO for large objects implementation.
 */
@Repository
public class LargeObjectsDaoImpl extends BaseDAOImpl implements LargeObjectsDao {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LargeObjectsDaoImpl.class);
    /**
     * Delete unused cdata values
     */
    private final String deleteUnusedCharacterDataSQL;
    /**
     * Delete unused bdata values
     */
    private final String deleteUnusedBinaryDataSQL;
    /**
     * Returns id for combnation id + field for binary data.
     */
    private final String checkBinaryDataIdSQL;
    /**
     * Returns id for combnation id + field for character data.
     */
    private final String checkCharacterDataIdSQL;
    /**
     * Check exist cdata value by id
     */
    private final String checkCharacterDataSQL;
    /**
     * Check exist bdata value by id
     */
    private final String checkBinaryDataSQL;
    /**
     * Fetch data.
     */
    private final String fetchBinaryLargeObjectByIdSQL;
    /**
     * Fetch data.
     */
    private final String fetchCharacterLargeObjectByIdSQL;
    /**
     * Updates existing binary record.
     */
    private final String updateBinaryLargeObjectSQL;
    /**
     * Updates existing character record.
     */
    private final String updateCharacterLargeObjectSQL;
    /**
     * Inserts binary data.
     */
    private final String insertBinaryLargeObjectSQL;
    /**
     * Inserts character data.
     */
    private final String insertCharacterLargeObjectSQL;
    /**
     * Deletes blob data.
     */
    private final String deleteBinaryLargeObjectSQL;
    /**
     * Deletes char data.
     */
    private final String deleteCharacterLargeObjectSQL;
    /**
     * Activates BLOB.
     */
    private final String activateBinaryLargeObjectSQL;
    /**
     * Activates CLOB.
     */
    private final String activateCharacterLargeObjectSQL;
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
    public LargeObjectsDaoImpl(
            @Qualifier("coreDataSource") final DataSource dataSource,
            @Qualifier("binary-data-sql") final Properties sql
    ) {
        super(dataSource);
        deleteUnusedBinaryDataSQL = sql.getProperty("deleteUnusedBinaryDataSQL");
        deleteUnusedCharacterDataSQL = sql.getProperty("deleteUnusedCharacterDataSQL");
        checkCharacterDataSQL = sql.getProperty("checkCharacterDataSQL");
        checkBinaryDataSQL = sql.getProperty("checkBinaryDataSQL");
        checkBinaryDataIdSQL = sql.getProperty("checkBinaryDataIdSQL");
        checkCharacterDataIdSQL = sql.getProperty("checkCharacterDataIdSQL");
        fetchBinaryLargeObjectByIdSQL = sql.getProperty("fetchBinaryLargeObjectByIdSQL");
        fetchCharacterLargeObjectByIdSQL = sql.getProperty("fetchCharacterLargeObjectByIdSQL");
        updateBinaryLargeObjectSQL = sql.getProperty("updateBinaryLargeObjectSQL");
        updateCharacterLargeObjectSQL = sql.getProperty("updateCharacterLargeObjectSQL");
        insertBinaryLargeObjectSQL = sql.getProperty("insertBinaryLargeObjectSQL");
        insertCharacterLargeObjectSQL = sql.getProperty("insertCharacterLargeObjectSQL");
        deleteBinaryLargeObjectSQL = sql.getProperty("deleteBinaryLargeObjectSQL");
        deleteCharacterLargeObjectSQL = sql.getProperty("deleteCharacterLargeObjectSQL");
        activateBinaryLargeObjectSQL = sql.getProperty("activateBinaryLargeObjectSQL");
        activateCharacterLargeObjectSQL = sql.getProperty("activateCharacterLargeObjectSQL");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LargeObjectPO fetchLargeObjectById(String id, boolean isBinary) {
        UUID val = UUID.fromString(id);
        if (isBinary) {
            return jdbcTemplate.query(fetchBinaryLargeObjectByIdSQL, BLOB_SINGLE_RESULT_EXTRACTOR, val);
        } else {
            return jdbcTemplate.query(fetchCharacterLargeObjectByIdSQL, CLOB_SINGLE_RESULT_EXTRACTOR, val);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public boolean upsertLargeObject(LargeObjectPO lob) throws IOException {

        int updatesCount = 0;
        final String checkIdSQL;
        final String updateSQL;
        final String insertSQL;

        if (lob.isBinary()) {
            checkIdSQL = checkBinaryDataIdSQL;
            updateSQL = updateBinaryLargeObjectSQL;
            insertSQL = insertBinaryLargeObjectSQL;
        } else {
            checkIdSQL = checkCharacterDataIdSQL;
            updateSQL = updateCharacterLargeObjectSQL;
            insertSQL = insertCharacterLargeObjectSQL;
        }

        final UUID rid = jdbcTemplate.queryForObject(checkIdSQL, UUID.class, UUID.fromString(lob.getId()), lob.getField());
        if (rid != null) {
            updatesCount = jdbcTemplate.execute(updateSQL, new LobCreatingPreparedStatementCallback(lob, false, lobHandler));
            if (updatesCount == 0) {
                throw new PlatformFailureException(
                        "Large object attachment update failed. The object [{}] has already been activated and has versions.",
                        CoreExceptionIds.EX_DATA_INVALID_LOB_UPDATE,
                        rid);
            }
        } else {
            updatesCount = jdbcTemplate.execute(insertSQL, new LobCreatingPreparedStatementCallback(lob, true, lobHandler));
        }

        return updatesCount == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteLargeObject(String id, String field, boolean isBinary) {
        String sql = isBinary ? deleteBinaryLargeObjectSQL : deleteCharacterLargeObjectSQL;
        return jdbcTemplate.update(sql, id, field) == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkLargeObject(String id, boolean isBinary) {
        final String sql = isBinary ? checkBinaryDataSQL : checkCharacterDataSQL;
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean activateLargeObjects(Collection<LargeObjectPO> spec) {

        if (CollectionUtils.isEmpty(spec)) {
            return false;
        }

        Map<Boolean, List<LargeObjectPO>> grouped = spec.stream().collect(Collectors.groupingBy(LargeObjectPO::isBinary));
        for (Entry<Boolean, List<LargeObjectPO>> entry : grouped.entrySet()) {

            String sql = entry.getKey() ? activateBinaryLargeObjectSQL : activateCharacterLargeObjectSQL;
            jdbcTemplate.batchUpdate(sql, entry.getValue(), entry.getValue().size(), (ps, obj) -> {

                ps.setObject(1, obj.getRecordId() == null ? null :UUID.fromString(obj.getRecordId()));
                ps.setObject(2, obj.getClassifierId() == null ? null :UUID.fromString(obj.getClassifierId()));
                ps.setObject(3, obj.getEventId() == null ? null : UUID.fromString(obj.getEventId()));
                ps.setString(4, ApprovalState.APPROVED.name());
                ps.setObject(5, UUID.fromString(obj.getId()));
                ps.setString(6, ApprovalState.PENDING.name());
            });
        }

        return true;
    }

    @Override
    public long cleanUnusedBinaryData(long maxLifetime){
        Timestamp dateForDelete = new Timestamp(Instant.now().minus(maxLifetime, ChronoUnit.MINUTES).toEpochMilli());
        long deleted =  jdbcTemplate.update(deleteUnusedBinaryDataSQL, ApprovalState.PENDING.name(), dateForDelete);
        deleted +=  jdbcTemplate.update(deleteUnusedCharacterDataSQL, ApprovalState.PENDING.name(), dateForDelete);
        return deleted;
    }
    /**
     * @author Mikhail Mikhailov
     * LOB handler jdbc template support.
     */
    private class LobCreatingPreparedStatementCallback extends AbstractLobCreatingPreparedStatementCallback {
        /**
         * The PO to handle.
         */
        private final LargeObjectPO po;
        /**
         * Insert or update action.
         */
        private final boolean insert;
        /**
         * Constructor.
         * @param po the PO to handle
         * @param lobHandler the handler for data
         */
        LobCreatingPreparedStatementCallback(LargeObjectPO po, boolean insert, LobHandler lobHandler) {
            super(lobHandler);
            this.po = po;
            this.insert = insert;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {

            try {

                if (insert) {

                    ps.setObject(1, UUID.fromString(po.getId()));
                    if (po.isBinary()) {
                        lobCreator.setBlobAsBinaryStream(ps, 2, po.getData(), po.getData().available());
                    } else {
                        lobCreator.setClobAsCharacterStream(ps, 2, new InputStreamReader(po.getData()), po.getData().available());
                    }

                    ps.setString(3, po.getFileName());
                    ps.setString(4, po.getMimeType());
                    ps.setString(5, po.getField());
                    ps.setTimestamp(6, new Timestamp(po.getCreateDate().getTime()));
                    ps.setString(7, po.getCreatedBy());
                    ps.setLong(8, po.getSize());
                    ps.setString(9, po.getState().name());
                    ps.setObject(10, po.getEventId() != null ? UUID.fromString(po.getEventId()) : null);

                } else {

                    if (po.isBinary()) {
                        lobCreator.setBlobAsBinaryStream(ps, 1, po.getData(), po.getData().available());
                    } else {
                        lobCreator.setClobAsCharacterStream(ps, 1, new InputStreamReader(po.getData()), po.getData().available());
                    }

                    ps.setString(2, po.getFileName());
                    ps.setString(3, po.getMimeType());
                    ps.setTimestamp(4, new Timestamp(po.getUpdateDate().getTime()));
                    ps.setString(5, po.getUpdatedBy());
                    ps.setObject(6, UUID.fromString(po.getId()));
                    ps.setString(7, ApprovalState.PENDING.name());
                    ps.setObject(8, po.getEventId() != null ? UUID.fromString(po.getEventId()) : null);
                }

            } catch (IOException ioe) {
                final String message = "I/O exception caught, while saving LOB.";
                LOGGER.error(message, ioe);
                throw new DataAccessResourceFailureException(message, ioe);
            }
        }
    }
}
