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

package com.unidata.mdm.backend.service.model.ie;

import java.nio.file.Path;

import com.unidata.mdm.backend.common.context.ExportContext;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
/**
 * 
 * @author ilya.bykov
 *
 */
public interface MetaIEService extends AfterContextRefresh{
	/**
	 * Validate zip file with meta model.
	 * 
	 * @param zipFile
	 *            path to meta model zip file.
	 * @param isOverride 
	 * @return Graph with file content.
	 */
	MetaGraph preloadMetaZip(Path zipFile, boolean isOverride);

	/**
	 * Import already preloaded metamodel.
	 * 
	 * @param graph
	 *            metamodel graph.
	 */
	MetaGraph importMetaZip(MetaGraph graph);

	/**
	 * Export meta model for the given storage id.
	 * 
	 * @param storageId
	 *            storage id.
	 */
	void exportMeta(String storageId, ExportContext exportContext);

}
