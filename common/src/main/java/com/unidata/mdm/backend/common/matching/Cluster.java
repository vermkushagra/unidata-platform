package com.unidata.mdm.backend.common.matching;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.search.types.Aggregatable;

/**
 * Matching cluster info. Conntent depends on the context / operation.
 */
public class Cluster implements Aggregatable {
    /**
     * Matching date
     */
    @Nonnull
    private final Date matchingDate;
    /**
     * Cluster meta data
     */
    private ClusterMetaData metaData;
    /**
     * Cluster id
     */
    private Long clusterId;
    /**
     * Cluster owner record
     */
    private String clusterOwnerRecord;
    /**
     * Data.
     */
    private List<Object> data;
    /**
     * Cluster records
     */
    private Set<ClusterRecord> clusterRecords;

    public Cluster(@Nonnull Date matchingDate) {
        this.matchingDate = matchingDate;
        this.clusterId =  UUID.randomUUID().getMostSignificantBits();
    }

    public ClusterMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(ClusterMetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * @return the data
     */
    public List<Object> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<Object> data) {
        this.data = data;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Set<ClusterRecord> getClusterRecords() {
        return clusterRecords;
    }

    public void setClusterRecords(Set<ClusterRecord> clusterRecords) {
        this.clusterRecords = clusterRecords;
    }

    public void addRecordToCluster(@Nonnull ClusterRecord clusterRecord) {
        if (getClusterRecords() == null) {
            setClusterRecords(new HashSet<>());
        }
        getClusterRecords().add(clusterRecord);
    }

    @Nonnull
    public Date getMatchingDate() {
        return matchingDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean discard() {
        return CollectionUtils.isEmpty(clusterRecords);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean stop() {
        return false;
    }

    public String getClusterOwnerRecord() {
        return clusterOwnerRecord;
    }

    public void setClusterOwnerRecord(String clusterOwnerRecord) {
        this.clusterOwnerRecord = clusterOwnerRecord;
    }
}
