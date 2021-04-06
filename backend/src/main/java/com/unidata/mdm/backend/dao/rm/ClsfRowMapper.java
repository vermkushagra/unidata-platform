package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.classifier.po.ClsfPO;

/**
 * The Class ClsfRowMapper.
 */
public class ClsfRowMapper implements RowMapper<ClsfPO> {

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    @Override
    public ClsfPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ClsfPO result = new ClsfPO();
        result.setCodePattern(rs.getString(ClsfPO.FieldColumns.CODE_PATTERN.name()));
        result.setCreatedAt(rs.getDate(ClsfPO.FieldColumns.CREATED_AT.name()));
        result.setCreatedBy(rs.getString(ClsfPO.FieldColumns.CREATED_BY.name()));
        result.setDescription(rs.getString(ClsfPO.FieldColumns.DESCRIPTION.name()));
        result.setDisplayName(rs.getString(ClsfPO.FieldColumns.DISPLAY_NAME.name()));
        result.setId(rs.getInt(ClsfPO.FieldColumns.ID.name()));
        result.setName(rs.getString(ClsfPO.FieldColumns.NAME.name()));
        result.setUpdatedAt(rs.getDate(ClsfPO.FieldColumns.UPDATED_AT.name()));
        result.setUpdatedBy(rs.getString(ClsfPO.FieldColumns.UPDATED_BY.name()));
        result.setValidateCodeByLevel(rs.getBoolean(ClsfPO.FieldColumns.VALIDATE_CODE_BY_LEVEL.name()));
        return result;
    }

}
