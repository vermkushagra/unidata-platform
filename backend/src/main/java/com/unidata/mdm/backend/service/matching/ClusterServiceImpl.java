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

package com.unidata.mdm.backend.service.matching;

import static com.unidata.mdm.backend.dao.util.ClusterQuery.builder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.search.FacetName;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext;
import com.unidata.mdm.backend.common.context.NestedSearchRequestContext.NestedSearchType;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.MatchingHeaderField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.ClustersDao;
import com.unidata.mdm.backend.dao.util.ClusterQuery;
import com.unidata.mdm.backend.po.matching.ClusterPO;
import com.unidata.mdm.backend.po.matching.ClusterUpdate;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.SimpleDataType;

import reactor.core.publisher.Flux;

@Service
public class ClusterServiceImpl implements ClusterService, ConfigurationUpdatesConsumer {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterServiceImpl.class);

    private AtomicInteger maxClusterSize = new AtomicInteger(
            (Integer) UnidataConfigurationProperty.UNIDATA_MATCHING_MAX_CLUSTER_SIZE.getDefaultValue().get()
    );


    /**
     * Common component for keys resolution.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;

    /**
     * Matched records dao
     */
    @Autowired
    private ClustersDao clustersDao;
    /**
     * Conversion service
     */
    @Autowired
    private ConversionService conversionService;

    @Autowired
    private SearchServiceExt searchService;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private MatchingRulesService matchingRulesService;

    @Autowired
    private MetaModelService metaModelService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upsertCluster(@Nonnull Cluster cluster, Boolean checkBlocked, boolean checkExist) {
        MeasurementPoint.start();
        try {
//            if(checkBlocked){
//                filterBlockedRecords(cluster);
//
//                if (cluster.getClusterRecords().size() < 2) {
//                    LOGGER.debug(
//                            "Cluster insertion interrupted because cluster is incorrect, records {} , cluster identifier {}",
//                            cluster.getClusterRecords().size(), null);
//                    return;
//                }
//
//            }

            Collection<ClusterUpdate> clustersForUpdate;
            ClusterQuery clusterQuery = builder()
                   .withRuleId(cluster.getMetaData().getRuleId())
                    .withEntityName(cluster.getMetaData().getEntityName())
                    //.withEtalonIds(Collections.singletonList(cluster.getClusterOwnerRecord()))
                    .withEtalonIds(cluster.getClusterRecords().stream()
                            .map(ClusterRecord::getEtalonId)
                            .collect(toList()))
                    .build();

            clustersForUpdate = collectClustersForUpdate(cluster, checkExist ?
                    clustersDao.getClusters(clusterQuery) : Collections.emptyList());


            if (CollectionUtils.isEmpty(clustersForUpdate)) {
                return;
            }
            clustersDao.updateClusters(clustersForUpdate);
        } catch (Exception e) {
            LOGGER.warn("Cluster upsert was reject, because cluster was already created{}", e);
        } finally {
            MeasurementPoint.stop();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void excludeFromCluster(@Nonnull Collection<String> etalonIds, @Nonnull Long clusterId) {
        MeasurementPoint.start();
        try {
            LOGGER.debug("Exclude from cluster {}. Etalons {}", clusterId, etalonIds);
            clustersDao.removeRecordsFromCluster(etalonIds, clusterId);
            ClusterQuery clusterQuery = builder()
                    .withClusterId(clusterId)
                    .corrupted()
                    .build();
            clustersDao.removeClusters(clusterQuery);
        } finally {
            MeasurementPoint.stop();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToBlockList(@Nonnull Collection<String> etalonIds, @Nonnull Cluster cluster) {
        LOGGER.debug("Add to block list records: {} for cluster {}", etalonIds, cluster.getClusterId());
        Collection<String> blockedFor = cluster.getClusterRecords().stream()
                .map(ClusterRecord::getEtalonId)
                .collect(toList());
        blockedFor.removeAll(etalonIds);
        Multimap<String, String> blockMap = HashMultimap.create(etalonIds.size(), 2);
        etalonIds.forEach(id -> blockMap.putAll(id, blockedFor));
        ClusterPO clusterPO = conversionService.convert(cluster, ClusterPO.class);
        clustersDao.addToBlockList(blockMap, clusterPO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dropFromBlockList(@Nonnull String etalonId, @Nonnull Collection<ClusterMetaData> clusterMetas) {
        if (clusterMetas.isEmpty()) {
            return;
        }
        LOGGER.debug("Drop from block lists {}", etalonId);
        String entityName = clusterMetas.iterator().next().getEntityName();
        ClusterQuery clusterQuery = builder()
                .withEntityName(entityName)
                .withEtalonIds(Collections.singleton(etalonId))
                .build();
        clustersDao.removeFromBlockList(clusterQuery, clusterMetas);
    }

    @Override
    public void dropFromBlockList(@Nonnull Collection<String> etalonIds) {
        LOGGER.debug("Drop from block lists {}", etalonIds);
        ClusterQuery clusterQuery = builder()
                .withEtalonIds(etalonIds)
                .build();
        clustersDao.removeFromBlockList(clusterQuery);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void excludeFromClusters(@Nonnull String entityName, @Nonnull Collection<String> etalonIds) {
        MeasurementPoint.start();
        try {
            LOGGER.debug("Exclude from clusters {}", etalonIds);
            int countOfRemovedRecords = clustersDao.removeRecordsFromClusters(etalonIds);
            if (countOfRemovedRecords == 0) {
                return;
            }
            ClusterQuery clusterQuery = builder()
                    .withEntityName(entityName)
                    .corrupted()
                    .build();
            clustersDao.removeClusters(clusterQuery);
        } finally {
            MeasurementPoint.stop();
        }
    }

    @Nonnull
    @Override
    public Collection<Long> getClusterIds(@Nonnull ClusterMetaData clusterMetaData) {
        ClusterQuery clusterQuery = builder()
                .withRuleId(clusterMetaData.getRuleId())
                .withEntityName(clusterMetaData.getEntityName())
                .ordered()
                .build();
        return clustersDao.getClusterIds(clusterQuery);
    }

    @Nullable
    @Override
    public Cluster getCluster(@Nonnull Long clusterId) {
        ClusterQuery clusterQuery = builder().withClusterId(clusterId).build();
        Collection<Cluster> result = clustersDao.getClusters(clusterQuery).stream()
                .map(cluster -> conversionService.convert(cluster, Cluster.class))
                .collect(Collectors.toList());
        return result.size() == 1 ? result.iterator().next() : null;
    }

    @Nonnull
    @Override
    public Collection<Cluster> getClusters(@Nonnull ClusterMetaData clusterMetaData,
                                           @Nullable Date atDate,
                                           int limit,
                                           int offset,
                                           boolean preprocessing) {
        if (preprocessing) {
            return getClustersPreprocessing(clusterMetaData, limit, offset, null);
        }

        Collection<ClusterPO> clustersPO = clustersDao.getClusters(ClusterQuery.builder()
                .withEntityName(clusterMetaData.getEntityName())
                .withRuleId(clusterMetaData.getRuleId())
                .withMatchingDate(atDate)
                .withLimit(limit)
                .withOffset(offset)
                .build());
        if (CollectionUtils.isNotEmpty(clustersPO)) {
            return clustersPO.stream()
                    .map(cluster -> conversionService.convert(cluster, Cluster.class))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<Cluster> getClustersPreprocessing(@Nonnull ClusterMetaData clusterMetaData, int limit, int offset, Integer shardNumber) {
        MeasurementPoint.start();
        try {
            Date matchingDate = new Date();

            FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();

            if (Objects.nonNull(clusterMetaData.getRuleId())) {
                groupAndRule.addFormField(
                        FormField.strictString(getRuleIdField(), clusterMetaData.getRuleId()));
            }

            groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, matchingDate));
            groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), matchingDate, null));


            SearchRequestContext nestedCtx = SearchRequestContext.builder(EntitySearchType.MATCHING, clusterMetaData.getEntityName())
                    .page(0)
                    .count(maxClusterSize.get())
                    .form(groupAndRule)
                    //.returnFields(Arrays.asList(
                    //        MatchingHeaderField.FIELD_ETALON_ID.getField(),
                    //        MatchingHeaderField.FIELD_RULE_ID.getField()))
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
                    .build();

            SearchResultDTO searchResult = searchService.search(main);
            if (CollectionUtils.isNotEmpty(searchResult.getHits())) {
                List<String> parentIds = searchResult.getHits().stream()
                        .map(SearchResultHitDTO::getId)
                        .collect(toList());

                SearchRequestContext clusterCtx = SearchRequestContext.builder(EntitySearchType.MATCHING, clusterMetaData.getEntityName())
                        .form(FormFieldsGroup.createAndGroup().addFormField(
                                FormField.strictValues(SimpleDataType.STRING, MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField(), parentIds)
                        ))
                        .returnFields(Arrays.asList(
                                MatchingHeaderField.FIELD_ETALON_ID.getField(),
                                MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField(),
                                MatchingHeaderField.FIELD_RULE_ID.getField()))
                        .onlyQuery(true)
                        .page(0)
                        .count(limit * maxClusterSize.get())
                        .shardNumber(shardNumber)
                        .build();
                SearchResultDTO clusters = searchService.search(clusterCtx);
                Map<String, Cluster> result = new HashMap<>();
                for (SearchResultHitDTO clusterHit : clusters.getHits()) {
                    String clusterHash = clusterHit.getFieldValue(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField()).getFirstValue().toString();
                    Cluster c = result.get(clusterHash);
                    if (c == null) {
                        c = new Cluster(matchingDate);
                        ClusterMetaData.ClusterMetaDataBuilder newMetaDataBuilder = ClusterMetaData.builder();
                        newMetaDataBuilder.entityName(clusterMetaData.getEntityName());
                        newMetaDataBuilder.storage(clusterMetaData.getStorage());
                        newMetaDataBuilder.ruleId((Integer) clusterHit.getFieldValue(MatchingHeaderField.FIELD_RULE_ID.getField()).getFirstValue());
                        c.setClusterOwnerRecord((String) clusterHit.getFieldValue(MatchingHeaderField.FIELD_ETALON_ID.getField()).getFirstValue());
                        c.setMetaData(newMetaDataBuilder.build());
                        result.put(clusterHash, c);
                    }
                    c.addRecordToCluster(new ClusterRecord(
                            clusterHit.getFieldValue(MatchingHeaderField.FIELD_ETALON_ID.getField()).getFirstValue().toString()
                            , matchingDate, 100));
                }
                return result.values();
            }
            return Collections.emptyList();


        } finally {
            MeasurementPoint.stop();
        }
    }

    @Nonnull
    @Override
    public Collection<Cluster> getClusters(@Nonnull String etalonId, boolean preprocessing) {
        if (preprocessing) {
            return getClusterPreprocessing(etalonId);
        }

        Collection<ClusterPO> clustersPO = clustersDao.getClusters(builder()
                .withEtalonIds(Collections.singleton(etalonId))
                .build());

        if (CollectionUtils.isNotEmpty(clustersPO)) {
            return clustersPO.stream()
                    .map(cluster -> conversionService.convert(cluster, Cluster.class))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private Collection<Cluster> getClusterPreprocessing(@Nonnull String etalonId) {
        Date matchingDate = new Date();

        RecordKeys keys = commonComponent.identify(EtalonKey.builder()
                .id(etalonId)
                .build());

        if (Objects.isNull(keys)) {
            return Collections.emptyList();
        }

        Collection<Cluster> clusters = matchingService.constructPreprocessing(etalonId, matchingDate);

        return getClusters(matchingDate, keys.getEntityName(), etalonId, clusters, false);
    }

    @Nonnull
    @Override
    public Long getClustersCount(@Nonnull ClusterMetaData clusterMetaData, boolean preprocessing) {
        if (!preprocessing) {
            return clustersDao.getCount(builder()
                    .withRuleId(clusterMetaData.getRuleId())
                    .withEntityName(clusterMetaData.getEntityName())
                    .build());
        }

        Date matchingDate = new Date();

        FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();

        if (Objects.nonNull(clusterMetaData.getRuleId())) {
            groupAndRule.addFormField(
                    FormField.strictString(getRuleIdField(), clusterMetaData.getRuleId()));
        }

        groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, matchingDate));
        groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), matchingDate, null));


        SearchRequestContext nestedCtx = SearchRequestContext.builder(EntitySearchType.MATCHING, clusterMetaData.getEntityName())
                .page(0)
                .count(maxClusterSize.get())
                .form(groupAndRule)
                .returnFields(Arrays.asList(
                        MatchingHeaderField.FIELD_ETALON_ID.getField(),
                        MatchingHeaderField.FIELD_RULE_ID.getField()))
                .build();

        SearchRequestContext main = SearchRequestContext.builder(EntitySearchType.MATCHING_HEAD, clusterMetaData.getEntityName())
                .onlyQuery(true)
                .countOnly(true)
                .nestedSearch(NestedSearchRequestContext.builder(nestedCtx)
                        .nestedSearchType(NestedSearchType.HAS_CHILD)
                        .nestedQueryName("clusterData")
                        .minDocCount(2)
                        .build())
                .build();

        return searchService.search(main).getTotalCount();
    }

    @Nonnull
    @Override
    public Long getClustersCount(@Nonnull String etalonId, boolean preprocessing) {
        if (preprocessing) {
            return (long) getClusters(etalonId, true).size();
        } else {
            return clustersDao.getCount(builder()
                    .withEtalonIds(Collections.singleton(etalonId))
                    .build());
        }


    }


    private Collection<Cluster> getClusters(Date matchingDate, String entityName, String etalonId, Collection<Cluster> clusters, boolean virtual) {
        Map<Cluster, SearchRequestContext> searchByGroups = new HashMap<>();
        List<Cluster> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(clusters)) {
            for (Cluster cluster : clusters) {

                FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();

                groupAndRule.addFormField(
                        FormField.strictString(getRuleIdField(), cluster.getMetaData().getRuleId()));

                groupAndRule.addFormField(
                        FormField.strictString(getClusterDataField(),
                                String.join(SearchUtils.PIPE_SEPARATOR, cluster.getData().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList()))));

                groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, matchingDate));
                groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), matchingDate, null));

                searchByGroups.put(cluster, SearchRequestContext.forEtalon(
                        EntitySearchType.MATCHING, entityName)
                        .totalCount(true)
                        .onlyQuery(true)
                        .skipEtalonId(true)
                        .form(Collections.singletonList(groupAndRule))
                        .returnFields(Collections.singletonList(MatchingHeaderField.FIELD_ETALON_ID.getField()))
                        .page(0)
                        .count(maxClusterSize.get())
                        .build());
            }

            Map<SearchRequestContext, SearchResultDTO> searchResult = searchService.search(ComplexSearchRequestContext.multi(searchByGroups.values()));

            for (Map.Entry<Cluster, SearchRequestContext> entry : searchByGroups.entrySet()) {
                Cluster cluster = entry.getKey();

                SearchResultDTO foundedClusters = searchResult.get(entry.getValue());
                if ((virtual && foundedClusters.getTotalCount() > 0) || foundedClusters.getTotalCount() > 1) {

                    ClusterMetaData metaData = ClusterMetaData.builder()
                            .entityName(entityName)
                            .ruleId(cluster.getMetaData().getRuleId())
                            .build();
                    Cluster newCluster = new Cluster(matchingDate);
                    newCluster.setClusterOwnerRecord(etalonId);
                    newCluster.setMetaData(metaData);
                    if (CollectionUtils.isNotEmpty(foundedClusters.getHits())) {
                        for (SearchResultHitDTO hit : foundedClusters.getHits()) {
                            newCluster.addRecordToCluster(new ClusterRecord(hit.getFieldValue(MatchingHeaderField.FIELD_ETALON_ID.getField()).getFirstValue().toString(), matchingDate, 100));
                        }
                    }
                    // if contains not only master cluster etalon record
                    if (newCluster.getClusterRecords().stream().anyMatch(clusterRecord -> !clusterRecord.getEtalonId().equals(etalonId))) {
                        result.add(newCluster);
                    }
                }
            }
        }

        return result;
    }

    @Nonnull
    @Override
    public Long getUniqueEtalonsCount(@Nonnull ClusterMetaData clusterMetaData, @Nullable Date atDate) {
        ClusterQuery clusterQuery = builder()
                .withRuleId(clusterMetaData.getRuleId())
                .withEntityName(clusterMetaData.getEntityName())
                .withMatchingDate(atDate)
                .build();
        return clustersDao.getUniqueRecordsCount(clusterQuery);
    }

    @Override
    public void removeAllClusters(@Nonnull ClusterMetaData cmd) {
        clustersDao.removeClusters(builder()
                .withEntityName(cmd.getEntityName())
                .withRuleId(cmd.getRuleId())
                .build());
    }

    @Override
    public void dropEveryThingForRule(@Nonnull String entityName, @Nonnull Integer ruleId) {
        FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();

        groupAndRule.addFormField(
                FormField.strictString(MatchingHeaderField.FIELD_RULE_ID.getField(), ruleId));

        SearchRequestContext matchingContext = SearchRequestContext.forEtalon(
                EntitySearchType.MATCHING, entityName)
                .totalCount(true)
                .onlyQuery(true)
                .skipEtalonId(true)
                .form(Collections.singletonList(groupAndRule))
                .returnFields(Collections.singletonList(MatchingHeaderField.FIELD_ETALON_ID.getField()))
                .build();
        searchService.deleteFoundResult(matchingContext);
    }



    @Override
    public Map<String, Date> getEtalonIdsForAutoMerge(EtalonRecord etalonRecord) {
        Map<String, Date> result = new HashMap<>();
        String entityName = etalonRecord.getInfoSection().getEntityName();

        Collection<MatchingRule> rulesForAutoMerge =
                matchingRulesService.getActiveMatchingRulesByEntityName(entityName).stream()
                .filter(matchingRule -> matchingRule.isAutoMerge())
                .collect(toList());

        if (CollectionUtils.isNotEmpty(rulesForAutoMerge)) {
            for(MatchingRule rule : rulesForAutoMerge){
                ClusterQuery clusterQuery = builder()
                        .withRuleId(rule.getId())
                        .withEntityName(entityName)
                        .withEtalonIds(Collections.singletonList(etalonRecord.getInfoSection().getEtalonKey().getId()))
                        .build();
                Collection<ClusterPO> clusters = clustersDao.getClusters(clusterQuery);
                clusters.forEach(cluster ->
                        cluster.getClusterRecordPOs().values().forEach(clusterRecord -> {
                            result.put(clusterRecord.getEtalonId(), cluster.getMatchingDate());
                        }));
            }
        }

        return result;
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String maxClusterSizeKey = UnidataConfigurationProperty.UNIDATA_MATCHING_MAX_CLUSTER_SIZE.getKey();
        updates
                .filter(values ->
                        values.containsKey(maxClusterSizeKey) && values.get(maxClusterSizeKey).isPresent()
                )
                .map(values -> (Integer) values.get(maxClusterSizeKey).get())
                .subscribe(maxClusterSize::set);
    }

    @Override
    public Collection<Cluster> searchNewClusters(@Nonnull EtalonRecord etalon, @Nonnull Date date, Integer ruleId, boolean virtual) {
        MeasurementPoint.start();
        try {
            Collection<MatchingRule> matchingRules = null;
            if (ruleId == null) {
                matchingRules = matchingRulesService.getActiveMatchingRulesByEntityName(etalon.getInfoSection().getEntityName());
            } else {
                MatchingRule rule = matchingRulesService.getMatchingRule(ruleId);
                if (rule == null) {
                    throw new SystemRuntimeException("Can't find a rule with id " + ruleId,
                            ExceptionId.EX_MATCHING_GROUP_OR_RULE_NOT_FOUND, ruleId);
                }
                if(rule.isActive()) {
                    matchingRules = Collections.singleton(rule);
                }
            }

            Collection<Cluster> result = new ArrayList<>();
            if (CollectionUtils.isEmpty(matchingRules)) {
                return result;
            }

            // UN-7980
            Map<String, AttributeInfoHolder> attributesMap
                = metaModelService.getAttributesInfoMap(etalon.getInfoSection().getEntityName());

            for (MatchingRule rule : matchingRules) {
                    SearchRequestContext ctx = SearchRequestContext.builder(EntitySearchType.ETALON_DATA, etalon.getInfoSection().getEntityName())
                            .form(rule.getFormFieldGroup(etalon, attributesMap))
                            .count(maxClusterSize.get())
                            .returnFields(Collections.singletonList(RecordHeaderField.FIELD_ETALON_ID.getField()))
                            .facets(Arrays.asList(FacetName.FACET_NAME_ACTIVE_ONLY, FacetName.FACET_NAME_PUBLISHED_ONLY))
                            .asOf(date)
                            .build();

                    SearchResultDTO searchResult = searchService.search(ctx);

                    Set<ClusterRecord> matchedPairs = searchResult.getHits().stream()
                            .map(hit -> hit.getFieldValue(RecordHeaderField.FIELD_ETALON_ID.getField()))
                            .filter(Objects::nonNull)
                            .filter(SearchResultHitFieldDTO::isNonNullField)
                            .filter(SearchResultHitFieldDTO::isSingleValue)
                            .map(field -> field.getFirstValue().toString())
                            .map(etalonID -> new ClusterRecord(etalonID, date, 100))
                            .collect(toSet());

                    //there could be logic which process this.
                    if (matchedPairs.isEmpty() || (!virtual && matchedPairs.size() < 2)) {
                        continue;
                    }

                    if(!virtual){
                        //for case if matchedPairs size > maxClusterSize
                        matchedPairs.add( new ClusterRecord(etalon.getInfoSection().getEtalonKey().getId(), date, 100));
                        if(matchedPairs.size() > maxClusterSize.longValue() && matchedPairs.size() > 2){
                            matchedPairs.remove(matchedPairs.stream()
                                    .filter(clusterRecord -> !clusterRecord.getEtalonId().equals(
                                            etalon.getInfoSection().getEtalonKey().getId()))
                                    .findAny().get());
                        }
                    }


                    Cluster cluster = new Cluster(date);

                    matchedPairs.forEach(cluster::addRecordToCluster);

                    ClusterMetaData newClusterMeta = ClusterMetaData.builder()
                            .entityName(rule.getEntityName())
                            .ruleId(rule.getId())
                            .build();
                    cluster.setMetaData(newClusterMeta);
                    cluster.setClusterId(null);
                    cluster.setClusterOwnerRecord(etalon.getInfoSection().getEtalonKey().getId());
                    result.add(cluster);
                }
            return result;
        } finally {
            MeasurementPoint.stop();
        }
    }


    /*Filter record from cluster which in block list*/
    //todo replace to cluster!(and made block list part of cluster)
    private void filterBlockedRecords(@Nonnull Cluster cluster) {
        MeasurementPoint.start();
        try {
            ClusterQuery clusterQuery = builder()
                    .withRuleId(cluster.getMetaData().getRuleId())
                    .withEntityName(cluster.getMetaData().getEntityName())
                    .withClusterIdentifier(null)
                    .build();

            Multimap<String, String> blockedIds = clustersDao.getBlockedPairs(clusterQuery);
            if (blockedIds.isEmpty()) {
                return;
            }
            Collection<String> clusterIds = cluster.getClusterRecords()
                    .stream()
                    .map(ClusterRecord::getEtalonId)
                    .collect(toList());
            int initialSize = clusterIds.size();

            for (String blockedId : blockedIds.keySet()) {
                boolean containsBlocked = clusterIds.contains(blockedId);
                if (!containsBlocked) {
                    continue;
                }
                Collection<String> forIds = blockedIds.get(blockedId);
                boolean contains = clusterIds.stream().anyMatch(forIds::contains);
                if (contains) {
                    clusterIds.remove(blockedId);
                }
            }
            if (initialSize != clusterIds.size()) {
                cluster.getClusterRecords().removeIf(record -> !clusterIds.contains(record.getEtalonId()));
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    // todo think about strategy
    private Collection<ClusterUpdate> collectClustersForUpdate(Cluster newCluster, Collection<ClusterPO> existClusters) {
        final ClusterPO newClusterPO = conversionService.convert(newCluster, ClusterPO.class);
        // if new cluster in not contains new information.
        if (existClusters.stream()
                .anyMatch(clusterPO -> clusterPO.getClusterRecordPOs().keySet().containsAll(newClusterPO.getClusterRecordPOs().keySet()))) {
            return Collections.emptyList();
        }
        List<ClusterUpdate> result = new ArrayList<>();
        for (ClusterPO existCluster : existClusters) {
            Collection<String> forExclude = CollectionUtils.retainAll(existCluster.getClusterRecordPOs().keySet(),
                    newClusterPO.getClusterRecordPOs().keySet());

            if (forExclude.size() > 0) {
                if (existCluster.getClusterRecordPOs().size() - forExclude.size() < 2) {
                    result.add(new ClusterUpdate(existCluster, ClusterUpdate.ClusterUpdateType.DELETE));
                } else {
                    if(forExclude.contains(existCluster.getClusterOwnerRecord())
                            || !existCluster.getClusterRecordPOs().keySet().contains(existCluster.getClusterOwnerRecord())){
                        existCluster.setClusterOwnerRecord(existCluster.getClusterRecordPOs().keySet()
                                .stream()
                                .filter(s -> !forExclude.contains(s))
                                .findFirst().get());
                    }
                    existCluster.setClusterRecordPOs(Collections.emptyMap());
                    existCluster.setMatchingDate(newClusterPO.getMatchingDate());
                    result.add(new ClusterUpdate(existCluster, forExclude));
                }
            }
        }
        result.add(new ClusterUpdate(newClusterPO, ClusterUpdate.ClusterUpdateType.INSERT));
        return result;
    }

    private String getRuleIdField() {
        return MatchingHeaderField.FIELD_RULE_ID.getField();
    }

    private String getClusterDataField() {
        return MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField();
    }
}
