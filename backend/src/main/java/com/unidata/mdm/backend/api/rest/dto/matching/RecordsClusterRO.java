package com.unidata.mdm.backend.api.rest.dto.matching;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.search.SearchResultHitFieldRO;

public class RecordsClusterRO {
    private String entityName;
    private Integer groupId;
    private Integer ruleId;
    private Long clusterId;
    private Date matchingDate;
    private Integer recordsCount;
    private Collection<ClusterRecordRO> records;
    private List<SearchResultHitFieldRO> preview;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

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

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Collection<ClusterRecordRO> getRecords() {
        return records;
    }

    public void setRecords(Collection<ClusterRecordRO> records) {
        this.records = records;
    }

    public Integer getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(Integer recordsCount) {
        this.recordsCount = recordsCount;
    }

    public Date getMatchingDate() {
        return matchingDate;
    }

    public void setMatchingDate(Date matchingDate) {
        this.matchingDate = matchingDate;
    }

    public List<SearchResultHitFieldRO> getPreview() {
        return preview;
    }

    public void setPreview(List<SearchResultHitFieldRO> preview) {
        this.preview = preview;
    }
}
