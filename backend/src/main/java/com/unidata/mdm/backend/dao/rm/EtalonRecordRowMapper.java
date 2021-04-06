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
import com.unidata.mdm.backend.po.EtalonRecordPO;

/**
 * @author Mikhail Mikhailov
 * Row mapper for {@link EtalonRecordPO} objects.
 */
public class EtalonRecordRowMapper
    extends AbstractRowMapper<EtalonRecordPO>
    implements RowMapper<EtalonRecordPO> {

    /**
     * Default reusable row mapper.
     */
    public static final EtalonRecordRowMapper DEFAULT_ROW_MAPPER = new EtalonRecordRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<EtalonRecordPO> DEFAULT_ETALON_FIRST_RESULT_EXTRACTOR
        = rs -> rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    public EtalonRecordRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecordPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        EtalonRecordPO po = new EtalonRecordPO();

        super.mapRow(po, rs, rowNum);

        po.setId(rs.getString(EtalonRecordPO.FIELD_ID));
        po.setName(rs.getString(EtalonRecordPO.FIELD_NAME));
        po.setVersion(rs.getInt(EtalonRecordPO.FIELD_VERSION));
        po.setStatus(RecordStatus.valueOf(rs.getString(EtalonRecordPO.FIELD_STATUS)));
        po.setApproval(ApprovalState.valueOf(rs.getString(EtalonRecordPO.FIELD_APPROVAL)));
        po.setGsn(rs.getLong(EtalonRecordPO.FIELD_GSN));
        po.setOperationId(rs.getString(EtalonRecordPO.FIELD_OPERATION_ID));

        return po;
    }

}
