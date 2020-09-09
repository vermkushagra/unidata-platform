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

package com.unidata.mdm.backend.dao;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.dao.util.ClusterQuery;
import com.unidata.mdm.backend.po.matching.ClusterPO;
import com.unidata.mdm.backend.po.matching.ClusterUpdate;

/**
 * Data access object responsible for working with matched records
 */
public interface ClustersDao {

    /**
     * @param cluster - records cluster
     */
    void insertCluster(@Nonnull ClusterPO cluster);

    /**
     * @param clusterUpdates list of {@link ClusterUpdate} for update cluster.
     */
    void updateClusters(Collection<ClusterUpdate> clusterUpdates);

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
