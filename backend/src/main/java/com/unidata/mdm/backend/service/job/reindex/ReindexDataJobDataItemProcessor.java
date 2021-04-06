package com.unidata.mdm.backend.service.job.reindex;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext.IndexRequestContextBuilder;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetClassifiersDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterSet;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Denis Kostovarov
 */
@Component("reindexDataJobItemProcessor")
@StepScope
public class ReindexDataJobDataItemProcessor implements ItemProcessor<Pair<Long, String>, IndexRequestContext>, InitializingBean {
    /**
     * Skip data quality
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_SKIP_DQ + "]}")
    private boolean skipDq;
    /**
     * Suppress system checks.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_SUPPRESS_CONSISTENCY_CHECK + "] ?: true }")
    private boolean suppressConsistencyChecks;
    /**
     * Clean types
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_CLEAN_INDEXES + "] ?: false }")
    private boolean indexesAreEmpty;
    /**
     * Job operation id
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_OPERATION_ID + "]}")
    private String operationId;
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private Boolean jobReindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private Boolean jobReindexRelations;
    /**
     * If true, classifiers will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS + "] ?: false}")
    private Boolean jobReindexClassifiers;
    /**
     * If true, matching data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_MATCHING + "] ?: false}")
    private Boolean jobReindexMatching;
    /**
     * Enable jms notifications
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_SKIP_NOTIFICATIONS + "] ?: false}")
    private boolean skipNotifications;
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private Boolean stepReindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private Boolean stepReindexRelations;
    /**
     * If true, classifiers will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS + "] ?: false}")
    private Boolean stepReindexClassifiers;
    /**
     * If true, matching data will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_MATCHING + "] ?: false}")
    private Boolean stepReindexMatching;
    /**
     * Entity name
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_ENTITY_NAME + "]}")
    private String entityName;
    /**
     * Linked with entity relation
     */
    private Map<String, List<RelationDef>> relationNames = Collections.emptyMap();

    /**
     * Linked with entity classifiers
     */
    private Map<String, List<String>> classifierNames = Collections.emptyMap();

    /**
     * Relation service
     */
    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    /**
     * Classifiers component.
     */
    @Autowired
    private ClassifiersServiceComponent classifiersComponent;

    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Common service component
     */
    @Autowired
    private EtalonRecordsComponent etalonRecordsComponent;
    /**
     * Common service component
     */
    @Autowired
    private OriginRecordsComponent originRecordsComponent;

    @Override
    public IndexRequestContext process(Pair<Long, String> row) throws Exception {

        Long gsn = row.getKey();
        String name = row.getValue();

        // record from already removed registry
        if (!metaModelService.isEntity(name) && !metaModelService.isLookupEntity(name)) {
            return null;
        }

        GetRequestContext gCtx = GetRequestContext.builder()
                .gsn(gsn)
                .build();

        WorkflowTimelineDTO timeline = null;
        try {
            timeline = originRecordsComponent.loadWorkflowTimeline(gCtx, true);
        } catch (SystemRuntimeException e) { /* NOP */ }

        if (timeline == null || timeline.getIntervals().isEmpty()) {
            return null;
        }

        RecordKeys keys = gCtx.keys();

        boolean isSoftDeleteReindex = keys.getEtalonStatus() == RecordStatus.INACTIVE;
        boolean isActive = keys.getEtalonStatus() == RecordStatus.ACTIVE;
        if (!isSoftDeleteReindex && !isActive) {
            return null;
        }

        IndexRequestContextBuilder builder = IndexRequestContext.builder()
                .drop(!indexesAreEmpty)
                .entity(keys.getEntityName())
                .routing(keys.getEtalonKey().getId());

        addRelations(keys, builder);
        addRecords(timeline, keys, builder);
        addClassifiers(keys, builder);

        return !builder.hasUpdates() ?
                null :
                builder.build();
    }
    /**
     * Adds records and clusters to the builder.
     * @param timeline the timeline to process
     * @param keys the keys
     * @param builder the builder
     */
    private void addRecords(WorkflowTimelineDTO timeline, RecordKeys keys, IndexRequestContextBuilder builder) {

        if ((!jobReindexRecords && !stepReindexRecords)
         && (!jobReindexMatching && !stepReindexMatching)) {
            return;
        }

        UpsertRequestContext ctx = UpsertRequestContext.builder()
           .entityName(keys.getEntityName())
           .recalculateWholeTimeline(true)
           .restore(true)
           .skipCleanse(skipDq)
           .skipConsistencyChecks(suppressConsistencyChecks)
           .skipMatching(!jobReindexMatching && !stepReindexMatching)
           .bypassExtensionPoints(true)
           .returnEtalon(true)
           .build();

        ctx.setOperationId(operationId);
        if (skipNotifications) {
            ctx.skipNotification();
        } else {
            ctx.setFlag(ContextUtils.CTX_FLAG_SEND_NOTIFICATION);
        }
        ctx.putToStorage(ctx.keysId(), keys);
        ctx.putToStorage(StorageId.DATA_UPSERT_IS_PUBLISHED, timeline.isPublished());
        ctx.putToStorage(StorageId.DATA_RECORD_TIMELINE, timeline);

        List<UpsertRequestContext> periodEtalons = etalonRecordsComponent.calculatePeriods(ctx, timeline.getIntervals());
        Pair<Map<EtalonRecord, Map<? extends SearchField, Object>>, Map<EtalonRecord, ClusterSet>> recordUpdates
            = collectUpdates(periodEtalons);

        builder.records(recordUpdates.getKey());
        builder.clusters(recordUpdates.getValue());
        if (!indexesAreEmpty) {

            SearchRequestContext sCtx = SearchRequestContext.builder(EntitySearchType.ETALON_DATA, keys.getEntityName())
                .form(Arrays.asList(FormFieldsGroup.createAndGroup(
                        FormField.strictString(RecordHeaderField.FIELD_ETALON_ID.getField(), keys.getEtalonKey().getId()))))
                .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                .onlyQuery(true)
                .source(false)
                .returnFields(Collections.emptyList())
                .build();

            SearchResultDTO result = searchService.search(sCtx);
            builder.recordsToDelete(result.getHits().stream()
                .map(SearchResultHitDTO::getId)
                .collect(Collectors.toList()));
        }
    }

