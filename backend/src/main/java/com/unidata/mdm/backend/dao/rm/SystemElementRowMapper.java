package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;

public class SystemElementRowMapper  implements RowMapper<SystemElementPO> {
/**
 * 	id, 
		element_type, 
		element_name, 
		element_folder, 
		element_description, 
		element_content, 
		created_at, 
		created_by, 
		updated_at, 
		updated_by
 */
	@Override
	public SystemElementPO mapRow(ResultSet rs, int rowNum) throws SQLException {
		SystemElementPO result = new SystemElementPO();
		result.setId(rs.getInt("id"));
		result.setType(ElementTypePO.valueOf(rs.getString("element_type")));
		result.setName(rs.getString("element_name"));
		result.setFolder(rs.getString("element_folder"));
		result.setDescription(rs.getString("element_description"));
		result.setContent(rs.getBytes("element_content"));
		result.setCreatedAt(rs.getDate("created_at"));
		result.setCreatedBy(rs.getString("created_by"));
		result.setUpdatedAt(rs.getDate("updated_at"));
		result.setUpdatedBy(rs.getString("updated_by"));
		return result;
	}

}
