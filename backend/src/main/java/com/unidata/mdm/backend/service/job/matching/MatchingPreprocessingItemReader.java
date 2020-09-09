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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.unidata.mdm.backend.common.context.NestedSearchRequestContext;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext.NestedSearchType;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.search.fields.MatchingHeaderField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.meta.SimpleDataType;

public class MatchingPreprocessingItemReader implements ItemReader<Collection<Cluster>> {

    @Autowired
    private SearchServiceExt searchService;

    private ClusterMetaData clusterMetaData;

    /**
     * Block size
     */
    private Long blockSize;


    private Integer shardNumber;

    private List<Object> sortValues = null;

    @Override
    public Collection<Cluster> read() throws Exception {
        Collection<Cluster> clusters = getNextClusters(clusterMetaData, blockSize.intValue(), 0 , shardNumber);
        if (CollectionUtils.isNotEmpty(clusters)) {
            return clusters;
        }
        return null;
    }

    @Required
    public void setClusterMetaData(ClusterMetaData clusterMetaData) {
        this.clusterMetaData = clusterMetaData;
    }

    @Required
    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }


    @Required
    public void setShardNumber(Integer shardNumber) {
        this.shardNumber = shardNumber;
    }

    private Collection<Cluster> getNextClusters(@Nonnull ClusterMetaData clusterMetaData, int limit, int offset, Integer shardNumber){
        Date matchingDate = new Date();

        FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();

        if (Objects.nonNull(clusterMetaData.getRuleId())) {
            groupAndRule.addFormField(
                    FormField.strictString(MatchingHeaderField.FIELD_RULE_ID.getField(), clusterMetaData.getRuleId()));
        }

        groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, matchingDate));
        groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), matchingDate, null));


        SearchRequestContext nestedCtx = SearchRequestContext.builder(EntitySearchType.MATCHING, clusterMetaData.getEntityName())
                .page(0)
                // to do
                .count(100)
                .form(groupAndRule)
                .returnFields(Arrays.asList(
                        MatchingHeaderField.FIELD_ETALON_ID.getField(),
                        MatchingHeaderField.FIELD_RULE_ID.getField()))
                .build();

        SearchRequestContext main = SearchRequestContext.builder(EntitySearchType.MATCHING_HEAD, clusterMetaData.getEntityName())
                .onlyQuery(true)
                .page(limit == 0 ? offset : offset / limit)
                .count(limit)
                .shardNumber(shardNumber)
                .nestedSearch(NestedSearchRequestContext.builder(nestedCtx)
                        .nestedSearchType(NestedSearchType.HAS_CHILD)
                        .nestedQueryName("clusterData")
                        .minDocCount(2)
                        .build())
                .addSorting(Collections.singleton(new SortField("_uid", SortField.SortOrder.ASC, false)))
                .searchAfter(sortValues)
                .build();

        SearchResultDTO searchResult = searchService.search(main);
        sortValues = searchResult.getSortValues();

        List<Cluster> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(searchResult.getHits())){
            for(SearchResultHitDTO clusterHit : searchResult.getHits()){
                if(clusterHit.getInnerHits() != null && clusterHit.getInnerHits().get("clusterData") != null){
                    List<SearchResultHitDTO> hitClusterData = clusterHit.getInnerHits().get("clusterData");
                    Cluster newCluster = new Cluster(matchingDate);
                    ClusterMetaData.ClusterMetaDataBuilder newMetaDataBuilder = ClusterMetaData.builder();
                    newMetaDataBuilder.entityName(clusterMetaData.getEntityName());
                    newMetaDataBuilder.storage(clusterMetaData.getStorage());
                    newMetaDataBuilder.ruleId((Integer) hitClusterData.get(0).getFieldValue(MatchingHeaderField.FIELD_RULE_ID.getField()).getFirstValue());
                    newCluster.setClusterOwnerRecord((String)hitClusterData.get(0).getFieldValue(MatchingHeaderField.FIELD_ETALON_ID.getField()).getFirstValue());
                    newCluster.setMetaData(newMetaDataBuilder.build());

                    for(SearchResultHitDTO clusterRecordHit : hitClusterData){
                        newCluster.addRecordToCluster(new ClusterRecord(
                                clusterRecordHit.getFieldValue(MatchingHeaderField.FIELD_ETALON_ID.getField()).getFirstValue().toString()
                                , matchingDate, 100));
                    }
                    result.add(newCluster);
                }

            }
        }
        return result;
    }
}
