package org.unidata.mdm.data.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.unidata.mdm.core.po.AbstractDistributedPO;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.po.keys.AbstractVistoryPO;
import org.unidata.mdm.data.serialization.DataSerializer;

/**
 * @author Mikhail Mikhailov
 * Common vistory stuff.
 */
public class AbstractVistoryRowMapper<T extends AbstractVistoryPO> {
    /**
     * Read JAXB data.
     */
    protected final boolean jaxbData;
    /**
     * Read binary protostuff data.
     */
    protected final boolean protostuffData;
    /**
     * Set to true, if we don't want data unmarshalling while mapping.
     */
    protected final boolean mapRawData;
    /**
     * Constructor.
     */
    protected AbstractVistoryRowMapper(boolean jaxbData, boolean protostuffData) {
        super();
        this.jaxbData = jaxbData;
        this.protostuffData = protostuffData;
        this.mapRawData = false;
    }
    /**
     * Constructor.
     */
    protected AbstractVistoryRowMapper(boolean jaxbData, boolean protostuffData, boolean mapRawData) {
        super();
        this.jaxbData = jaxbData;
        this.protostuffData = protostuffData;
        this.mapRawData = mapRawData;
    }
    /**
     * Maps common rows.
     * @param t object
     * @param rs result set
     * @param rowNum row number
     * @throws SQLException if something went wrong
     */
    protected void mapRow(T po, ResultSet rs, int rowNum) throws SQLException {

        po.setId(rs.getString(AbstractVistoryPO.FIELD_ID));
        po.setOriginId(rs.getString(AbstractVistoryPO.FIELD_ORIGIN_ID));
        po.setShard(rs.getInt(AbstractDistributedPO.FIELD_SHARD));
        po.setRevision(rs.getInt(AbstractVistoryPO.FIELD_REVISION));
        po.setValidFrom(rs.getTimestamp(AbstractVistoryPO.FIELD_VALID_FROM));
        po.setValidTo(rs.getTimestamp(AbstractVistoryPO.FIELD_VALID_TO));
        if (jaxbData) {
            if (mapRawData) {
                po.setJaxbRawData(rs.getString(AbstractVistoryPO.FIELD_DATA_A));
            } else {
                po.setData(DataSerializer.restoreOriginRecordFromJaxb(rs.getString(AbstractVistoryPO.FIELD_DATA_A)));
            }
        } else if (protostuffData) {
            if (mapRawData) {
                po.setProtostuffRawData(rs.getBytes(AbstractVistoryPO.FIELD_DATA_B));
            } else {
                po.setData(DataSerializer.fromProtostuff(rs.getBytes(AbstractVistoryPO.FIELD_DATA_B)));
            }
        }
        po.setCreateDate(rs.getTimestamp(AbstractVistoryPO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(AbstractVistoryPO.FIELD_CREATED_BY));
        po.setStatus(RecordStatus.valueOf(rs.getString(AbstractVistoryPO.FIELD_STATUS)));
        po.setApproval(ApprovalState.valueOf(rs.getString(AbstractVistoryPO.FIELD_APPROVAL)));
        po.setShift(DataShift.valueOf(rs.getString(AbstractVistoryPO.FIELD_SHIFT)));
        po.setOperationType(OperationType.valueOf(rs.getString(AbstractVistoryPO.FIELD_OPERATION_TYPE)));
        po.setOperationId(rs.getString(AbstractVistoryPO.FIELD_OPERATION_ID));
        po.setMajor(rs.getInt(AbstractVistoryPO.FIELD_MAJOR));
        po.setMinor(rs.getInt(AbstractVistoryPO.FIELD_MINOR));
    }
    /**
     * @return the withData
     */
    public boolean isWithData() {
        return jaxbData || protostuffData;
    }
}
