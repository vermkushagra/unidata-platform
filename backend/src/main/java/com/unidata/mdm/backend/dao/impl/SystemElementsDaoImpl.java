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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.dao.SystemElementsDao;
import com.unidata.mdm.backend.dao.rm.SystemElementRowMapper;
import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;

/**
 * The Class SystemElementsDaoImpl.
 */
@Repository
public class SystemElementsDaoImpl extends AbstractDaoImpl implements SystemElementsDao {

	/** The create new sql. */
	private final String CREATE_NEW_SQL;

	/** The get by id sql. */
	private final String GET_BY_ID_SQL;

	/** The get by name and path sql. */
	private final String GET_BY_NAME_AND_PATH_SQL;

	/** The get by path and types sql. */
	private final String GET_BY_PATH_AND_TYPES_SQL;

	/** The get by types sql. */
	private final String GET_BY_TYPES_SQL;

	/** The update by id sql. */
	private final String UPDATE_BY_ID_SQL;

	/** The delete by id sql. */
	private final String DELETE_BY_ID_SQL;

	/** The clear old sql. */
	private final String CLEAR_OLD_SQL;

	/** The delete by name and path sql. */
	private final String DELETE_BY_NAME_AND_PATH_SQL;

	/** The delete by path and types sql. */
	private final String DELETE_BY_PATH_AND_TYPES_SQL;
	/** The delete by name and types sql. */
	private final String DELETE_BY_NAME_AND_TYPES_SQL;
	/** The update class names. */
	private final String UPDATE_CLASS_NAMES_SQL;
	
	/** Delete element by class name */
	private final String DELETE_BY_CLASS_NAME;
	/**
	 * Delete cleanse function duplicates
	 */
	private final String DELETE_DUPLICATES;
	
	/** The system element row mapper. */
	private SystemElementRowMapper systemElementRowMapper = new SystemElementRowMapper();

	/**
	 * Instantiates a new system elements dao impl.
	 *
	 * @param dataSource
	 *            the data source
	 * @param sql
	 *            the sql
	 */
	@Autowired
	public SystemElementsDaoImpl(DataSource dataSource, @Qualifier("system-elements-sql") Properties sql) {
		super(dataSource);
		this.CREATE_NEW_SQL = sql.getProperty("CREATE_NEW_SQL");
		this.GET_BY_ID_SQL = sql.getProperty("GET_BY_ID_SQL");
		this.GET_BY_NAME_AND_PATH_SQL = sql.getProperty("GET_BY_NAME_AND_PATH_SQL");
		this.GET_BY_PATH_AND_TYPES_SQL = sql.getProperty("GET_BY_PATH_AND_TYPES_SQL");
		this.GET_BY_TYPES_SQL = sql.getProperty("GET_BY_TYPES_SQL");
		this.UPDATE_BY_ID_SQL = sql.getProperty("UPDATE_BY_ID_SQL");
		this.DELETE_BY_ID_SQL = sql.getProperty("DELETE_BY_ID_SQL");
		this.CLEAR_OLD_SQL = sql.getProperty("CLEAR_OLD_SQL");
		this.DELETE_BY_NAME_AND_PATH_SQL = sql.getProperty("DELETE_BY_NAME_AND_PATH_SQL");
		this.DELETE_BY_PATH_AND_TYPES_SQL = sql.getProperty("DELETE_BY_PATH_AND_TYPES_SQL");
		this.DELETE_BY_NAME_AND_TYPES_SQL = sql.getProperty("DELETE_BY_NAME_AND_TYPES_SQL");
		this.DELETE_BY_CLASS_NAME = sql.getProperty("DELETE_BY_CLASS_NAME");
		this.UPDATE_CLASS_NAMES_SQL = sql.getProperty("UPDATE_CLASS_NAMES_SQL");
		this.DELETE_DUPLICATES = sql.getProperty("DELETE_DUPLICATES");
	}

