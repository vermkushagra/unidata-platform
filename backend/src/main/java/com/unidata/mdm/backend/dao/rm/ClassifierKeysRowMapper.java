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
import com.unidata.mdm.backend.po.ClassifierKeysPO;


/**
 * @author Mikhail Mikhailov
 * Mapper class for classifier keys.
 */
public class ClassifierKeysRowMapper implements RowMapper<ClassifierKeysPO> {
    /**
     * Default row mapper.
     */
    public static final ClassifierKeysRowMapper DEFAULT_ROW_MAPPER = new ClassifierKeysRowMapper();
    /**
     * Default first result extractor.
     */
    public static final ResultSetExtractor<ClassifierKeysPO> DEFAULT_FIRST_RESULT_EXTRACTOR
        = rs -> rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;
    /**
     * Constructor.
     */
    private ClassifierKeysRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeysPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        ClassifierKeysPO keys = new ClassifierKeysPO();

        // 1. Etalon
        keys.setEtalonId(rs.getString(ClassifierKeysPO.FIELD_ETALON_ID));
        keys.setEtalonName(rs.getString(ClassifierKeysPO.FIELD_ETALON_NAME));

        String statusAsString = rs.getString(ClassifierKeysPO.FIELD_ETALON_STATUS);
        keys.setEtalonStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        String stateAsString = rs.getString(ClassifierKeysPO.FIELD_ETALON_STATE);
        keys.setEtalonState(stateAsString != null ? ApprovalState.valueOf(stateAsString) : null);

        // 2. Origin
        keys.setOriginId(rs.getString(ClassifierKeysPO.FIELD_ORIGIN_ID));
        keys.setOriginName(rs.getString(ClassifierKeysPO.FIELD_ORIGIN_NAME));
        keys.setOriginNodeId(rs.getString(ClassifierKeysPO.FIELD_ORIGIN_NODE_ID));
        keys.setOriginSourceSystem(rs.getString(ClassifierKeysPO.FIELD_ORIGIN_SOURCE_SYSTEM));
        keys.setOriginRevision(rs.getInt(ClassifierKeysPO.FIELD_ORIGIN_REVISION));

        statusAsString = rs.getString(ClassifierKeysPO.FIELD_ORIGIN_STATUS);
        keys.setOriginStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        // 3. Etalon record
        keys.setEtalonIdRecord(rs.getString(ClassifierKeysPO.FIELD_ETALON_ID_RECORD));
        keys.setEtalonRecordName(rs.getString(ClassifierKeysPO.FIELD_ETALON_RECORD_NAME));

        statusAsString = rs.getString(ClassifierKeysPO.FIELD_ETALON_RECORD_STATUS);
        keys.setEtalonRecordStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        stateAsString = rs.getString(ClassifierKeysPO.FIELD_ETALON_RECORD_STATE);
        keys.setEtalonRecordState(stateAsString != null ? ApprovalState.valueOf(stateAsString) : null);

        // Origin record
        keys.setOriginIdRecord(rs.getString(ClassifierKeysPO.FIELD_ORIGIN_ID_RECORD));
        keys.setOriginRecordExternalId(rs.getString(ClassifierKeysPO.FIELD_ORIGIN_RECORD_EXTERNAL_ID));
        keys.setOriginRecordName(rs.getString(ClassifierKeysPO.FIELD_ORIGIN_RECORD_NAME));
        keys.setOriginRecordSourceSystem(rs.getString(ClassifierKeysPO.FIELD_ORIGIN_RECORD_SOURCE_SYSTEM));

        statusAsString = rs.getString(ClassifierKeysPO.FIELD_ORIGIN_RECORD_STATUS);
        keys.setOriginRecordStatus(statusAsString != null ? RecordStatus.valueOf(statusAsString) : null);

        return keys;
    }

}
