/**
 * Date: 05.07.2016
 */

package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.security.po.RolePropertyPO;
import com.unidata.mdm.backend.service.security.po.RolePropertyPO.FieldColumns;

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
        result.setName(rs.getString(FieldColumns.NAME.name()));
        result.setDisplayName(rs.getString(FieldColumns.DISPLAY_NAME.name()));

        return result;
    }
}
