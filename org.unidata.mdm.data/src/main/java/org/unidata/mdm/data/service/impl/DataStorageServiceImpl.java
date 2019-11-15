package org.unidata.mdm.data.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata.mdm.data.configuration.DataConfigurationConstants;
import org.unidata.mdm.data.convert.DataClusterConverter;
import org.unidata.mdm.data.dao.DataStorageDAO;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.po.storage.DataClusterPO;
import org.unidata.mdm.data.service.DataStorageService;
import org.unidata.mdm.data.type.storage.DataCluster;
import org.unidata.mdm.system.exception.PlatformFailureException;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

/**
 * @author Mikhail Mikhailov
 * Data storage managemnt routines.
 */
@Service
public class DataStorageServiceImpl implements DataStorageService {
    /**
     * Cluster info.
     * TODO: put to HZ
     */
    private DataCluster cluster = null;
    /**
     * Storage metadata repo.
     */
    @Autowired
    private DataStorageDAO storageDAO;
    /**
     * HZ.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * Constructor.
     */
    public DataStorageServiceImpl() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterContextRefresh() {

        ILock lock = hazelcastInstance.getLock(DataConfigurationConstants.STORAGE_LOCK_NAME);
        lock.lock();
        try {

            DataClusterPO clusterPO = storageDAO.loadAndInit();
            if (Objects.isNull(clusterPO)) {
                throw new PlatformFailureException("Cluster metadata in invalid state.",
                        DataExceptionIds.EX_DATA_STORAGE_INVALID_STATE);
            }

            cluster = DataClusterConverter.of(clusterPO);

        } catch (Exception e) {
            cluster = null;
            throw new PlatformFailureException("Failed to start storage cluster.", e,
                    DataExceptionIds.EX_DATA_STORAGE_START_FAILED);
        } finally {
            lock.unlock();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getShardsCount() {
        return cluster.getShards().length;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNodesCount() {
        return cluster.getNodes().length;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DataCluster getCluster() {
        return cluster;
    }
}
