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

package com.unidata.mdm.backend.dao.rm;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.unidata.mdm.backend.po.DQErrorPO;

/**
 * The Class DQErrorsExtractor.
 */
public class DQErrorsExtractor implements ResultSetExtractor<List<DQErrorPO>> {
	/** The Constant ID. */
	private static final String ID = "id";

	/** The Constant REQUEST_ID. */
	private static final String REQUEST_ID = "request_id";

	/** The Constant RECORD_ID. */
	private static final String RECORD_ID = "record_id";

	/** The Constant ENTITY_NAME. */
	private static final String ENTITY_NAME = "entity_name";

	/** The Constant RULE_NAME. */
	private static final String RULE_NAME = "rule_name";

	/** The Constant SEVERITY. */
	private static final String SEVERITY = "severity";

	/** The Constant STATUS. */
	private static final String STATUS = "status";

	/** The Constant CATEGORY. */
	private static final String CATEGORY = "category";

	/** The Constant MESSAGE. */
	private static final String MESSAGE = "message";

	/** The Constant CREATED_AT. */
	private static final String CREATED_AT = "created_at";

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "created_by";

	/** The Constant UPDATED_AT. */
	private static final String UPDATED_AT = "updated_at";

	/** The Constant UPDATED_BY. */
	private static final String UPDATED_BY = "updated_by";


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.
	 * ResultSet)
	 */
	@Override
	public List<DQErrorPO> extractData(ResultSet rs) throws SQLException, DataAccessException {

	    List<DQErrorPO> errors = new ArrayList<>();
		while (rs.next()) {

			DQErrorPO error = new DQErrorPO();
			Array categories = rs.getArray(CATEGORY);
			error.setCategory(Arrays.asList((String[])categories.getArray()));
			error.setCreatedAt(rs.getDate(CREATED_AT));
			error.setCreatedBy(rs.getString(CREATED_BY));
			error.setId(rs.getLong(ID));
//			Array messages = rs.getArray(MESSAGE);
//			error.setMessage(Arrays.asList((String[])messages.getArray()));
			error.setEntityName(rs.getString(ENTITY_NAME));
			error.setRecordId(rs.getString(RECORD_ID));
			error.setRequestId(rs.getString(REQUEST_ID));
			Array rules = rs.getArray(RULE_NAME);
			error.setRuleName(Arrays.asList((String[])rules.getArray()));
			Array severities = rs.getArray(SEVERITY);
			error.setSeverity(Arrays.asList((String[])severities.getArray()));
			Array statuses = rs.getArray(STATUS);
			error.setStatus(Arrays.asList((String[])statuses.getArray()));
			error.setUpdatedAt(rs.getDate(UPDATED_AT));
			error.setUpdatedBy(rs.getString(UPDATED_BY));
			errors.add(error);

		}
		return errors;
	}

}
