/**
 * Date: 05.07.2016
 */

package org.unidata.mdm.core.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.unidata.mdm.core.po.security.RolePropertyPO;
import org.unidata.mdm.core.po.security.RolePropertyPO.FieldColumns;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class RolePropertyRowMapper implements RowMapper<RolePropertyPO> {

    @Override
    public RolePropertyPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        RolePropertyPO result = new RolePropertyPO();

        result.setId(rs.getLong(FieldColumns.ID.name()));
        result.setRequired(rs.getBoolean(FieldColumns.REQUIRED.name()));
        result.setName(rs.getString(FieldColumns.NAME.name()));
        result.setDisplayName(rs.getString(FieldColumns.DISPLAY_NAME.name()));

        return result;
    }
}
