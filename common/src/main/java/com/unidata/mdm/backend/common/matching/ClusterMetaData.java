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
     * Matching group id
     */
    private Integer groupId;
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
    public Integer getGroupId() {
        return groupId;
    }

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
        private Integer groupId;
        private Integer ruleId;
        private String entityName;
        private String storage;

        private ClusterMetaDataBuilder() {
            super();
        }

        public ClusterMetaDataBuilder groupId(@Nullable Integer groupId) {
            this.groupId = groupId;
            return this;
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
            clusterMetaData.groupId = this.groupId;
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

        return Objects.equals(that.ruleId, ruleId) && Objects.equals(that.groupId, groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, groupId);
    }


    @Override
    public String toString() {
        return "ClusterMetaData{" +
                "groupId=" + groupId +
                ", ruleId=" + ruleId +
                ", entityName='" + entityName + '\'' +
                ", storage='" + storage + '\'' +
                '}';
    }
}
