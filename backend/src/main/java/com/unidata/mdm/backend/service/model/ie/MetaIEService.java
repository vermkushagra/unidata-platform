package com.unidata.mdm.backend.service.model.ie;

import java.nio.file.Path;

import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
/**
 * 
 * @author ilya.bykov
 *
 */
public interface MetaIEService {
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
	void exportMeta(String storageId);

}
