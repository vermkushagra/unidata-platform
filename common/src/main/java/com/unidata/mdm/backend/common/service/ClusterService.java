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

package com.unidata.mdm.backend.common.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
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
    void upsertCluster(@Nonnull Cluster cluster, Boolean checkBlocked, boolean checkExist);
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
     * @param atDate optional filter for date
     * @param limit           - limit
     * @param offset          - offset
     * @param  preprocessing - use cluster preprocessing mode
     * @return collection of clusters
     */
    @Nonnull
    Collection<Cluster> getClusters(@Nonnull ClusterMetaData clusterMetaData,
                                    @Nullable Date atDate,
                                    int limit,
                                    int offset,
                                    boolean preprocessing);

    Collection<Cluster> getClustersPreprocessing(@Nonnull ClusterMetaData clusterMetaData, int limit, int offset,
                                    Integer shardNumber);

    /**
     * @param etalonId - etalon id
     * @param  preprocessing - use cluster preprocessing mode
     * @return collection of clusters
     */
    @Nonnull
    Collection<Cluster> getClusters(@Nonnull String etalonId, boolean preprocessing);


    /**
     * @param clusterMetaData - cluster meta data
     * @param  preprocessing - use cluster preprocessing mode
     * @return number of clusters
     */
    @Nonnull
    Long getClustersCount(ClusterMetaData clusterMetaData, boolean preprocessing);



    /**
     * @param etalonId - etalon id
     * @param  preprocessing - use cluster preprocessing mode
     * @return number of clusters which contains etalon
     */
    @Nonnull
    Long getClustersCount(@Nonnull String etalonId, boolean preprocessing);

    /**
     * @param clusterMetaData - cluster meta data
     * @return count of unique etalons in clusters
     */
    @Nonnull
    Long getUniqueEtalonsCount(@Nonnull ClusterMetaData clusterMetaData, @Nullable Date atDate);

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

    Map<String, Date> getEtalonIdsForAutoMerge(EtalonRecord etalonRecord);

    /**
     * Search new clusters by matching rules
     * @param etalon etalon record for search
     * @param date date for search
     * @param ruleId rule identifier, if null then search by all rules
     * @return clusters
     */
    Collection<Cluster> searchNewClusters(@Nonnull EtalonRecord etalon, @Nonnull Date date, Integer ruleId, boolean virtual);
}
