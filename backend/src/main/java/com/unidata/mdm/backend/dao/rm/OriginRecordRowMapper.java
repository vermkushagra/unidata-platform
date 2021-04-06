/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;

/**
 * @author Mikhail Mikhailov
 * Row mapper for {@link EtalonRecordPO} objects.
 */
public class OriginRecordRowMapper implements RowMapper<OriginRecordPO> {

    /**
     * Default reusable row mapper.
     */
    public static final OriginRecordRowMapper DEFAULT_ROW_MAPPER = new OriginRecordRowMapper();
    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<OriginRecordPO> DEFAULT_ORIGIN_FIRST_RESULT_EXTRACTOR
        = rs -> rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    private OriginRecordRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecordPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        OriginRecordPO po = new OriginRecordPO();

        po.setId(rs.getString(OriginRecordPO.FIELD_ID));
        po.setName(rs.getString(OriginRecordPO.FIELD_NAME));
        po.setVersion(rs.getInt(OriginRecordPO.FIELD_VERSION));
        po.setCreateDate(rs.getTimestamp(OriginRecordPO.FIELD_CREATE_DATE));
        po.setUpdateDate(rs.getTimestamp(OriginRecordPO.FIELD_UPDATE_DATE));
        po.setCreatedBy(rs.getString(OriginRecordPO.FIELD_CREATED_BY));
        po.setUpdatedBy(rs.getString(OriginRecordPO.FIELD_UPDATED_BY));
        po.setEtalonId(rs.getString(OriginRecordPO.FIELD_ETALON_ID));
        po.setSourceSystem(rs.getString(OriginRecordPO.FIELD_SOURCE_SYSTEM));
        po.setExternalId(rs.getString(OriginRecordPO.FIELD_EXTERNAL_ID));
        po.setEnrichment(rs.getBoolean(OriginRecordPO.FIELD_IS_ENRICHMENT));
        po.setStatus(RecordStatus.valueOf(rs.getString(OriginRecordPO.FIELD_STATUS)));

        return po;
    }

}
