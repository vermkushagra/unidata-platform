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

package com.unidata.mdm.backend.po.matching;

import java.util.Collection;
import java.util.List;

/**
 * @author Dmitry Kopin on 19.02.2018.
 */
public class ClusterUpdate {
    private ClusterPO clusterForUpdate;
    private ClusterUpdateType updateType;
    private Collection<String> deletedClusterIds;

    public ClusterUpdate(ClusterPO clusterForUpdate, ClusterUpdateType updateType){
        this.clusterForUpdate = clusterForUpdate;
        this.updateType = updateType;
    }

    public ClusterUpdate(ClusterPO clusterForUpdate, Collection<String> deletedClusterIds){
        this.clusterForUpdate = clusterForUpdate;
        this.deletedClusterIds = deletedClusterIds;
        this.updateType = ClusterUpdateType.UPDATE;
    }
    public ClusterPO getClusterForUpdate() {
        return clusterForUpdate;
    }

    public ClusterUpdateType getUpdateType() {
        return updateType;
    }

    public Collection<String> getDeletedClusterIds() {
        return deletedClusterIds;
    }

    public enum ClusterUpdateType{
        INSERT, UPDATE, DELETE
    }
}
