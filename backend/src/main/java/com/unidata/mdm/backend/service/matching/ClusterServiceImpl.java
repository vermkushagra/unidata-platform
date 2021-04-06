package com.unidata.mdm.backend.service.matching;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;
import com.unidata.mdm.backend.common.context.AggregationRequestContext;
import com.unidata.mdm.backend.common.context.CardinalityAggregationRequestContext;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.FilterAggregationRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.TermsAggregationRequestContext;
import com.unidata.mdm.backend.common.context.ValueCountAggregationRequestContext;
import com.unidata.mdm.backend.common.dto.AggregationResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.ClustersDao;
import com.unidata.mdm.backend.dao.util.ClusterQuery;
import com.unidata.mdm.backend.po.matching.ClusterPO;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.AggregatableNumericAdapter;
import com.unidata.mdm.backend.service.search.util.MatchingHeaderField;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.unidata.mdm.backend.dao.util.ClusterQuery.ClusterQueryBuilder.query;
import static java.util.stream.Collectors.toList;

@Service
public class ClusterServiceImpl implements ClusterService, ConfigurationUpdatesConsumer {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterServiceImpl.class);

    private AtomicInteger searchPageSize = new AtomicInteger(
            (Integer) UnidataConfigurationProperty.UNIDATA_MATCHING_SEARCH_PAGE_SIZE.getDefaultValue().get()
    );

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
     * Haxelcast instance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * Conversion service
     */
    @Autowired
    private ConversionService conversionService;

    @Autowired
    private SearchServiceExt searchService;

    @Autowired
    private MatchingService matchingService;

    @Override
    public void upsertCluster(@Nonnull Cluster cluster) {
        upsertCluster(cluster, false);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upsertCluster(@Nonnull Cluster cluster, boolean useCache) {
        MeasurementPoint.start();
        try {
            filterBlockedRecords(cluster);

            if (cluster.getClusterRecords().size() < 2) {
                LOGGER.debug(
                        "Cluster insertion interrupted because cluster is incorrect, records {} , cluster identifier {}",
                        cluster.getClusterRecords().size(), null);
                return;
            }

            ClusterQuery clusterQuery = query().withRuleId(cluster.getMetaData().getRuleId())
                    .withGroupId(cluster.getMetaData().getGroupId())
                    .withEntityName(cluster.getMetaData().getEntityName(),
                            cluster.getMetaData().getStorage())
                    .withClusterIdentifier(null)
                    .build();
            boolean result = false;
            Collection<ClusterPO> clusters = clustersDao.getClusters(clusterQuery);
            ClusterPO existedClusterPO = clusters.isEmpty() ? null : clusters.iterator().next();
            cluster.setClusterId(existedClusterPO == null ? null : existedClusterPO.getClusterId());
            ClusterPO clusterPO = conversionService.convert(cluster, ClusterPO.class);
            if (existedClusterPO == null) {
                LOGGER.debug("Try to insert new cluster.Cluster size {} where identifier {}",
                        cluster.getClusterRecords().size(), null);
                clustersDao.insertCluster(clusterPO);
                result = true;
            } else {
                Date matchingDate = existedClusterPO.getMatchingDate();
                Date clusterMatchingDate = clusterPO.getMatchingDate();
                //some one already update cluster
                if (clusterMatchingDate.getTime() <= matchingDate.getTime()) {
                    LOGGER.debug("Cluster update was rejected because it is too old. Matching time {}", matchingDate);
                } else {
                    clustersDao.updateCluster(existedClusterPO, clusterPO);
                    result = true;
                }
            }

            if (result && useCache) {
                ISet<String> clusterHashSet = hazelcastInstance.getSet(MatchingConstants.MATCHING_CLUSTER_HASH_SET);
                if (clusterHashSet.size() < MatchingConstants.MATCHING_MAX_SIZE_IN_CLUSTER &&
                        cluster.getClusterRecords().size() >= MatchingConstants.MATCHING_MIN_COUNT_RECORDS_FOR_CACHE) {
                    clusterHashSet.add(null);
                }
            }


        } catch (Exception e) {
            LOGGER.warn("Cluster upsert was reject, because cluster was already created{}", e);
        } finally {
            MeasurementPoint.stop();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void excludeFromCluster(@Nonnull Collection<String> etalonIds, @Nonnull Long clusterId) {
        LOGGER.debug("Exclude from cluster {}. Etalons {}", clusterId, etalonIds);
        clustersDao.removeRecordsFromCluster(etalonIds, clusterId);
        ClusterQuery clusterQuery = query()
                .withClusterId(clusterId)
                .corrupted()
                .build();
        clustersDao.removeClusters(clusterQuery);
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
        ClusterQuery clusterQuery = query()
                .withEntityName(entityName, SecurityUtils.getCurrentUserStorageId())
                .withEtalonIds(Collections.singleton(etalonId))
                .build();
        clustersDao.removeFromBlockList(clusterQuery, clusterMetas);
    }

    @Override
    public void dropFromBlockList(@Nonnull Collection<String> etalonIds) {
        LOGGER.debug("Drop from block lists {}", etalonIds);
        ClusterQuery clusterQuery = query()
                .withEtalonIds(etalonIds)
                .build();
        clustersDao.removeFromBlockList(clusterQuery);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void excludeFromClusters(@Nonnull String entityName, @Nonnull Collection<String> etalonIds) {
        LOGGER.debug("Exclude from clusters {}", etalonIds);
        int countOfRemovedRecords = clustersDao.removeRecordsFromClusters(etalonIds);
        if (countOfRemovedRecords == 0) {
            return;
        }
        ClusterQuery clusterQuery = query()
                .withEntityName(entityName, SecurityUtils.getCurrentUserStorageId())
                .corrupted()
                .build();
        clustersDao.removeClusters(clusterQuery);
    }

    @Nonnull
    @Override
    public Collection<Long> getClusterIds(@Nonnull ClusterMetaData clusterMetaData) {
        ClusterQuery clusterQuery = query()
                .withRuleId(clusterMetaData.getRuleId())
                .withGroupId(clusterMetaData.getGroupId())
                .withEntityName(clusterMetaData.getEntityName(), clusterMetaData.getStorage())
                .ordered()
                .build();
        return clustersDao.getClusterIds(clusterQuery);
    }

    @Nullable
    @Override
    public Cluster getCluster(@Nonnull Long clusterId) {
        ClusterQuery clusterQuery = query().withClusterId(clusterId).build();
        Collection<Cluster> result = clustersDao.getClusters(clusterQuery).stream()
                .map(cluster -> conversionService.convert(cluster, Cluster.class))
                .collect(Collectors.toList());
        return result.size() == 1 ? result.iterator().next() : null;
    }

    @Nonnull
    @Override
    public Collection<Cluster> getClusters(@Nonnull ClusterMetaData clusterMetaData, int limit, int offset, Integer shardNumber) {

        Date matchingDate = new Date();

        FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();
        if (Objects.nonNull(clusterMetaData.getGroupId())) {
            groupAndRule.addFormField(
                    FormField.strictString(getGroupIdField(), clusterMetaData.getGroupId()));
        }

        if (Objects.nonNull(clusterMetaData.getRuleId())) {
            groupAndRule.addFormField(
                    FormField.strictString(getRuleIdField(), clusterMetaData.getRuleId()));
        }

        groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, matchingDate));
        groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), matchingDate, null));

        SearchRequestContext mainContext = SearchRequestContext.builder(EntitySearchType.MATCHING_HEAD, clusterMetaData.getEntityName())
                .count(limit)
                .page(limit == 0 ? offset : offset / limit)
                .shardNumber(shardNumber)
                .onlyQuery(true)
                .fetchAll(true)
                .innerHits(ImmutablePair.of("etalons",
                        Arrays.asList(MatchingHeaderField.FIELD_ETALON_ID.getField(),
                                MatchingHeaderField.FIELD_GROUP_ID.getField(),
                                MatchingHeaderField.FIELD_RULE_ID.getField())))
                .build();

        SearchRequestContext childContext = SearchRequestContext.builder(EntitySearchType.MATCHING, clusterMetaData.getEntityName())
                .form(groupAndRule)
                .count(maxClusterSize.get())
                .onlyQuery(true)
                .build();

        ComplexSearchRequestContext ctx = ComplexSearchRequestContext.hierarchical(mainContext, childContext);
        ctx.setMinChildCount(2);


        SearchResultDTO searchResult = searchService.search(ctx).get(mainContext);
        List<Cluster> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(searchResult.getHits())){
            for(SearchResultHitDTO clusterHit : searchResult.getHits()){
                if(clusterHit.getInnerHits() != null && clusterHit.getInnerHits().size() > 1){
                    Cluster newCluster = new Cluster(matchingDate);
                    ClusterMetaData.ClusterMetaDataBuilder newMetaDataBuilder = ClusterMetaData.builder();
                    newMetaDataBuilder.entityName(clusterMetaData.getEntityName());
                    newMetaDataBuilder.storage(clusterMetaData.getStorage());
                    newMetaDataBuilder.ruleId((Integer) clusterHit.getInnerHits().get(0).getFieldValue(MatchingHeaderField.FIELD_RULE_ID.getField()).getFirstValue());
                    newMetaDataBuilder.groupId((Integer) clusterHit.getInnerHits().get(0).getFieldValue(MatchingHeaderField.FIELD_GROUP_ID.getField()).getFirstValue());
                    newCluster.setMetaData(newMetaDataBuilder.build());

                    for(SearchResultHitDTO clusterRecordHit : clusterHit.getInnerHits()){
                        newCluster.addRecordToCluster(new ClusterRecord(
                                clusterRecordHit.getFieldValue(MatchingHeaderField.FIELD_ETALON_ID.getField()).getFirstValue().toString()
                                , matchingDate, 100));
                        if(newCluster.getClusterRecords().size() == maxClusterSize.get()){
                            break;
                        }
                    }
                    result.add(newCluster);
                }

            }
        }
        return result;
    }

    @Nonnull
    @Override
    public Collection<Cluster> getClusters(@Nonnull String etalonId, int limit, int offset) {
        Date matchingDate = new Date();

        RecordKeys keys = commonComponent.identify(EtalonKey.builder()
                .id(etalonId)
                .build());

        if (Objects.isNull(keys)) {
            return Collections.emptyList();
        }

        Collection<Cluster> clusters = matchingService.construct(etalonId, matchingDate);

        return getClusters(matchingDate, keys.getEntityName(), etalonId, clusters, false);
    }

    @Nonnull
    @Override
    public Collection<Cluster> getClusters(@Nonnull EtalonRecord etalonRecord, Integer groupId, Integer ruleId, boolean virtual) {
        Date matchingDate = new Date();
        Collection<Cluster> clusters = matchingService.construct(etalonRecord, matchingDate, groupId, ruleId);

        return getClusters(matchingDate, etalonRecord.getInfoSection().getEntityName(),
                etalonRecord.getInfoSection().getEtalonKey().getId(), clusters, virtual);
    }


    @Nonnull
    @Override
    public Long getClustersCount(@Nonnull ClusterMetaData clusterMetaData) {

        Date matchingDate = new Date();

        FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();
        if (Objects.nonNull(clusterMetaData.getGroupId())) {
            groupAndRule.addFormField(
                    FormField.strictString(getGroupIdField(), clusterMetaData.getGroupId()));
        }

        if (Objects.nonNull(clusterMetaData.getRuleId())) {
            groupAndRule.addFormField(
                    FormField.strictString(getRuleIdField(), clusterMetaData.getRuleId()));
        }

        groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, matchingDate));
        groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), matchingDate, null));

        SearchRequestContext mainContext = SearchRequestContext.builder(EntitySearchType.MATCHING_HEAD, clusterMetaData.getEntityName())
                .count(0)
                .page(0)
                .onlyQuery(true)
                .fetchAll(true)
                .build();

        SearchRequestContext childContext = SearchRequestContext.builder(EntitySearchType.MATCHING, clusterMetaData.getEntityName())
                .form(groupAndRule)
                .count(maxClusterSize.get())
                .onlyQuery(true)
                .build();

        ComplexSearchRequestContext ctx = ComplexSearchRequestContext.hierarchical(mainContext, childContext);
        ctx.setMinChildCount(2);


        SearchResultDTO searchResult = searchService.search(ctx).get(mainContext);
        return searchResult.getTotalCount();
    }

    @Nonnull
    @Override
    public Long getClustersCount(@Nonnull String etalonId) {
        return (long) getClusters(etalonId, 0, searchPageSize.get()).size();
    }


    private Collection<Cluster> getClusters(Date fromDate, Date toDate, String entityName, String etalonId, Collection<Cluster> clusters) {
        Map<Cluster, SearchRequestContext> searchByGroups = new HashMap<>();
        List<Cluster> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(clusters)) {
            clusters.forEach(cluster -> {
                FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();
                groupAndRule.addFormField(
                        FormField.strictString(getGroupIdField(), cluster.getMetaData().getGroupId()));

                groupAndRule.addFormField(
                        FormField.strictString(getRuleIdField(), cluster.getMetaData().getRuleId()));

                groupAndRule.addFormField(
                        FormField.strictString(getClusterDataField(),
                                String.join(SearchUtils.PIPE_SEPARATOR, cluster.getData().stream()
                                        .map(Object::toString)
                                        .collect(Collectors.toList()))));

                groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, toDate));
                groupAndRule.addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), fromDate, null));

                searchByGroups.put(cluster, SearchRequestContext.forEtalon(
                        EntitySearchType.MATCHING, entityName)
                        .totalCount(true)
                        .onlyQuery(true)
                        .skipEtalonId(true)
                        .form(Collections.singletonList(groupAndRule))
                        .returnFields(Arrays.asList(MatchingHeaderField.FIELD_ETALON_ID.getField(),
                                MatchingHeaderField.FIELD_FROM.getField()))
                        .page(0)
                        .count(maxClusterSize.get())
                        .build());
            });

            Date matchingDate = new Date();
            Map<SearchRequestContext, SearchResultDTO> searchResult = searchService.search(ComplexSearchRequestContext.multi(searchByGroups.values()));

            searchByGroups.forEach((cluster, searchRequestContext) -> {
                SearchResultDTO foundedClusters = searchResult.get(searchRequestContext);
                if (foundedClusters.getTotalCount() > 1) {
                    ClusterMetaData metaData = ClusterMetaData.builder()
                            .entityName(entityName)
                            .groupId(cluster.getMetaData().getGroupId())
                            .ruleId(cluster.getMetaData().getRuleId())
                            .build();
                    Cluster newCluster = new Cluster(matchingDate);
                    newCluster.setMetaData(metaData);
                    if (CollectionUtils.isNotEmpty(foundedClusters.getHits())) {
                        for (SearchResultHitDTO hit : foundedClusters.getHits()) {
                            newCluster.addRecordToCluster(
                                    new ClusterRecord(
                                            hit.getFieldValue(MatchingHeaderField.FIELD_ETALON_ID.getField()).getFirstValue().toString(),
                                            SearchUtils.parseFromIndex(hit.getFieldValue(MatchingHeaderField.FIELD_FROM.getField()).getFirstValue()),
                                            100));
                        }
                    }
                    // if contains not only master cluster etalon record
                    if (newCluster.getClusterRecords().stream().anyMatch(clusterRecord -> !clusterRecord.getEtalonId().equals(etalonId))) {
                        result.add(newCluster);
                    }
                }
            });
        }

        return result;
    }


    private Collection<Cluster> getClusters(Date matchingDate, String entityName, String etalonId, Collection<Cluster> clusters, boolean virtual) {
        Map<Cluster, SearchRequestContext> searchByGroups = new HashMap<>();
        List<Cluster> result = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(clusters)) {
            for (Cluster cluster : clusters) {

                FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();
                groupAndRule.addFormField(
                        FormField.strictString(getGroupIdField(), cluster.getMetaData().getGroupId()));

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
                    if (result.size() >= searchPageSize.get()) {
                        break;
                    }
                    ClusterMetaData metaData = ClusterMetaData.builder()
                            .entityName(entityName)
                            .groupId(cluster.getMetaData().getGroupId())
                            .ruleId(cluster.getMetaData().getRuleId())
                            .build();
                    Cluster newCluster = new Cluster(matchingDate);
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
    public Long getUniqueEtalonsCount(@Nonnull ClusterMetaData clusterMetaData) {

        Long result = 0L;
        List<AggregationRequestContext> aggregations = new ArrayList<>();
        aggregations.add(ValueCountAggregationRequestContext.builder()
                .entity(clusterMetaData.getEntityName())
                .name("total_exact_data")
                .path(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField())
                .build());

        aggregations.add(CardinalityAggregationRequestContext.builder()
                .entity(clusterMetaData.getEntityName())
                .name("unique_exact_data")
                .path(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField())
                .build());

        SearchRequestContext searchContext = SearchRequestContext.forEtalon(EntitySearchType.MATCHING, clusterMetaData.getEntityName())
                .countOnly(false)
                .totalCount(true)
                .onlyQuery(true)
                .fetchAll(true)
                .aggregations(aggregations)
                .build();

        SearchResultDTO searchResultDTO = searchService.search(searchContext);
        if (CollectionUtils.isNotEmpty(searchResultDTO.getAggregates())) {
            AggregationResultDTO totalAggregation = searchResultDTO.getAggregates()
                    .stream()
                    .filter(aggr -> "total_exact_data".equals(aggr.getAggregationName()))
                    .findFirst()
                    .orElse(null);

            AggregationResultDTO uniqueAggregation = searchResultDTO.getAggregates()
                    .stream()
                    .filter(aggr -> "unique_exact_data".equals(aggr.getAggregationName()))
                    .findFirst()
                    .orElse(null);

            if (totalAggregation != null) {
                result = uniqueAggregation != null
                        ? totalAggregation.getCountMap().get(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField()) - uniqueAggregation.getCountMap().get(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField())
                        : totalAggregation.getCountMap().get(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField());
            }
        }

        return result;
    }

    @Override
    public void removeAllClusters(@Nonnull ClusterMetaData cmd) {
        LOGGER.debug("Drop clusters  {}", cmd);
        FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();
        groupAndRule.addFormField(
                FormField.strictString(MatchingHeaderField.FIELD_GROUP_ID.getField(), cmd.getGroupId()));
        groupAndRule.addFormField(
                FormField.strictString(MatchingHeaderField.FIELD_RULE_ID.getField(), cmd.getRuleId()));

        SearchRequestContext matchingContext = SearchRequestContext.forEtalon(
                EntitySearchType.MATCHING, cmd.getEntityName())
                .totalCount(true)
                .onlyQuery(true)
                .skipEtalonId(true)
                .form(Collections.singletonList(groupAndRule))
                .returnFields(Collections.singletonList(MatchingHeaderField.FIELD_ETALON_ID.getField()))
                .build();
        searchService.deleteFoundResult(matchingContext);
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
    public void dropEveryThingForGroup(@Nonnull String entityName, @Nonnull Integer groupId) {
        FormFieldsGroup groupAndRule = FormFieldsGroup.createAndGroup();
        groupAndRule.addFormField(
                FormField.strictString(MatchingHeaderField.FIELD_GROUP_ID.getField(), groupId));

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

        Date forDate = etalonRecord.getInfoSection().getValidFrom() != null
                ? etalonRecord.getInfoSection().getValidFrom()
                : new Date();

        Collection<Cluster> autoMergeClusters = matchingService.constructAutoMerge(etalonRecord, forDate);
        if (CollectionUtils.isNotEmpty(autoMergeClusters)) {
            Collection<Cluster> clustersForMerging = getClusters(etalonRecord.getInfoSection().getValidFrom(),
                    etalonRecord.getInfoSection().getValidTo(),
                    etalonRecord.getInfoSection().getEntityName(),
                    etalonRecord.getInfoSection().getEtalonKey().getId(),
                    autoMergeClusters);

            clustersForMerging.forEach(cluster ->
                    cluster.getClusterRecords().forEach(clusterRecord -> {
                        result.put(clusterRecord.getEtalonId(), clusterRecord.getMatchingDate());
                    }));
        }

        return result;
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String maxClusterSizeKey = UnidataConfigurationProperty.UNIDATA_MATCHING_MAX_CLUSTER_SIZE.getKey();
        final String searchPageSizeKey = UnidataConfigurationProperty.UNIDATA_MATCHING_SEARCH_PAGE_SIZE.getKey();
        updates
                .filter(values ->
                        values.containsKey(maxClusterSizeKey) && values.get(maxClusterSizeKey).isPresent()
                )
                .map(values -> (Integer) values.get(maxClusterSizeKey).get())
                .subscribe(maxClusterSize::set);

        updates
                .filter(values ->
                        values.containsKey(searchPageSizeKey) && values.get(searchPageSizeKey).isPresent()
                )
                .map(values -> (Integer) values.get(searchPageSizeKey).get())
                .subscribe(searchPageSize::set);
    }

    /*Filter record from cluster which in block list*/
    //todo replace to cluster!(and made block list part of cluster)
    private void filterBlockedRecords(@Nonnull Cluster cluster) {
        ClusterQuery clusterQuery = query()
                .withRuleId(cluster.getMetaData().getRuleId())
                .withGroupId(cluster.getMetaData().getGroupId())
                .withEntityName(cluster.getMetaData().getEntityName(), cluster.getMetaData().getStorage())
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
    }

    private String getGroupIdField() {
        return MatchingHeaderField.FIELD_GROUP_ID.getField();
    }

    private String getRuleIdField() {
        return MatchingHeaderField.FIELD_RULE_ID.getField();
    }

    private String getClusterDataField() {
        return MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField();
    }
}
