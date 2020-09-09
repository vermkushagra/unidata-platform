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
    private String clusterOwnerId;
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

    public String getClusterOwnerId() {
        return clusterOwnerId;
    }

    public void setClusterOwnerId(String clusterOwnerId) {
        this.clusterOwnerId = clusterOwnerId;
    }
}
