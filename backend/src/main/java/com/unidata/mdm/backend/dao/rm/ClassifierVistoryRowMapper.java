package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;
import com.unidata.mdm.backend.util.DumpUtils;

/**
 * @author Mikhail Mikhailov
 * Classifier vistory row mapper.
 */
public class ClassifierVistoryRowMapper implements RowMapper<OriginsVistoryClassifierPO> {

    /**
     * Default 'with JAXB data' row mapper.
     */
    public static final ClassifierVistoryRowMapper DEFAULT_CLASSIFIER_VISTORY_JAXB_ROW_MAPPER
        = new ClassifierVistoryRowMapper(true, true, false);
    /**
     * Default 'with Protostuff data' row mapper.
     */
    public static final ClassifierVistoryRowMapper DEFAULT_CLASSIFIER_VISTORY_PROTOSTUFF_ROW_MAPPER
        = new ClassifierVistoryRowMapper(true, false, true);
    /**
     * Default 'without data' row mapper.
     */
    public static final ClassifierVistoryRowMapper DEFAULT_CLASSIFIER_VISTORY_NO_DATA_ROW_MAPPER
        = new ClassifierVistoryRowMapper(false, false, false);

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
    private ClassifierVistoryRowMapper(boolean withData, boolean jaxbData, boolean protostuffData) {
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
    public OriginsVistoryClassifierPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        OriginsVistoryClassifierPO po = new OriginsVistoryClassifierPO();

        if (withData) {
            if (jaxbData) {
                po.setData(DumpUtils.restoreOriginClassifierFromJaxb(rs.getString(OriginsVistoryClassifierPO.FIELD_DATA_A)));
            } else if (protostuffData) {
                po.setData(DumpUtils.restoreFromProtostuff(rs.getBytes(OriginsVistoryClassifierPO.FIELD_DATA_B)));
            }
        }

        po.setId(rs.getString(OriginsVistoryClassifierPO.FIELD_ID));
        po.setOriginId(rs.getString(OriginsVistoryClassifierPO.FIELD_ORIGIN_ID));
        po.setOperationId(rs.getString(OriginsVistoryClassifierPO.FIELD_OPERATION_ID));
        po.setEtalonId(rs.getString(OriginsVistoryClassifierPO.FIELD_ETALON_ID));
        po.setRevision(rs.getInt(OriginsVistoryClassifierPO.FIELD_REVISION));
        po.setValidFrom(rs.getTimestamp(OriginsVistoryClassifierPO.FIELD_VALID_FROM));
        po.setValidTo(rs.getTimestamp(OriginsVistoryClassifierPO.FIELD_VALID_TO));
        po.setCreateDate(rs.getTimestamp(OriginsVistoryClassifierPO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(OriginsVistoryClassifierPO.FIELD_CREATED_BY));
        po.setUpdateDate(rs.getTimestamp(OriginsVistoryClassifierPO.FIELD_UPDATE_DATE));
        po.setUpdatedBy(rs.getString(OriginsVistoryClassifierPO.FIELD_UPDATED_BY));
        po.setSourceSystem(rs.getString(OriginsVistoryClassifierPO.FIELD_SOURCE_SYSTEM));
        po.setName(rs.getString(OriginsVistoryClassifierPO.FIELD_NAME));
        po.setNodeId(rs.getString(OriginsVistoryClassifierPO.FIELD_NODE_ID));
        po.setStatus(RecordStatus.valueOf(rs.getString(OriginsVistoryClassifierPO.FIELD_STATUS)));
        po.setApproval(ApprovalState.valueOf(rs.getString(OriginsVistoryClassifierPO.FIELD_APPROVAL)));
        po.setMajor(rs.getInt(OriginsVistoryClassifierPO.FIELD_MAJOR));
        po.setMajor(rs.getInt(OriginsVistoryClassifierPO.FIELD_MINOR));

        // Origin record
        po.setOriginIdRecord(rs.getString(OriginsVistoryClassifierPO.FIELD_ORIGIN_ID_RECORD));
        po.setOriginRecordExternalId(rs.getString(OriginsVistoryClassifierPO.FIELD_ORIGIN_RECORD_EXTERNAL_ID));
        po.setOriginRecordName(rs.getString(OriginsVistoryClassifierPO.FIELD_ORIGIN_RECORD_NAME));
        po.setOriginRecordSourceSystem(rs.getString(OriginsVistoryClassifierPO.FIELD_ORIGIN_RECORD_SOURCE_SYSTEM));
        po.setOriginRecordStatus(RecordStatus.valueOf(rs.getString(OriginsVistoryClassifierPO.FIELD_ORIGIN_RECORD_STATUS)));

        return po;
    }

}
