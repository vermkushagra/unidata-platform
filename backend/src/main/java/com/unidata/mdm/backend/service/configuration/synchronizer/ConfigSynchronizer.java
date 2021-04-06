package com.unidata.mdm.backend.service.configuration.synchronizer;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

/**
 * Service performs synchronization and validation of configuration/shared
 * resources between nodes in cluster.
 * 
 * @author ilya.bykov
 *
 */
public interface ConfigSynchronizer extends AfterContextRefresh {
	/**
	 * Check configuration and resource files of the current node. If calculated
	 * fingerprint is not the same as earlier published to the hazelcast method
	 * will throw an exception, thus preventing application server from
	 * starting.
	 */
	void validate();

	/**
	 * Synchronize resources files between the nodes.
	 * 
	 */
	void synchronize();
}
