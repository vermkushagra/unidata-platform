package com.unidata.mdm.backend.po.matching;

import java.util.Date;
import java.util.Map;

public class ClusterPO {

    private Integer groupId;

    private Integer ruleId;

    private String entityName;

    private String storage;

    private Date matchingDate;

    private Long clusterId;

    private String clusterHash;

    private Integer version;

    private Map<String, ClusterRecordPO> clusterRecordPOs;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public Date getMatchingDate() {
        return matchingDate;
    }

    public void setMatchingDate(Date matchingDate) {
        this.matchingDate = matchingDate;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterHash() {
        return clusterHash;
    }

    public void setClusterHash(String clusterHash) {
        this.clusterHash = clusterHash;
    }

    public Map<String, ClusterRecordPO> getClusterRecordPOs() {
        return clusterRecordPOs;
    }

    public void setClusterRecordPOs(Map<String, ClusterRecordPO> clusterRecordPOs) {
        this.clusterRecordPOs = clusterRecordPOs;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
