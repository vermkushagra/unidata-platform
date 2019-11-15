/**
 *
 */
package org.unidata.mdm.core.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.unidata.mdm.core.type.data.ApprovalState;

import org.unidata.mdm.core.po.LargeObjectPO;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class LargeObjectAbstractRowMapper {

    /**
     * Maps common part for large objects.
     * @param po the object
     * @param rs the {@link ResultSet}
     * @param rowNum row number
     * @throws SQLException
     */
    public void mapRow(LargeObjectPO po, ResultSet rs, int rowNum) throws SQLException {

        UUID val = rs.getObject(LargeObjectPO.FIELD_ID, UUID.class);
        po.setId(val != null ? val.toString() : null);

        val = rs.getObject(LargeObjectPO.FIELD_CLASSIFIER_ID, UUID.class);
        po.setClassifierId(val != null ? val.toString() : null);

        val = rs.getObject(LargeObjectPO.FIELD_RECORD_ID, UUID.class);
        po.setRecordId(val != null ? val.toString() : null);

        val = rs.getObject(LargeObjectPO.FIELD_EVENT_ID, UUID.class);
        po.setEventId(val != null ? val.toString() : null);

        po.setCreateDate(rs.getDate(LargeObjectPO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(LargeObjectPO.FIELD_CREATED_BY));
        po.setUpdateDate(rs.getDate(LargeObjectPO.FIELD_UPDATE_DATE));
        po.setUpdatedBy(rs.getString(LargeObjectPO.FIELD_UPDATED_BY));
        po.setField(rs.getString(LargeObjectPO.FIELD_FIELD));
        po.setFileName(rs.getString(LargeObjectPO.FIELD_FILE_NAME));
        po.setMimeType(rs.getString(LargeObjectPO.FIELD_MIME_TYPE));
        po.setData(rs.getBinaryStream(LargeObjectPO.FIELD_DATA));
        po.setSize(rs.getLong(LargeObjectPO.FIELD_SIZE));

        po.setState(ApprovalState.valueOf(rs.getString(LargeObjectPO.FIELD_STATUS)));
    }
}
