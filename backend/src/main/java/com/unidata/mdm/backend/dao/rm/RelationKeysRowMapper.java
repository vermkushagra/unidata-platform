/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.RelationKeysPO;


/**
 * @author Mikhail Mikhailov
 * Mapper class for relation keys.
 */
public class RelationKeysRowMapper implements RowMapper<RelationKeysPO> {
    /**
     * Default row mapper.
     */
    public static final RelationKeysRowMapper DEFAULT_ROW_MAPPER = new RelationKeysRowMapper();
    /**
     * Default first result extractor.
     */
    public static final ResultSetExtractor<RelationKeysPO> DEFAULT_FIRST_RESULT_EXTRACTOR
        = rs -> rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;
    /**
     * Constructor.
     */
    private RelationKeysRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationKeysPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        RelationKeysPO keys = new RelationKeysPO();

        // 1. Etalon
        keys.setEtalonId(rs.getString(RelationKeysPO.FIELD_ETALON_ID));
        keys.setEtalonName(rs.getString(RelationKeysPO.FIELD_ETALON_NAME));

        String statusAsString = rs.getString(RelationKeysPO.FIELD_ETALON_STATUS);
        keys.setEtalonStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        String stateAsString = rs.getString(RelationKeysPO.FIELD_ETALON_STATE);
        keys.setEtalonState(stateAsString != null ? ApprovalState.valueOf(stateAsString) : null);

        // 2. Origin
        keys.setOriginId(rs.getString(RelationKeysPO.FIELD_ORIGIN_ID));
        keys.setOriginRevision(rs.getInt(RelationKeysPO.FIELD_ORIGIN_REVISION));
        keys.setOriginName(rs.getString(RelationKeysPO.FIELD_ORIGIN_NAME));
        keys.setOriginSourceSystem(rs.getString(RelationKeysPO.FIELD_ORIGIN_SOURCE_SYSTEM));

        statusAsString = rs.getString(RelationKeysPO.FIELD_ORIGIN_STATUS);
        keys.setOriginStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        // 3. From side
        // Etalon
        keys.setEtalonIdFrom(rs.getString(RelationKeysPO.FIELD_ETALON_ID_FROM));
        keys.setEtalonFromName(rs.getString(RelationKeysPO.FIELD_ETALON_FROM_NAME));

        statusAsString = rs.getString(RelationKeysPO.FIELD_ETALON_FROM_STATUS);
        keys.setEtalonFromStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        stateAsString = rs.getString(RelationKeysPO.FIELD_ETALON_FROM_STATE);
        keys.setEtalonFromState(stateAsString != null ? ApprovalState.valueOf(stateAsString) : null);

        // Origin
        keys.setOriginIdFrom(rs.getString(RelationKeysPO.FIELD_ORIGIN_ID_FROM));
        keys.setOriginFromExternalId(rs.getString(RelationKeysPO.FIELD_ORIGIN_FROM_EXTERNAL_ID));
        keys.setOriginFromName(rs.getString(RelationKeysPO.FIELD_ORIGIN_FROM_NAME));
        keys.setOriginFromSourceSystem(rs.getString(RelationKeysPO.FIELD_ORIGIN_FROM_SOURCE_SYSTEM));

        statusAsString = rs.getString(RelationKeysPO.FIELD_ORIGIN_FROM_STATUS);
        keys.setOriginFromStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        // 4. To side
        // Etalon
        keys.setEtalonIdTo(rs.getString(RelationKeysPO.FIELD_ETALON_ID_TO));
        keys.setEtalonToName(rs.getString(RelationKeysPO.FIELD_ETALON_TO_NAME));

        statusAsString = rs.getString(RelationKeysPO.FIELD_ETALON_TO_STATUS);
        keys.setEtalonToStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        stateAsString = rs.getString(RelationKeysPO.FIELD_ETALON_TO_STATE);
        keys.setEtalonToState(stateAsString != null ? ApprovalState.valueOf(stateAsString) : null);

        // Origin
        keys.setOriginIdTo(rs.getString(RelationKeysPO.FIELD_ORIGIN_ID_TO));
        keys.setOriginToExternalId(rs.getString(RelationKeysPO.FIELD_ORIGIN_TO_EXTERNAL_ID));
        keys.setOriginToName(rs.getString(RelationKeysPO.FIELD_ORIGIN_TO_NAME));
        keys.setOriginToSourceSystem(rs.getString(RelationKeysPO.FIELD_ORIGIN_TO_SOURCE_SYSTEM));

        statusAsString = rs.getString(RelationKeysPO.FIELD_ORIGIN_TO_STATUS);
        keys.setOriginToStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        return keys;
    }

}
