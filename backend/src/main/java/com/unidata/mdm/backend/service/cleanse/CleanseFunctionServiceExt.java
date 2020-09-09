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

package com.unidata.mdm.backend.service.cleanse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.cleanse.CFCustomUploaderResponse;
import com.unidata.mdm.backend.common.service.CleanseFunctionService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
/**
 * 
 * @author ilya.bykov
 *
 */
public interface CleanseFunctionServiceExt extends CleanseFunctionService, AfterContextRefresh {

    /**
     * Pre-load and validate custom function.
     * TODO Refactor this method. It uses mixture of REST and core types, creates dependencies to REST!
     *
     * @param jarFile
     *            the jar file
     * @param saveToDb
     *            the save to db
     * @return the CF custom uploader response
     */
    CFCustomUploaderResponse preloadAndValidateCustomFunction(Path jarFile, boolean saveToDb);

    /**
     * Load and init custom cf.
     *
     * @param tempId
     *            the temp id
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void loadAndInit(String tempId) throws IOException;

	/**
	 * Import composite cleanse functions.
	 * 
	 * @param ccfs
	 *            composite cleanse functions.
	 */
	void importCompositeCleanseFunctions(List<CompositeCleanseFunctionDef> ccfs);

}