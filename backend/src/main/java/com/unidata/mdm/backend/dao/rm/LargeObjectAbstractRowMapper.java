/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.po.LargeObjectPO;

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

        po.setId(rs.getString(LargeObjectPO.FIELD_ID));
        po.setCreateDate(rs.getDate(LargeObjectPO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(LargeObjectPO.FIELD_CREATED_BY));
        po.setUpdateDate(rs.getDate(LargeObjectPO.FIELD_UPDATE_DATE));
        po.setUpdatedBy(rs.getString(LargeObjectPO.FIELD_UPDATED_BY));
        po.setEtalonId(rs.getString(LargeObjectPO.FIELD_ETALON_ID));
        po.setOriginId(rs.getString(LargeObjectPO.FIELD_ORIGIN_ID));
        po.setEventId(rs.getString(LargeObjectPO.FIELD_EVENT_ID));
        po.setField(rs.getString(LargeObjectPO.FIELD_FIELD));
        po.setFileName(rs.getString(LargeObjectPO.FIELD_FILE_NAME));
        po.setMimeType(rs.getString(LargeObjectPO.FIELD_MIME_TYPE));
        po.setData(rs.getBinaryStream(LargeObjectPO.FIELD_DATA));
        po.setSize(rs.getLong(LargeObjectPO.FIELD_SIZE));
        po.setState(ApprovalState.valueOf(rs.getString(LargeObjectPO.FIELD_STATUS)));
    }
}
