/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.dao.MetaDraftDao;
import com.unidata.mdm.backend.dao.rm.MetaDraftRowMapper;
import com.unidata.mdm.backend.po.MetaDraftPO;

/**
 * The Class MetaDraftDaoImpl.
 */
@Repository
public class MetaDraftDaoImpl extends AbstractDaoImpl implements MetaDraftDao {

	/** The create sql. */
	private String CREATE_SQL;

	/** The update sql. */
	private String UPDATE_SQL;

	/** The delete sql. */
	private String DELETE_SQL;

	/** The read sql. */
	private String READ_SQL;

	/** The read sql active draft. */
	private String READ_SQL_ACTIVE_DRAFT;

	/** The read max version. */
	private String READ_MAX_VERSION;

	/** The is draft exists. */
	private String IS_DRAFT_EXISTS;

	/** The Constant ROW_MAPPER. */
	private static final MetaDraftRowMapper ROW_MAPPER = new MetaDraftRowMapper();

	/**
	 * Instantiates a new meta draft dao impl.
	 *
	 * @param dataSource
	 *            the data source
	 * @param sql
	 *            the sql
	 */
	@Autowired
	public MetaDraftDaoImpl(DataSource dataSource, @Qualifier("meta-draft-sql") final Properties sql) {
		super(dataSource);
		this.CREATE_SQL = sql.getProperty("CREATE_SQL");
		this.UPDATE_SQL = sql.getProperty("UPDATE_SQL");
		this.DELETE_SQL = sql.getProperty("DELETE_SQL");
		this.READ_SQL = sql.getProperty("READ_SQL");
		this.READ_SQL_ACTIVE_DRAFT = sql.getProperty("READ_SQL_ACTIVE_DRAFT");
		this.READ_MAX_VERSION = sql.getProperty("READ_MAX_VERSION");
		this.IS_DRAFT_EXISTS = sql.getProperty("IS_DRAFT_EXISTS");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.MetaDraftDao#create(com.unidata.mdm.backend.po.
	 * MetaDraftPO)
	 */
	@Override
	public boolean create(MetaDraftPO source) {
		if (source == null) {
			return false;
		}
		Map<String, Object> params = new HashMap<>();
		params.put(MetaDraftPO.Field.NAME, source.getName());
		params.put(MetaDraftPO.Field.ACTIVE, source.isActive());
		params.put(MetaDraftPO.Field.TYPE, source.getType().name());
		params.put(MetaDraftPO.Field.VALUE, source.getValue());
		params.put(MetaDraftPO.Field.VERSION, source.getVersion());
		params.put(MetaDraftPO.Field.CREATED_AT, source.getCreatedAt());
		params.put(MetaDraftPO.Field.CREATED_BY, source.getCreatedBy());
		params.put(MetaDraftPO.Field.UPDATED_AT, source.getUpdatedAt());
		params.put(MetaDraftPO.Field.UPDATED_BY, source.getUpdatedBy());
		namedJdbcTemplate.update(CREATE_SQL, params);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.MetaDraftDao#update(com.unidata.mdm.backend.po.
	 * MetaDraftPO)
	 */
	@Override
	public boolean update(MetaDraftPO source) {
		if (source == null) {
			return false;
		}
		Map<String, Object> params = new HashMap<>();
		params.put(MetaDraftPO.Field.ID, source.getId());
		params.put(MetaDraftPO.Field.NAME, source.getName());
		params.put(MetaDraftPO.Field.TYPE, source.getType().name());
		params.put(MetaDraftPO.Field.VALUE, source.getValue());
		params.put(MetaDraftPO.Field.VERSION, source.getVersion());
		params.put(MetaDraftPO.Field.CREATED_AT, source.getCreatedAt());
		params.put(MetaDraftPO.Field.CREATED_BY, source.getCreatedBy());
		params.put(MetaDraftPO.Field.UPDATED_AT, source.getUpdatedAt());
		params.put(MetaDraftPO.Field.UPDATED_BY, source.getUpdatedBy());
		namedJdbcTemplate.update(UPDATE_SQL, params);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.MetaDraftDao#delete(com.unidata.mdm.backend.po.
	 * MetaDraftPO)
	 */
	@Override
	public boolean delete(MetaDraftPO source) {
		if (source == null) {
			return false;
		}
		Map<String, Object> params = new HashMap<>();
		params.put(MetaDraftPO.Field.ID, source.getId());
		namedJdbcTemplate.update(DELETE_SQL, params);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.MetaDraftDao#read(com.unidata.mdm.backend.po.
	 * MetaDraftPO)
	 */
	@Override
	public List<MetaDraftPO> read(MetaDraftPO source) {
		if (source == null) {
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		params.put(MetaDraftPO.Field.ID, source.getId());
		params.put(MetaDraftPO.Field.NAME, source.getName());
		params.put(MetaDraftPO.Field.TYPE, source.getType());
		params.put(MetaDraftPO.Field.VERSION, source.getVersion());
		List<MetaDraftPO> result = namedJdbcTemplate.query(READ_SQL, params, ROW_MAPPER);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.dao.MetaDraftDao#currentDraft(java.lang.String)
	 */
	@Override
	public List<MetaDraftPO> currentDraft(String storageId) {
		Map<String, Object> params = new HashMap<>();
		params.put(MetaDraftPO.Field.STORAGE_ID, storageId);
		List<MetaDraftPO> result = namedJdbcTemplate.query(READ_SQL_ACTIVE_DRAFT, params, ROW_MAPPER);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.dao.MetaDraftDao#isDraftExist(java.lang.String)
	 */
	@Override
	public boolean isDraftExist(String storageId) {
		Map<String, Object> params = new HashMap<>();
		params.put(MetaDraftPO.Field.STORAGE_ID, storageId);
		Boolean result = namedJdbcTemplate.queryForObject(IS_DRAFT_EXISTS, params, Boolean.class);
		return result == null ? false : result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.MetaDraftDao#getLastVersion(java.lang.String)
	 */
	@Override
	public long getLastVersion(String storageId) {
		Map<String, Object> params = new HashMap<>();
		params.put(MetaDraftPO.Field.STORAGE_ID, storageId);
		Long result = namedJdbcTemplate.queryForObject(READ_MAX_VERSION, params, Long.class);
		return result == null ? 0 : result;
	}

}
