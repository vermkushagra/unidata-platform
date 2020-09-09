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

package com.unidata.mdm.backend.service.job.matching;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class MatchingItemWriter implements ItemWriter<MatchingItemDto> {
    /**
     * Etalon data component.
     */
    @Autowired
    private EtalonRecordsComponent etalonComponent;

    @Autowired
    private ClusterService clusterService;

    /**
     * As of date.
     */
    private String jobRunTime;

    private Boolean checkBlocked;

    @Override
    public void write(List<? extends MatchingItemDto> items) throws Exception {
        Date atDate = new Date(Long.parseLong(jobRunTime));
        Multimap<MatchingRule, String> itemMaps = HashMultimap.create();
        items.forEach(item -> itemMaps.put(item.getMatchingRule(), item.getEtalonId()));

        for (Map.Entry<MatchingRule, String> key : itemMaps.entries()) {
            EtalonRecord etalonRecord = etalonComponent.loadEtalonData(key.getValue(), atDate, null, null, null, false, false);
            if(etalonRecord == null
                    || etalonRecord.getInfoSection().getStatus() != RecordStatus.ACTIVE
                    || etalonRecord.getInfoSection().getApproval() != ApprovalState.APPROVED){
                continue;
            }
            Collection<Cluster> clusters = clusterService.searchNewClusters(etalonRecord, atDate, key.getKey().getId(), false);
            if (CollectionUtils.isNotEmpty(clusters)) {
                clusters.forEach(cluster -> clusterService.upsertCluster(cluster, checkBlocked, true));
            }
        }
    }

    @Required
    public void setJobRunTime(String jobRunTime) {
        this.jobRunTime = jobRunTime;
    }

    //@Required
    public void setCheckBlocked(Boolean checkBlocked) {
        this.checkBlocked = checkBlocked;
    }
}
