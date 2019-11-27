package org.unidata.mdm.data.dao;

import java.util.UUID;

import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.data.dao.impl.BaseStorageDAOImpl.DataNodeEntry;
import org.unidata.mdm.system.dao.BaseDao;

/**
 * @author Mikhail Mikhailov
 * Data storage DAO base class.
 */
public interface BaseStorageDAO extends BaseDao {
    /**
     * Selects connection entry by node number directly.
     * @param node the node number
     * @return connection entry
     */
    DataNodeEntry nodeSelect(int node);
    /**
     * Selects connection entry by shard number.
     * @param shard the shard number
     * @return connection entry
     */
    DataNodeEntry shardSelect(int shard);
    /**
     * Selects default connection entry.
     * @return connection entry
     */
    DataNodeEntry defaultSelect();
    /**
     * Selects connection entry by etalon id.
     * @param uuid the record (record/clsf/rel) UUID id
     * @return connection entry
     */
    DataNodeEntry keySelect(String uuid);
    /**
     * Selects connection entry by etalon id.
     * @param uuid the record (record/clsf/rel) UUID id
     * @return connection entry
     */
    DataNodeEntry keySelect(UUID uuid);
    /**
     * Selects connection entry by record external id.
     * @param id ext id
     * @param name entity name
     * @param system source system
     * @return connection entry
     */
    DataNodeEntry keySelect(String id, String name, String system);
    /**
     * Selects connection entry by record external id.
     * @param id ext id
     * @return connection entry
     */
    DataNodeEntry keySelect(ExternalId id);
}
