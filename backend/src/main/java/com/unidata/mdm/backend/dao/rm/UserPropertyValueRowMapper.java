/**
 * Date: 06.07.2016
 */

package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.security.po.RolePropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyValuePO;
import com.unidata.mdm.backend.service.security.po.UserPropertyValuePO.FieldColumns;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class UserPropertyValueRowMapper implements RowMapper<UserPropertyValuePO> {

    /**
     * Dfeault instance singleton.
     */
    public static final UserPropertyValueRowMapper DEFAULT_ROW_MAPPER = new UserPropertyValueRowMapper();
    /**
     * Constructor.
     */
    private UserPropertyValueRowMapper() {
        super();
    }

    @Override
    public UserPropertyValuePO mapRow(ResultSet rs, int rowNum) throws SQLException {

        UserPropertyValuePO result = new UserPropertyValuePO();

        long id = rs.getLong(FieldColumns.ID.name());
        result.setId(rs.wasNull() ? null : id);

        result.setUserId(rs.getLong(FieldColumns.USER_ID.name()));
        result.setValue(rs.getString(FieldColumns.VALUE.name()));

        long propertyId = rs.getLong(FieldColumns.PROPERTY_ID.name());

        UserPropertyPO property = new UserPropertyPO();
        property.setId(propertyId);
        property.setName(rs.getString(RolePropertyPO.FieldColumns.NAME.name()));
        property.setDisplayName(rs.getString(RolePropertyPO.FieldColumns.DISPLAY_NAME.name()));

        result.setProperty(property);

        return result;
    }
}
