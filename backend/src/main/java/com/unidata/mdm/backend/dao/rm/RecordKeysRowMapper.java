/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.RecordKeysPO;


/**
 * @author Mikhail Mikhailov
 *
 */
public class RecordKeysRowMapper implements RowMapper<RecordKeysPO> {
    /**
     * Default reusable row mapper.
     */
    public static final RecordKeysRowMapper DEFAULT_ROW_MAPPER = new RecordKeysRowMapper();
    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<List<RecordKeysPO>> DEFAULT_FIRST_RESULT_EXTRACTOR
        = rs -> {
        boolean hasData = rs.next();
        if (!hasData) {
            return Collections.emptyList();
        }
        List<RecordKeysPO> result = new ArrayList<>();
        while (hasData) {
            result.add(DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()));
            hasData = rs.next();
        }
        return result;
    };
    /**
     * Constructor.
     */
    private RecordKeysRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeysPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        RecordKeysPO keys = new RecordKeysPO();

        keys.setEtalonId(rs.getString(RecordKeysPO.FIELD_ETALON_ID));
        keys.setEtalonName(rs.getString(RecordKeysPO.FIELD_ETALON_NAME));

        String etalonStatusString = rs.getString(RecordKeysPO.FIELD_ETALON_STATUS);
        String etalonStateString = rs.getString(RecordKeysPO.FIELD_ETALON_STATE);

        keys.setEtalonStatus(etalonStatusString != null ? RecordStatus.valueOf(etalonStatusString) : null);
        keys.setEtalonState(etalonStateString != null ? ApprovalState.valueOf(etalonStateString) : null);
        keys.setEtalonVersion(rs.getInt(RecordKeysPO.FIELD_ETALON_VERSION));
        keys.setEtalonGsn(rs.getLong(RecordKeysPO.FIELD_ETALON_GSN));

        keys.setOriginId(rs.getString(RecordKeysPO.FIELD_ORIGIN_ID));
        keys.setOriginName(rs.getString(RecordKeysPO.FIELD_ORIGIN_NAME));

        String originStatusString = rs.getString(RecordKeysPO.FIELD_ORIGIN_STATUS);
        keys.setOriginStatus(originStatusString != null ? RecordStatus.valueOf(originStatusString) : null);
        keys.setOriginVersion(rs.getInt(RecordKeysPO.FIELD_ORIGIN_VERSION));
        keys.setOriginSourceSystem(rs.getString(RecordKeysPO.FIELD_ORIGIN_SOURCE_SYSTEM));
        keys.setOriginExternalId(rs.getString(RecordKeysPO.FIELD_ORIGIN_EXTERNAL_ID));
        keys.setOriginGsn(rs.getLong(RecordKeysPO.FIELD_ORIGIN_GSN));
        keys.setOriginRevision(rs.getInt(RecordKeysPO.FIELD_ORIGIN_REVISION));

        boolean enrichment = rs.getBoolean(RecordKeysPO.FIELD_ENRICHMENT);
        keys.setEnrich(rs.wasNull() ? null : enrichment);

        boolean published = rs.getBoolean(RecordKeysPO.FIELD_HAS_APPROVED_REVISIONS);
        keys.setHasApprovedRevisions(rs.wasNull() ? null : published);

        return keys;
    }
}
