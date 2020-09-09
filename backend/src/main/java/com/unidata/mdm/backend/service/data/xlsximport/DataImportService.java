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

package com.unidata.mdm.backend.service.data.xlsximport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 * The Interface DataImportService.
 * @author ilya.bykov
 */
public interface DataImportService{
	
	/**
	 * Import data.
	 *
	 * @param data
	 *            the data
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	void importData(File data, String entityNam, String sourceSystem, String creationDate, boolean mergeWithPrevious);
	
	/**
	 * Creates template file.
	 *
	 * @param entityName
	 *            the entity name to import.
	 * @return template file as an byte array.
	 */
	ByteArrayOutputStream createTemplateFile(String entityName);
}
