/**
 * Date: 05.07.2016
 */

package org.unidata.mdm.core.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.unidata.mdm.core.po.security.UserPropertyPO;
import org.unidata.mdm.core.po.security.UserPropertyPO.FieldColumns;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class UserPropertyRowMapper implements RowMapper<UserPropertyPO> {
    @Override
    public UserPropertyPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserPropertyPO result = new UserPropertyPO();

        result.setId(rs.getLong(FieldColumns.ID.name()));
        result.setRequired(rs.getBoolean(FieldColumns.REQUIRED.name()));
        result.setName(rs.getString(FieldColumns.NAME.name()));
        result.setDisplayName(rs.getString(FieldColumns.DISPLAY_NAME.name()));

        return result;
    }
}
