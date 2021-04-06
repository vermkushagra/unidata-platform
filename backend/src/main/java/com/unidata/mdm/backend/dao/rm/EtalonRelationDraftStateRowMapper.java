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
import com.unidata.mdm.backend.po.EtalonRelationDraftStatePO;


/**
 * @author Mikhail Mikhailov
 * Etalon relation draft state row mapper.
 */
public class EtalonRelationDraftStateRowMapper implements RowMapper<EtalonRelationDraftStatePO> {

        /**
     * Default row mapper.
     */
    public static final EtalonRelationDraftStateRowMapper DEFAULT_ROW_MAPPER = new EtalonRelationDraftStateRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<EtalonRelationDraftStatePO> DEFAULT_FIRST_RESULT_EXTRACTOR
        = rs -> rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    private EtalonRelationDraftStateRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRelationDraftStatePO mapRow(ResultSet rs, int rowNum) throws SQLException {

        EtalonRelationDraftStatePO po = new EtalonRelationDraftStatePO();
        po.setCreateDate(rs.getTimestamp(EtalonDraftStatePO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(EtalonDraftStatePO.FIELD_CREATED_BY));
        po.setEtalonId(rs.getString(EtalonDraftStatePO.FIELD_ETALON_ID));
        po.setId(rs.getInt(EtalonDraftStatePO.FIELD_ID));
        po.setRevision(rs.getInt(EtalonDraftStatePO.FIELD_REVISION));
        po.setStatus(RecordStatus.valueOf(rs.getString(EtalonDraftStatePO.FIELD_STATUS)));

        return po;
    }

}
