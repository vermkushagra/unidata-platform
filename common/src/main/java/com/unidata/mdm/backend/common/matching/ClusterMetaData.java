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

package com.unidata.mdm.backend.common.matching;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Cluster meta data
 */
public class ClusterMetaData implements Serializable {
    /**
     * Make sonar happy.
     */
    private static final long serialVersionUID = -600312241700693220L;
    /**
     * Matching rule id
     */
    private Integer ruleId;
    /**
     * Entity name
     */
    private String entityName;
    /**
     * Storage
     */
    private String storage;

    @Nullable
    public Integer getRuleId() {
        return ruleId;
    }

    @Nonnull
    public String getEntityName() {
        return entityName;
    }

    public String getStorage() {
        return storage;
    }

    public static ClusterMetaDataBuilder builder() {
        return new ClusterMetaDataBuilder();
    }

    /**
     * Cluster metadata builder.
     */
    public static final class ClusterMetaDataBuilder {
        private Integer ruleId;
        private String entityName;
        private String storage;

        private ClusterMetaDataBuilder() {
            super();
        }

        public ClusterMetaDataBuilder entityName(@Nonnull String entityName) {
            this.entityName = entityName;
            return this;
        }

        public ClusterMetaDataBuilder ruleId(@Nullable Integer ruleId) {
            this.ruleId = ruleId;
            return this;
        }

        public ClusterMetaDataBuilder storage(@Nonnull String storage) {
            this.storage = storage;
            return this;
        }

        public ClusterMetaData build() {

            if (Objects.isNull(this.entityName)) {
                throw new NullPointerException("Entity name is null.");
            }

            ClusterMetaData clusterMetaData = new ClusterMetaData();
            clusterMetaData.ruleId = this.ruleId;
            clusterMetaData.entityName = this.entityName;
            clusterMetaData.storage = this.storage;
            return clusterMetaData;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClusterMetaData)) return false;

        ClusterMetaData that = (ClusterMetaData) o;

        return Objects.equals(that.ruleId, ruleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId);
    }


    @Override
    public String toString() {
        return "ClusterMetaData{" +
                " ruleId=" + ruleId +
                ", entityName='" + entityName + '\'' +
                ", storage='" + storage + '\'' +
                '}';
    }
}
