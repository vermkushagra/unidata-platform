package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.service.security.po.LabelAttributePO;
import com.unidata.mdm.backend.service.security.po.LabelPO;

/**
 * Row mapper for the LabelPO object.
 * @author ilya.bykov
 */
public class LabelAttributeRowMapper implements RowMapper<LabelAttributePO> {

    /**
     * Mapper singleton.
     */
    public static final LabelAttributeRowMapper DEFAULT_ROW_MAPPER = new LabelAttributeRowMapper();

    /**
     * Constructor.
     */
    private LabelAttributeRowMapper() {
        super();
    }
	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
	 * int)
	 */
	@Override
	public LabelAttributePO mapRow(ResultSet rs, int rowNum) throws SQLException {
		LabelAttributePO result = new LabelAttributePO();
		result.setId(rs.getInt(LabelAttributePO.Fields.ID));
		result.setName(rs.getString(LabelAttributePO.Fields.NAME));
		result.setValue(rs.getString(LabelAttributePO.Fields.VALUE));
		result.setDescription(rs.getString(LabelAttributePO.Fields.DESCRIPTION));
		result.setCreatedAt(rs.getTimestamp(LabelAttributePO.Fields.CREATED_AT));
		result.setUpdatedAt(rs.getTimestamp(LabelAttributePO.Fields.UPDATED_AT));
		result.setCreatedBy(rs.getString(LabelAttributePO.Fields.CREATED_BY));
		result.setUpdatedBy(rs.getString(LabelAttributePO.Fields.UPDATED_BY));
		result.setLabel(new LabelPO(rs.getInt(LabelAttributePO.Fields.S_LABEL_ID)));
		return result;
	}

}
