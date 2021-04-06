/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.OriginRelationPO;

/**
 * @author Mikhail Mikhailov
 * Etalon relations row mapper.
 */
public class OriginRelationRowMapper
    extends AbstractRowMapper<OriginRelationPO>
    implements RowMapper<OriginRelationPO> {

    /**
     * Default 'with data' row mapper.
     */
    public static final OriginRelationRowMapper DEFAULT_ORIGIN_RELATION_ROW_MAPPER
        = new OriginRelationRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<OriginRelationPO> DEFAULT_ORIGIN_RELATION_FIRST_RESULT_EXTRACTOR
        = rs -> rs != null && rs.next() ? DEFAULT_ORIGIN_RELATION_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    private OriginRelationRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRelationPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        OriginRelationPO po = new OriginRelationPO();

        super.mapRow(po, rs, rowNum);

        po.setId(rs.getString(OriginRelationPO.FIELD_ID));
        po.setEtalonId(rs.getString(OriginRelationPO.FIELD_ETALON_ID));
        po.setName(rs.getString(OriginRelationPO.FIELD_NAME));
        po.setOriginIdFrom(rs.getString(OriginRelationPO.FIELD_ORIGIN_ID_FROM));
        po.setOriginIdTo(rs.getString(OriginRelationPO.FIELD_ORIGIN_ID_TO));
        po.setSourceSystem(rs.getString(OriginRelationPO.FIELD_SOURCE_SYSTEM));
        po.setStatus(RecordStatus.valueOf(rs.getString(OriginRelationPO.FIELD_STATUS)));

        return po;
    }

}
