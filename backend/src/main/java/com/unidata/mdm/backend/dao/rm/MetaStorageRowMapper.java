package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.po.MetaStoragePO;

/**
 * @author Mikhail Mikhailov
 */
public class MetaStorageRowMapper implements RowMapper<MetaStoragePO> {
    @Override
    public MetaStoragePO mapRow(ResultSet rs, int rowNum) throws SQLException {

        MetaStoragePO storage = new MetaStoragePO();
        storage.setId(rs.getString(MetaStoragePO.FIELD_ID));
        storage.setName(rs.getString(MetaStoragePO.FIELD_NAME));
        storage.setCreateDate(rs.getTimestamp(MetaStoragePO.FIELD_CREATE_DATE));
        storage.setUpdateDate(rs.getTimestamp(MetaStoragePO.FIELD_UPDATE_DATE));
        storage.setCreatedBy(rs.getString(MetaStoragePO.FIELD_CREATED_BY));
        storage.setUpdatedBy(rs.getString(MetaStoragePO.FIELD_UPDATED_BY));

        return storage;
    }
}
