package com.unidata.mdm.backend.service.model;

import java.util.Set;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;


/**
 * The Interface MetaDependencyService.
 * @author ilya.bykov
 */
public interface MetaDependencyService extends AfterContextRefresh{
	
	/**
	 * Calclulate dependencies.
	 *
	 * @param storageId the storage id
	 * @param forTypes the for types
	 * @param skipTypes the skip types
	 * @return the meta graph
	 */
	MetaGraph calculateDependencies(String storageId, Set<MetaType> forTypes, Set<MetaType> skipTypes);
}