	/**
	 * Creates the.
	 *
	 * @param element
	 *            the element
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.SystemElementsDao#create(com.unidata.mdm.
	 * backend.po.initializer.SystemElementPO)
	 */
	@Override
	public void create(SystemElementPO element) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("element_type", element.getType().name());
		paramMap.put("element_name", element.getName());
		paramMap.put("element_folder", element.getFolder());
		paramMap.put("element_description", element.getDescription());
		paramMap.put("element_content", element.getContent());
		paramMap.put("element_class", element.getClassName());
		paramMap.put("created_at", element.getCreatedAt());
		paramMap.put("created_by", element.getCreatedBy());
		paramMap.put("updated_at", element.getUpdatedAt());
		paramMap.put("updated_by", element.getUpdatedBy());
		namedJdbcTemplate.update(CREATE_NEW_SQL, paramMap);

	}

	/**
	 * Update.
	 *
	 * @param element
	 *            the element
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.SystemElementsDao#update(com.unidata.mdm.
	 * backend.po.initializer.SystemElementPO)
	 */
	@Override
	public void update(SystemElementPO element) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("element_type", element.getType().name());
		paramMap.put("element_name", element.getName());
		paramMap.put("element_folder", element.getFolder());
		paramMap.put("element_description", element.getDescription());
		paramMap.put("element_content", element.getContent());
		paramMap.put("element_class", element.getClassName());
		paramMap.put("created_at", element.getCreatedAt());
		paramMap.put("created_by", element.getCreatedBy());
		paramMap.put("updated_at", element.getUpdatedAt());
		paramMap.put("updated_by", element.getUpdatedBy());
		namedJdbcTemplate.update(UPDATE_BY_ID_SQL, paramMap);

	}

	/**
	 * Gets the by id.
	 *
	 * @param id
	 *            the id
	 * @return the by id
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.dao.SystemElementsDao#getById(int)
	 */
	@Override
	public SystemElementPO getById(int id) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("id", id);
		List<SystemElementPO> result = namedJdbcTemplate.query(GET_BY_ID_SQL, paramMap, systemElementRowMapper);
		return result != null && result.size() != 0 ? result.get(0) : null;
	}

	/**
	 * Gets the by name and path.
	 *
	 * @param name
	 *            the name
	 * @param path
	 *            the path
	 * @return the by name and path
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.SystemElementsDao#getByNameAndPath(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public SystemElementPO getByNameAndPath(String name, String path) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("element_name", name);
		paramMap.put("element_folder", path);
		List<SystemElementPO> result = namedJdbcTemplate.query(GET_BY_NAME_AND_PATH_SQL, paramMap,
				systemElementRowMapper);
		return result != null && result.size() != 0 ? result.get(0) : null;
	}

	/**
	 * Gets the by path and types.
	 *
	 * @param path
	 *            the path
	 * @param types
	 *            the types
	 * @return the by path and types
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.SystemElementsDao#getByPathAndTypes(java.lang
	 * .String, com.unidata.mdm.backend.po.initializer.ElementTypePO[])
	 */
	@Override
	public List<SystemElementPO> getByPathAndTypes(String path, ElementTypePO... types) {

		List<String> sTypes = new ArrayList<>();
		for (int i = 0; i < types.length; i++) {
			sTypes.add(types[i].name());

		}
		MapSqlParameterSource parameters = new MapSqlParameterSource();

		parameters.addValue("element_types", sTypes);
		parameters.addValue("element_folder", path);
		List<SystemElementPO> result = namedJdbcTemplate.query(GET_BY_PATH_AND_TYPES_SQL, parameters,
				systemElementRowMapper);
		return result;
	}

	/**
	 * Gets the by type.
	 *
	 * @param type
	 *            the type
	 * @return the by type
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.SystemElementsDao#getByType(com.unidata.mdm.
	 * backend.po.initializer.ElementTypePO)
	 */
	@Override
	public List<SystemElementPO> getByType(ElementTypePO type) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("element_type", type.name());
		List<SystemElementPO> result = namedJdbcTemplate.query(GET_BY_TYPES_SQL, paramMap, systemElementRowMapper);
		return result;
	}

	/**
	 * Delete by id.
	 *
	 * @param id
	 *            the id
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.dao.SystemElementsDao#deleteById(int)
	 */
	@Override
	public void deleteById(int id) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("id", id);
		namedJdbcTemplate.update(DELETE_BY_ID_SQL, paramMap);

	}

	/**
	 * Delete by name and path.
	 *
	 * @param name
	 *            the name
	 * @param path
	 *            the path
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.SystemElementsDao#deleteByNameAndPath(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public void deleteByNameAndPath(String name, String path) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("element_name", name);
		paramMap.put("element_folder", path);
		namedJdbcTemplate.update(DELETE_BY_NAME_AND_PATH_SQL, paramMap);

	}

	/**
	 * Delete by path and types.
	 *
	 * @param path
	 *            the path
	 * @param types
	 *            the types
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.SystemElementsDao#deleteByPathAndTypes(java.
	 * lang.String, com.unidata.mdm.backend.po.initializer.ElementTypePO[])
	 */
	@Override
	public void deleteByPathAndTypes(String path, ElementTypePO... types) {
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put("element_types", Arrays.stream(types).map(ElementTypePO::name).collect(Collectors.toSet()));
		paramMap.put("element_folder", path);
		namedJdbcTemplate.update(DELETE_BY_PATH_AND_TYPES_SQL, paramMap);

	}

	/**
	 * Clear old.
	 *
	 * @param deleteAfter
	 *            the delete after
	 * @param types
	 *            the types
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.dao.SystemElementsDao#clearOld(java.util.Date,
	 * com.unidata.mdm.backend.po.initializer.ElementTypePO[])
	 */
	@Override
	public void clearOld(Date deleteAfter, ElementTypePO... types) {
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put("element_types", types);
		paramMap.put("created_at", deleteAfter);
		namedJdbcTemplate.update(CLEAR_OLD_SQL, paramMap);

	}

	@Override
	public void deleteByNameAndTypes(String name, ElementTypePO... types) {
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put("element_types", Arrays.stream(types).map(ElementTypePO::name).collect(Collectors.toSet()));
		paramMap.put("element_name", name);
		namedJdbcTemplate.update(DELETE_BY_NAME_AND_TYPES_SQL, paramMap);
		
	}


	@Override
	public void deleteByClassName(String className) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("element_class", className);
		namedJdbcTemplate.update(DELETE_BY_CLASS_NAME, paramMap);
		
	}

	@Override
	public void removeOldFunctions(Map<Integer, String> toUpdate) {
		Map<String, Object> paramMap = new HashMap<>();
		toUpdate.forEach((k, v)->{	
			paramMap.put("id", k);
			paramMap.put("element_class", v);
			namedJdbcTemplate.update(UPDATE_CLASS_NAMES_SQL, paramMap);
		});
		namedJdbcTemplate.update(DELETE_DUPLICATES, paramMap);
		
	}
}
