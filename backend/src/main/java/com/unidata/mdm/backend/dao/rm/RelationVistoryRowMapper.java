/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.util.DumpUtils;

/**
 * @author Mikhail Mikhailov
 * Relations vistory row mapper.
 */
public class RelationVistoryRowMapper implements RowMapper<OriginsVistoryRelationsPO> {

    /**
     * Default 'with JAXB data' row mapper.
     */
    public static final RelationVistoryRowMapper DEFAULT_RELATIONS_VISTORY_JAXB_ROW_MAPPER
        = new RelationVistoryRowMapper(true, true, false);
    /**
     * Default 'with Protostuff data' row mapper.
     */
    public static final RelationVistoryRowMapper DEFAULT_RELATIONS_VISTORY_PROTOSTUFF_ROW_MAPPER
        = new RelationVistoryRowMapper(true, false, true);
    /**
     * Default 'without data' row mapper.
     */
    public static final RelationVistoryRowMapper DEFAULT_RELATIONS_VISTORY_NO_DATA_ROW_MAPPER
        = new RelationVistoryRowMapper(false, false, false);

    /**
     * Read JAXB data.
     */
    private final boolean jaxbData;
    /**
     * Read binary protostuff data.
     */
    private final boolean protostuffData;
    /**
     * Skip data or not.
     */
    private final boolean withData;

    /**
     * Constructor.
     */
    private RelationVistoryRowMapper(boolean withData, boolean jaxbData, boolean protostuffData) {
        super();
        this.withData = withData;
        this.jaxbData = jaxbData;
        this.protostuffData = protostuffData;
    }

    /**
     * @return the withData
     */
    public boolean isWithData() {
        return withData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginsVistoryRelationsPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        OriginsVistoryRelationsPO po = new OriginsVistoryRelationsPO();

        if (withData) {
            if (jaxbData) {
                po.setData(DumpUtils.restoreOriginRelationFromJaxb(rs.getString(OriginsVistoryRelationsPO.FIELD_DATA_A)));
            } else if (protostuffData) {
                po.setData(DumpUtils.restoreFromProtostuff(rs.getBytes(OriginsVistoryRelationsPO.FIELD_DATA_B)));
            }
        }

        po.setId(rs.getString(OriginsVistoryRelationsPO.FIELD_ID));
        po.setOriginId(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_ID));
        po.setOperationId(rs.getString(OriginsVistoryRelationsPO.FIELD_OPERATION_ID));
        po.setRevision(rs.getInt(OriginsVistoryRelationsPO.FIELD_REVISION));
        po.setValidFrom(rs.getTimestamp(OriginsVistoryRelationsPO.FIELD_VALID_FROM));
        po.setValidTo(rs.getTimestamp(OriginsVistoryRelationsPO.FIELD_VALID_TO));
        po.setCreateDate(rs.getTimestamp(OriginsVistoryRelationsPO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(OriginsVistoryRelationsPO.FIELD_CREATED_BY));
        po.setUpdateDate(rs.getTimestamp(OriginsVistoryRelationsPO.FIELD_UPDATE_DATE));
        po.setUpdatedBy(rs.getString(OriginsVistoryRelationsPO.FIELD_UPDATED_BY));
        po.setSourceSystem(rs.getString(OriginsVistoryRelationsPO.FIELD_SOURCE_SYSTEM));
        po.setName(rs.getString(OriginsVistoryRelationsPO.FIELD_NAME));
        po.setStatus(RecordStatus.valueOf(rs.getString(OriginsVistoryRelationsPO.FIELD_STATUS)));
        po.setShift(DataShift.valueOf(rs.getString(OriginsVistoryRelationsPO.FIELD_SHIFT)));
        po.setApproval(ApprovalState.valueOf(rs.getString(OriginsVistoryRelationsPO.FIELD_APPROVAL)));
        po.setMajor(rs.getInt(OriginsVistoryRelationsPO.FIELD_MAJOR));
        po.setMajor(rs.getInt(OriginsVistoryRelationsPO.FIELD_MINOR));
        po.setRelationEtalonId(rs.getString(OriginsVistoryRelationsPO.FIELD_RELATION_ETALON_ID));

        // Origin FROM
        po.setOriginIdFrom(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_ID_FROM));
        po.setOriginFromExternalId(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_FROM_EXTERNAL_ID));
        po.setOriginFromName(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_FROM_NAME));
        po.setOriginFromSourceSystem(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_FROM_SOURCE_SYSTEM));

        // Origin TO
        po.setOriginIdTo(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_ID_TO));
        po.setOriginToExternalId(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_TO_EXTERNAL_ID));
        po.setOriginToName(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_TO_NAME));
        po.setOriginToSourceSystem(rs.getString(OriginsVistoryRelationsPO.FIELD_ORIGIN_TO_SOURCE_SYSTEM));

        return po;
    }

}
