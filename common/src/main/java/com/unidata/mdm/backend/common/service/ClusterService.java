package com.unidata.mdm.backend.common.service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.types.EtalonRecord;

/**
 * Responsible for working with matching groups and matching results.
 */
public interface ClusterService {

    /**
     * Upsert cluster
     * @param cluster cluster

     */
    void upsertCluster(@Nonnull Cluster cluster);

    /**
     * Upsert cluster
     * @param cluster cluster
     * @param useCache if true, result will be added to cluster cache
     */
    void upsertCluster(@Nonnull Cluster cluster, boolean useCache);

    /**
     * @param clusterMetaData - cluster meta data
     * @return collection of cluster ids.
     */
    @Nonnull
    @Deprecated
    Collection<Long> getClusterIds(@Nonnull ClusterMetaData clusterMetaData);

    /**
     * @param clusterId - cluster id
     * @return cluster of records
     */
    @Nullable
    Cluster getCluster(@Nonnull Long clusterId);

    /**
     * Remove records with this etalons from all clusters.
     * Remove all corrupted clusters
     *
     * @param etalonIds  - collection of etalon id
     * @param entityName - entityName
     */
    void excludeFromClusters(@Nonnull String entityName, @Nonnull Collection<String> etalonIds);

    /**
     * @param clusterMetaData cluster meta data
     */
    void removeAllClusters(@Nonnull ClusterMetaData clusterMetaData);

    /**
     * @param clusterMetaData - cluster meta data
     * @param limit           - limit
     * @param offset          - offset
     * @return collection of clusters
     */
    @Nonnull
    Collection<Cluster> getClusters(@Nonnull ClusterMetaData clusterMetaData, int limit, int offset, Integer shardNumber);

    /**
     * @param etalonId - etalon id
     * @return collection of clusters
     */
    @Nonnull
    Collection<Cluster> getClusters(@Nonnull String etalonId, int limit, int offset);


    /**
     * @param clusterMetaData - cluster meta data
     * @return number of clusters
     */
    @Nonnull
    Long getClustersCount(ClusterMetaData clusterMetaData);

    Collection<Cluster> getClusters(@Nonnull EtalonRecord etalonRecord, @Nonnull Integer groupId, @Nonnull Integer ruleId, boolean virtual);

    /**
     * @param etalonId - etalon id
     * @return number of clusters which contains etalon
     */
    @Nonnull
    Long getClustersCount(@Nonnull String etalonId);

    /**
     * @param clusterMetaData - cluster meta data
     * @return count of unique etalons in clusters
     */
    @Nonnull
    Long getUniqueEtalonsCount(@Nonnull ClusterMetaData clusterMetaData);

    /**
     * Exclude record from cluster
     *
     * @param etalonIds - etalon ids which will be excluded
     * @param clusterId - cluster id from will be excluded etalon
     */
    void excludeFromCluster(@Nonnull Collection<String> etalonIds, @Nonnull Long clusterId);

    /**
     * Add record with etalon id to black list for this cluster
     *
     * @param etalonIds - etalon ids
     * @param cluster   - cluster
     */
    void addToBlockList(@Nonnull Collection<String> etalonIds, @Nonnull Cluster cluster);

    /**
     * Remove records from block list
     *
     * @param etalonId     -  etalon id
     * @param clusterMetas - cluster meta which define special rule for dropping.
     */
    void dropFromBlockList(@Nonnull String etalonId, @Nonnull Collection<ClusterMetaData> clusterMetas);

    /**
     * Remove records from block list
     *
     * @param etalonIds - etalon ids
     */
    void dropFromBlockList(@Nonnull Collection<String> etalonIds);

    /**
     * Drop clusters and block lists for rule;
     *
     * @param entityName - entity name
     * @param ruleId     - rule id
     */
    void dropEveryThingForRule(@Nonnull String entityName, @Nonnull Integer ruleId);

    /**
     * Drop clusters and block lists for rule;
     *
     * @param entityName - entity name
     * @param groupId    - group id
     */
    void dropEveryThingForGroup(@Nonnull String entityName, @Nonnull Integer groupId);

    Map<String, Date> getEtalonIdsForAutoMerge(EtalonRecord etalonRecord);
}