    //it is a duplicate code, will be deleted after remove map with search field
    private Pair<
            Map<EtalonRecord, Map<? extends SearchField, Object>>,
            Map<EtalonRecord, ClusterSet>>
        collectUpdates(List<UpsertRequestContext> periods) {

        if (periods.isEmpty()) {
            return new ImmutablePair<>(null, null);
        }

        Map<EtalonRecord, Map<? extends SearchField, Object>> indexUpdate = new HashMap<>(periods.size(), 1);
        Map<EtalonRecord, ClusterSet> matchingUpdates = new HashMap<>(periods.size(), 1);
        for (UpsertRequestContext pCtx : periods) {

            EtalonRecord etalon = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
            if (etalon == null) {
                continue;
            }

            Map<? extends SearchField, Object> fields = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_INDEX_UPDATE);
            if (fields != null) {
                indexUpdate.put(etalon, fields);
            }

            Collection<Cluster> clusters = pCtx.getFromStorage(StorageId.DATA_UPSERT_ETALON_MATCHING_UPDATE);
            if (CollectionUtils.isNotEmpty(clusters)) {
                matchingUpdates.put(etalon, new ClusterSet(clusters,
                        etalon.getInfoSection().getValidFrom(), etalon.getInfoSection().getValidTo()));
            }
        }

