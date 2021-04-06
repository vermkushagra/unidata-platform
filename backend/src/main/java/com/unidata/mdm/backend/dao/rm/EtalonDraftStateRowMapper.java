/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.EtalonDraftStatePO;


/**
 * @author Mikhail Mikhailov
 * Etalon draft state row mapper.
 */
public class EtalonDraftStateRowMapper implements RowMapper<EtalonDraftStatePO> {

        /**
     * Default row mapper.
     */
    public static final EtalonDraftStateRowMapper DEFAULT_ROW_MAPPER = new EtalonDraftStateRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<EtalonDraftStatePO> DEFAULT_FIRST_RESULT_EXTRACTOR
        = rs -> rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    private EtalonDraftStateRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonDraftStatePO mapRow(ResultSet rs, int rowNum) throws SQLException {

        EtalonDraftStatePO po = new EtalonDraftStatePO();
        po.setCreateDate(rs.getTimestamp(EtalonDraftStatePO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(EtalonDraftStatePO.FIELD_CREATED_BY));
        po.setEtalonId(rs.getString(EtalonDraftStatePO.FIELD_ETALON_ID));
        po.setId(rs.getInt(EtalonDraftStatePO.FIELD_ID));
        po.setRevision(rs.getInt(EtalonDraftStatePO.FIELD_REVISION));
        po.setStatus(RecordStatus.valueOf(rs.getString(EtalonDraftStatePO.FIELD_STATUS)));

        return po;
    }

}
