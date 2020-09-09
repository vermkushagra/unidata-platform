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

package com.unidata.mdm.backend.dao;

import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.service.data.xlsximport.XLSXHeader;

/**
 * The Interface DataImportDao.
 */
public interface DataImportDao {

	/** The import schema. */
	String IMPORT_SCHEMA = "unidata_data_import._";

	/**
	 * Save data.
	 *
	 * @param toSave
	 *            the to save
	 * @param headers
	 *            the headers
	 * @param entityName
	 *            the entity name
	 * @param modelVersion
	 *            the model version
	 * @param fileName
	 *            the file name
	 * @param fileCreationDate
	 *            the file creation date
	 */
	void saveData(List<Map<String, Object>> toSave, List<XLSXHeader> headers, String entityName, String sourceSystem, String modelVersion,
			String fileName, String fileCreationDate);

	/**
	 * Creates the table.
	 *
	 * @param headers
	 *            the headers
	 * @param entityName
	 *            the entity name
	 * @param modelVersion
	 *            the model version
	 * @param fileName
	 *            the file name
	 * @param fileCreationDate
	 *            the file creation date
	 */
	void createImportTable(List<XLSXHeader> headers, String entityName, String modelVersion, String fileName,
			String fileCreationDate);

	/**
	 * Drop import table.
	 *
	 * @param entityName
	 *            the entity name
	 * @param modelVersion
	 *            the model version
	 * @param fileName
	 *            the file name
	 * @param fileCreationDate
	 *            the file creation date
	 */
	void dropImportTable(String entityName, String modelVersion, String fileName, String fileCreationDate);
	/**
	 * Drop import table.
	 *
	 * @param tableName
	 *            the table name
	 */
	void dropImportTable(String tableName);
	/**
	 * Purge import table.
	 *
	 * @param entityName
	 *            the entity name
	 * @param modelVersion
	 *            the model version
	 * @param fileName
	 *            the file name
	 * @param fileCreationDate
	 *            the file creation date
	 */
	void purgeImportTable(String entityName, String modelVersion, String fileName, String fileCreationDate);

	/**
	 * Construct table name.
	 *
	 * @param entityName
	 *            the entity name
	 * @param modelVersion
	 *            the model version
	 * @param fileName
	 *            the file name
	 * @param fileCreationDate
	 *            the file creation date
	 * @return the string
	 */
	String constructTableName(String entityName, String modelVersion, String fileName, String fileCreationDate);

	/**
	 * Gets the connection url.
	 *
	 * @return the connection url
	 */
	String getConnectionURL();
}
