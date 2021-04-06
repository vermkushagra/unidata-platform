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
import com.unidata.mdm.backend.po.EtalonClassifierPO;

/**
 * @author Mikhail Mikhailov
 * Etalon classifier data row mapper.
 */
public class EtalonClassifierRowMapper
    extends AbstractRowMapper<EtalonClassifierPO>
    implements RowMapper<EtalonClassifierPO> {

    /**
     * Default 'with data' row mapper.
     */
    public static final EtalonClassifierRowMapper DEFAULT_ETALON_CLASSIFIER_ROW_MAPPER
        = new EtalonClassifierRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<EtalonClassifierPO> DEFAULT_ETALON_CLASSIFIER_FIRST_RESULT_EXTRACTOR
        = rs -> rs != null && rs.next() ? DEFAULT_ETALON_CLASSIFIER_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    public EtalonClassifierRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonClassifierPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        EtalonClassifierPO po = new EtalonClassifierPO();

        super.mapRow(po, rs, rowNum);

        po.setId(rs.getString(EtalonClassifierPO.FIELD_ID));
        po.setName(rs.getString(EtalonClassifierPO.FIELD_NAME));
        po.setEtalonIdRecord(rs.getString(EtalonClassifierPO.FIELD_ETALON_ID_RECORD));
        po.setStatus(RecordStatus.valueOf(rs.getString(EtalonClassifierPO.FIELD_STATUS)));
        po.setApproval(ApprovalState.valueOf(rs.getString(EtalonClassifierPO.FIELD_APPROVAL)));
        po.setGsn(rs.getLong(EtalonClassifierPO.FIELD_GSN));
        po.setOperationId(rs.getString(EtalonClassifierPO.FIELD_OPERATION_ID));

        return po;
    }

}
