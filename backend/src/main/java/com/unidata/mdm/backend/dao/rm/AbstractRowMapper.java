package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.unidata.mdm.backend.po.AbstractPO;

/**
 * @author Mikhail Mikhailov
 * Abstract PO mapper.
 */
public abstract class AbstractRowMapper<T extends AbstractPO> {

    /**
     * Constructor.
     */
    public AbstractRowMapper() {
        super();
    }

    /**
     * Maps common rows.
     * @param t object
     * @param rs result set
     * @param rowNum row number
     * @throws SQLException if something went wrong
     */
    protected void mapRow(T t, ResultSet rs, int rowNum) throws SQLException {
        t.setCreateDate(rs.getTimestamp(AbstractPO.FIELD_CREATE_DATE));
        t.setCreatedBy(rs.getString(AbstractPO.FIELD_CREATED_BY));
        t.setUpdateDate(rs.getTimestamp(AbstractPO.FIELD_UPDATE_DATE));
        t.setUpdatedBy(rs.getString(AbstractPO.FIELD_UPDATED_BY));
    }
}
