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

import java.util.Date;
import java.util.Map;

public class ClusterPO {


    private Integer ruleId;

    private String entityName;

    private String storage;

    private Date matchingDate;

    private Long clusterId;

    private String clusterOwnerRecord;

    private Integer version;

    private Map<String, ClusterRecordPO> clusterRecordPOs;

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

    public String getClusterOwnerRecord() {
        return clusterOwnerRecord;
    }

    public void setClusterOwnerRecord(String clusterOwnerRecord) {
        this.clusterOwnerRecord = clusterOwnerRecord;
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
