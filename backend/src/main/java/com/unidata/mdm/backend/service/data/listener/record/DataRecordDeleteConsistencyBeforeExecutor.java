package com.unidata.mdm.backend.service.data.listener.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.TermsAggregationRequestContext;
import com.unidata.mdm.backend.common.dto.GetRelationsDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.exception.ConsistencyException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import static com.unidata.mdm.backend.common.search.FormField.FormType.POSITIVE;
import static com.unidata.mdm.backend.common.search.FormField.strictValue;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.*;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.ES_MAX_DATE;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.ES_MIN_DATE;
import static com.unidata.mdm.meta.SimpleDataType.BOOLEAN;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DataRecordDeleteConsistencyBeforeExecutor implements DataRecordBeforeExecutor<DeleteRequestContext> {

    /**
     * Aggregation name
     */
    private static final String AGGR_NAME = "AGGR_NAME";

    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * MetaModel service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Relations component.
     */
    @Autowired
    private RelationsServiceComponent relationsComponent;
    /**
     * Etalon data component.
     */
    @Autowired
    private EtalonRecordsComponent etalonComponent;

    @Override
    public boolean execute(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);

        // workflow action we skip
        if (keys.isPending() || ctx.isWorkflowAction()) {
            return true;
        }

        //we process only etalon removing and etalon period removing!
        if (!(ctx.isInactivateEtalon() || ctx.isInactivatePeriod() || ctx.isWipe())) {
            return true;
        }

        String entityName = keys.getEntityName();
        String key = keys.getEtalonKey().getId();
        if (!ctx.isInactivatePeriod() && metaModelService.isEntity(entityName)) {
            List<String> relationNames = metaModelService.getRelationsByToEntityName(keys.getEntityName()).stream()
                    .filter(r -> r.getRelType() != RelType.CONTAINS)
                    .map(RelationDef::getName)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(relationNames)) {

                final GetRelationsRequestContext relCtx = GetRelationsRequestContext.builder()
                        .etalonKey(keys.getEtalonKey().getId())
                        .relationNames(relationNames)
                        .build();

                final GetRelationsDTO relationsDTO = relationsComponent.getAllRelations(relCtx);

                final Map<String, Long> entities = relationsDTO.getRelations().values().stream()
                        .flatMap(list -> list.stream()
                                .filter(r -> r.getRelationKeys().getEtalonStatus() == RecordStatus.ACTIVE)
                                .map(r -> r.getRelationKeys().getFrom()))
                        .filter(r -> r.getEtalonStatus() == RecordStatus.ACTIVE)
                        .map(e -> metaModelService.getEntityByIdNoDeps(e.getEntityName()).getDisplayName())
                        .reduce(
                                new HashMap<String, Long>(),
                                (data, name) -> {
                                    if (!data.containsKey(name)) {
                                        data.put(name, 0L);
                                    }
                                    data.put(name, 1L + data.get(name));
                                    return data;
                                },
                                (m1, m2) -> {
                                    m2.forEach((k, v) -> m2.merge(k, v, (v1, v2) -> v1 + v2));
                                    return m2;
                                }
                        );

                if (!entities.isEmpty()) {
                    //throw another kind of exception!
                    throw new ConsistencyException(
                            "The etalon record has relations to own self",
                            ExceptionId.EX_DATA_ETALON_CONTAINS_RELATIONS_TO_YOURSELF,
                            entities
                    );
                }
            }
        }

        if (metaModelService.isLookupEntity(entityName)) {
            LookupEntityDef lookupEntityDef = metaModelService.getLookupEntityById(entityName);
            String codeAttrName = lookupEntityDef.getCodeAttribute().getName();
            DataRecord record = getEtalonRecord(ctx, null);

            // try get etalon by validity period
            if (record == null) {
                record = getOriginFromFirstPeriod(ctx, key);
            }
            // can't get etalon by keys
            if (record == null) {
                return false;
            }

            FormFieldsGroup timeLineFormFields = timeLineFormField(ctx);

            //always one code attr!
            CodeAttribute<?> codeAttribute = record.getCodeAttribute(codeAttrName);
            Map<String, Long> linkedRecords = new HashMap<>();

            // 1. Check Entity -> Lookup
            Map<EntityDef, Set<AttributeInfoHolder>> entities = metaModelService.getEntitiesReferencingThisLookup(entityName);
            entities.entrySet()
                    .stream()
                    .map(e -> findLinks(e, codeAttribute, timeLineFormFields))
                    .filter(Objects::nonNull)
                    .forEach(pair -> linkedRecords.put(pair.getKey(), pair.getValue()));

            // 2. Check Lookup -> Lookup
            Map<LookupEntityDef, Set<AttributeInfoHolder>> lookupEntities = metaModelService.getLookupsReferencingThisLookup(entityName);
            lookupEntities.entrySet()
                    .stream()
                    .map(e -> findLinks(e, codeAttribute, timeLineFormFields))
                    .filter(Objects::nonNull)
                    .forEach(pair -> linkedRecords.put(pair.getKey(), pair.getValue()));

            if (!linkedRecords.isEmpty()) {
                throw new ConsistencyException(
                        "The etalon record has links to yourself",
                        ExceptionId.EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF,
                        linkedRecords
                );
            }

        }
        return true;
    }

    private DataRecord getOriginFromFirstPeriod(DeleteRequestContext ctx, String key) {
        TimelineDTO timeline = etalonComponent.loadEtalonTimeline(new GetRequestContext.GetRequestContextBuilder().etalonKey(key).build());
        if (timeline != null && CollectionUtils.isNotEmpty(timeline.getIntervals())) {
            return getEtalonRecord(ctx, timeline.getIntervals().get(0).getValidFrom());
        }
        return null;
    }

    @Nullable
    private Pair<String, Long> findLinks(Map.Entry<? extends AbstractEntityDef, Set<AttributeInfoHolder>> e,
                                         CodeAttribute<?> codeAttribute, FormFieldsGroup timeLineFormFields) {

        String linkedEntityName = e.getKey().getName();
        Set<AttributeInfoHolder> linkedAttrs = e.getValue();

        List<Object> values = new ArrayList<>();
        values.add(codeAttribute.getValue());
        if (codeAttribute.hasSupplementary()) {
            values.addAll(codeAttribute.getSupplementary());
        }
        // todo refactoring this place, after add values in form field.

        List<String> searchFields = linkedAttrs.stream()
                .map(linkedAttr -> isLookupStringAttr(linkedAttr)
                        ? linkedAttr.getPath() + "." + FIELD_NOT_ANALYZED.getField()
                        : linkedAttr.getPath())
                .collect(Collectors.toList());

        //it is an insurance for case when the cache model won't update after clean up.
        if (searchFields.isEmpty()) {
            return null;
        }

        TermsAggregationRequestContext taCtx = TermsAggregationRequestContext.builder()
                .name(AGGR_NAME)
                .path(FIELD_ETALON_ID.getField())
                .size(1)
                .build();

        FormFieldsGroup baseForm = createAndGroup(
                strictValue(BOOLEAN, FIELD_DELETED.getField(), FALSE),
                strictValue(BOOLEAN, FIELD_PUBLISHED.getField(), TRUE)
        );

        SearchRequestContext ctx = SearchRequestContext.forEtalonData(linkedEntityName)
                .search(SearchRequestType.TERM)
                .operator(SearchRequestOperator.OP_OR)
                .onlyQuery(true)
                .totalCount(true)
                .skipEtalonId(true)
                .searchFields(searchFields)
                .values(values)
                .count(0)
                .page(0)
                .form(baseForm, timeLineFormFields)
                .aggregations(Collections.singletonList(taCtx))
                .build();

        SearchResultDTO result = searchService.search(ctx);
        final Long count = result.getTotalCount() - (
                CollectionUtils.isNotEmpty(result.getAggregates()) ?
                        result.getAggregates().stream()
                                .flatMap(a -> a.getCountMap().values().stream().map(v -> v - 1))
                                .mapToLong(v -> v)
                                .sum() :
                        0
        );

        if (count > 0) {
            return Pair.of(e.getKey().getDisplayName(), count);
        } else {
            return null;
        }
    }


    private boolean isLookupStringAttr(AttributeInfoHolder attrHolder) {
        return (attrHolder.isSimple() &&
                ((SimpleAttributeDef) attrHolder.getAttribute()).getLookupEntityCodeAttributeType() == SimpleDataType.STRING)
                || (attrHolder.isArray() &&
                ((ArrayAttributeDef) attrHolder.getAttribute()).getLookupEntityCodeAttributeType() == ArrayValueType.STRING);
    }

    @Nonnull
    private EtalonRecord getEtalonRecord(@Nonnull final DeleteRequestContext ctx, Date asOf) {
        EtalonRecord etalonRecord = ctx.getFromStorage(StorageId.DATA_DELETE_ETALON_RECORD);
        if (etalonRecord == null) {
            RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);
            if (asOf == null) {
                asOf = ctx.getValidFrom() != null ? ctx.getValidFrom() : ctx.getValidTo();
            }
            etalonRecord = etalonComponent.loadEtalonData(keys.getEtalonKey().getId(), asOf, null, null, null, true, false);
        }
        return etalonRecord;
    }


    private FormFieldsGroup timeLineFormField(DeleteRequestContext ctx) {
        if (!ctx.isInactivatePeriod()) {
            return null;
        }
        Date from = ctx.getValidFrom() == null ? ES_MIN_DATE : ctx.getValidFrom();
        Date to = ctx.getValidTo() == null ? ES_MAX_DATE : ctx.getValidTo();
        FormField fromFormField = FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(),
                POSITIVE, null, to);
        FormField toFromField = FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(),
                POSITIVE, from, null);
        return FormFieldsGroup.createAndGroup(fromFormField, toFromField);
    }
}
