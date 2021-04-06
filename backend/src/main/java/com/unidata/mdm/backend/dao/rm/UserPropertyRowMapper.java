/**
 * Date: 05.07.2016
 */

package com.unidata.mdm.backend.dao.rm;

import com.unidata.mdm.backend.service.security.po.UserPropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyPO.FieldColumns;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

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
        result.setName(rs.getString(FieldColumns.NAME.name()));
        result.setDisplayName(rs.getString(FieldColumns.DISPLAY_NAME.name()));

        return result;
    }
}
