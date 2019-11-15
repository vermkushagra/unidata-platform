package org.unidata.mdm.data.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.keys.AbstractVistoryPO;

/**
 * @author Mikhail Mikhailov
 * Extended stuff mapper.
 */
public class ExtendedRecordVistoryRowMapper
    extends AbstractVistoryRowMapper<RecordVistoryPO>
    implements RowMapper<RecordVistoryPO> {
    /**
     * Default 'JAXB data' row mapper.
     */
    public static final ExtendedRecordVistoryRowMapper DEFAULT_EXTENDED_RECORD_VISTORY_JAXB_ROW_MAPPER
        = new ExtendedRecordVistoryRowMapper(true, false);
    /**
     * Default 'binary protostuff data' row mapper.
     */
    public static final ExtendedRecordVistoryRowMapper DEFAULT_EXTENDED_RECORD_VISTORY_PROTOSTUFF_ROW_MAPPER
        = new ExtendedRecordVistoryRowMapper(false, true);
    /**
     * Default 'without data' row mapper.
     */
    public static final ExtendedRecordVistoryRowMapper DEFAULT_EXTENDED_RECORD_VISTORY_NO_DATA_ROW_MAPPER
        = new ExtendedRecordVistoryRowMapper(false, false);
    /**
     * Constructor.
     */
    protected ExtendedRecordVistoryRowMapper(boolean jaxbData, boolean protostuffData) {
        super(jaxbData, protostuffData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordVistoryPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        RecordVistoryPO po = new RecordVistoryPO();
        super.mapRow(po, rs, rowNum);

        po.setUpdateDate(rs.getTimestamp(AbstractVistoryPO.FIELD_UPDATE_DATE));
        po.setUpdatedBy(rs.getString(AbstractVistoryPO.FIELD_UPDATED_BY));
        po.setExternalId(rs.getString(RecordVistoryPO.FIELD_EXTERNAL_ID));
        po.setName(rs.getString(AbstractVistoryPO.FIELD_NAME));
        po.setSourceSystem(rs.getString(AbstractVistoryPO.FIELD_SOURCE_SYSTEM));
        po.setEnrichment(rs.getBoolean(AbstractVistoryPO.FIELD_IS_ENRICHMENT));
        po.setOriginStatus(RecordStatus.valueOf(rs.getString(AbstractVistoryPO.FIELD_ORIGIN_STATUS)));

        return po;
    }

}
