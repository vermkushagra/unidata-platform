package org.unidata.mdm.data.service;

import org.unidata.mdm.data.type.storage.DataCluster;
import org.unidata.mdm.system.service.AfterContextRefresh;

/**
 * @author Mikhail Mikhailov
 * Internal mostly data storage manipulation routines.
 */
public interface DataStorageService extends AfterContextRefresh {
    /**
     * Gets current shards count.
     * @return shards count
     */
    int getShardsCount();
    /**
     * Gets current nodes count.
     * @return nodes count.
     */
    int getNodesCount();
    /**
     * Gets the cluster instance.
     * @return cluster instance
     */
    DataCluster getCluster();
}
