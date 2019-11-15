package org.unidata.mdm.meta.service;

import java.util.Set;

import org.unidata.mdm.meta.type.ie.MetaGraph;
import org.unidata.mdm.meta.type.ie.MetaType;
import org.unidata.mdm.system.service.AfterContextRefresh;


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
