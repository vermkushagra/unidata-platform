package com.unidata.mdm.backend.dao;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.dao.util.ClusterQuery;
import com.unidata.mdm.backend.po.matching.ClusterPO;

/**
 * Data access object responsible for working with matched records
 */
public interface ClustersDao {

    /**
     * @param cluster - records cluster
     */
    void insertCluster(@Nonnull ClusterPO cluster);

    /**
     * @param newCluster - new version of cluster.
     * @param existedCluster -  old version of cluster.
     */
    void updateCluster(@Nonnull ClusterPO existedCluster, @Nonnull ClusterPO newCluster);

    /**
     * @param clusterQuery - cluster query
     */
    void removeClusters(ClusterQuery clusterQuery);

    /**
     * @param clusterQuery - cluster query
     * @return collection of cluster ids.
     */
    @Nonnull
    Collection<Long> getClusterIds(@Nonnull ClusterQuery clusterQuery);

    /**
     * @param clusterQuery - query
     * @return records clusters
     */
    @Nonnull
    Collection<ClusterPO> getClusters(@Nonnull ClusterQuery clusterQuery);

    /**
     * @param clusterQuery - query
     * @return total count for request
     */
    @Nonnull
    Long getCount(@Nonnull ClusterQuery clusterQuery);

    /**
     * @param etalonIds collections of etalon ids
     * @return count of removed records
     */
    int removeRecordsFromClusters(@Nonnull Collection<String> etalonIds);

    /**
     * @param etalonIds - collections of etalon ids
     * @param clusterId - cluster id
     */
    void removeRecordsFromCluster(@Nonnull Collection<String> etalonIds, @Nonnull Long clusterId);

    /**
     * @param clusterQuery - query
     * @return count of unique records in clusters
     */
    Long getUniqueRecordsCount(@Nonnull ClusterQuery clusterQuery);

    /**
     * @param etalonIds - blocked ids multi map
     * @param cluster   - cluster for which it block
     */
    void addToBlockList(Multimap<String, String> etalonIds, ClusterPO cluster);

    /**
     * @param clusterQuery - cluster query
     * @return map where key it is a blocked id, and value it is record for which was allayed block.
     */
    Multimap<String, String> getBlockedPairs(@Nonnull ClusterQuery clusterQuery);

    /**
     * Remove records from block list
     *
     * @param clusterQuery - cluster query
     * @param clusterMetas - cluster meta - optional
     */
    void removeFromBlockList(@Nonnull ClusterQuery clusterQuery, @Nonnull Collection<ClusterMetaData> clusterMetas);

    /**
     * Remove records from block list
     *
     * @param clusterQuery - cluster query
     */
    void removeFromBlockList(@Nonnull ClusterQuery clusterQuery);
}