        return new ImmutablePair<>(indexUpdate, matchingUpdates);
    }
    /**
     * Gets and adds classifiers updates.
     * @param keys the keys
     * @param builder the builder
     */

    private void addClassifiers(RecordKeys keys, IndexRequestContextBuilder builder) {

        if (!jobReindexClassifiers && !stepReindexClassifiers) {
            return;
        }

        List<String> classifiers = StringUtils.isNotBlank(entityName)
                ? classifierNames.get(entityName)
                : classifierNames.computeIfAbsent(keys.getEntityName(), this::getClassifiersForEntity);

        if (classifiers.isEmpty()) {
            return;
        }

        String etalonId = keys.getEtalonKey().getId();
        GetClassifiersDataRequestContext clsfCtx = GetClassifiersDataRequestContext.builder()
               .etalonKey(etalonId)
               .classifierNames(classifiers)
               .build();

        clsfCtx.setOperationId(operationId);
        clsfCtx.putToStorage(clsfCtx.keysId(), keys);

        GetClassifiersDTO result = classifiersComponent.getClassifiers(clsfCtx);
        List<EtalonClassifier> classifiersData = result.getClassifiers()
                     .values()
                     .stream()
                     .flatMap(Collection::stream)
                     .map(GetClassifierDTO::getEtalon)
                     .filter(Objects::nonNull)
                     .map(cl -> { cl.getInfoSection().withStatus(keys.getEtalonStatus()); return cl; })
                     .collect(Collectors.toList());

        builder.classifiers(classifiersData);
        if (!indexesAreEmpty) {
            builder.classifiersToDelete(classifiers.stream()
                .map(name -> SearchUtils.childPeriodId(keys.getEtalonKey().getId(), name))
                .collect(Collectors.toList()));
        }
    }
    /**
     * Gets and adds to context relation data suitable for reindexing.
     * @param keys the keys.
     * @param builder the builder to fill
     * @return relations data
     */
    private void addRelations(RecordKeys keys, IndexRequestContextBuilder builder) {

        // 1. Skip action, if disabled by the user
        if (!jobReindexRelations && !stepReindexRelations) {
            return;
        }

        // 2. Find out the names
        List<RelationDef> names = StringUtils.isNotBlank(entityName)
                ? relationNames.get(entityName)
                : relationNames.computeIfAbsent(keys.getEntityName(), this::getRelationDefsForEntityName);

        if (CollectionUtils.isEmpty(names)) {
            return;
        }

        // 3. Load current elastic data to collect ids for bulk deletion.
        // Skip this, if the whole index was already cleansed.
        if (!indexesAreEmpty) {

            SearchRequestContext sCtx = SearchRequestContext.builder(EntitySearchType.ETALON_RELATION, keys.getEntityName())
                .form(Arrays.asList(FormFieldsGroup.createAndGroup(
                        FormField.strictString(RelationHeaderField.FIELD_FROM_ETALON_ID.getField(), keys.getEtalonKey().getId()))))
                .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                .onlyQuery(true)
                .source(false)
                .returnFields(Arrays.asList(
                        RelationHeaderField.FIELD_PERIOD_ID.getField(),
                        RelationHeaderField.FIELD_TO_ETALON_ID.getField(),
                        RelationHeaderField.REL_NAME.getField()))
                .build();

            SearchResultDTO result = searchService.search(sCtx);
            result.getHits().stream()
                .forEach(h -> {

                    String periodId = h.getFieldFirstValue(RelationHeaderField.FIELD_PERIOD_ID.getField());
                    String name = h.getFieldFirstValue(RelationHeaderField.REL_NAME.getField());
                    String toEtalonId = h.getFieldFirstValue(RelationHeaderField.FIELD_TO_ETALON_ID.getField());
                    if (Objects.nonNull(periodId) && Objects.nonNull(name) && Objects.nonNull(toEtalonId)) {

                        builder.relationToDelete(keys.getEntityName(),
                                SearchUtils.childPeriodId(periodId, keys.getEtalonKey().getId(), name, toEtalonId));

                        RelationDef rel = metaModelService.getRelationById(name);
                        if (Objects.nonNull(rel)) {
                            builder.relationToDelete(rel.getToEntity(),
                                    SearchUtils.childPeriodId(periodId, toEtalonId, name, keys.getEtalonKey().getId()));
                        }
                    }
                });
        }

        List<EtalonRelation> relations = relationsServiceComponent.loadActiveEtalonsRelationsByFromSideAsList(keys, operationId);
        if (CollectionUtils.isNotEmpty(relations)) {
            builder.relations(relations.stream()
                    .map(rel -> { rel.getInfoSection().withStatus(keys.getEtalonStatus()); return rel; })
                    .collect(Collectors.toList()));
        }
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setJobReindexRecords(Boolean reindexRecords) {
        this.jobReindexRecords = reindexRecords;
    }

    public void setJobReindexRelations(Boolean reindexRelations) {
        this.jobReindexRelations = reindexRelations;
    }

    public void setSkipDq(boolean skipDq) {
        this.skipDq = skipDq;
    }

    private List<RelationDef> getRelationDefsForEntityName(String entityName) {

        if (StringUtils.isBlank(entityName)) {
            return Collections.emptyList();
        }

        return metaModelService.getRelationsByFromEntityName(entityName)
                               .stream()
                               .collect(Collectors.toList());
    }

    private List<String> getClassifiersForEntity(String entityName) {
        if (StringUtils.isBlank(entityName)) {
            return Collections.emptyList();
        }
        return metaModelService.getClassifiersForEntity(entityName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.isBlank(entityName)) {
            relationNames = new HashMap<>();
            classifierNames = new HashMap<>();
        } else {
            List<RelationDef> rels = getRelationDefsForEntityName(entityName);
            List<String> classifiers = getClassifiersForEntity(entityName);
            relationNames = Collections.singletonMap(entityName, rels);
            classifierNames = Collections.singletonMap(entityName, classifiers);
        }
    }
}
