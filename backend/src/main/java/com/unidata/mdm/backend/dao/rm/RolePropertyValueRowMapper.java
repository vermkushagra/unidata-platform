/**
 * Date: 06.07.2016
 */

package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.security.po.RolePropertyPO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO.FieldColumns;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class RolePropertyValueRowMapper implements RowMapper<RolePropertyValuePO> {
    @Override
    public RolePropertyValuePO mapRow(ResultSet rs, int rowNum) throws SQLException {

        RolePropertyValuePO result = new RolePropertyValuePO();

        long id = rs.getLong(FieldColumns.ID.name());
        result.setId(rs.wasNull() ? null : id);

        result.setRoleId(rs.getLong(FieldColumns.ROLE_ID.name()));
        result.setValue(rs.getString(FieldColumns.VALUE.name()));

        long propertyId = rs.getLong(FieldColumns.PROPERTY_ID.name());

        RolePropertyPO property = new RolePropertyPO();
        property.setId(propertyId);
        property.setName(rs.getString(RolePropertyPO.FieldColumns.NAME.name()));
        property.setDisplayName(rs.getString(RolePropertyPO.FieldColumns.DISPLAY_NAME.name()));

        result.setProperty(property);
        return result;
    }
}
