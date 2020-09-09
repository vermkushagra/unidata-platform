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

package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.common.search.FormField.strictValue;
import static com.unidata.mdm.backend.common.search.FormField.FormType.POSITIVE;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createOrGroup;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_DELETED;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_PUBLISHED;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.ES_MAX_DATE;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.ES_MIN_DATE;
import static com.unidata.mdm.meta.SimpleDataType.BOOLEAN;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.types.RelationType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.TermsAggregationRequestContext;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.dto.GetRelationsDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.exception.ConsistencyException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

public class DataRecordDeleteConsistencyBeforeExecutor
        implements DataRecordBeforeExecutor<DeleteRequestContext>, AbstractDataRecordDeleteCommonExecutor<DeleteRequestContext> {
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

    @Override
    public boolean execute(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.keys();

        // workflow action we skip
        if (keys.isPending() || ctx.isWorkflowAction()) {
            return true;
        }

        //we process only etalon removing and etalon period removing!
        if (!(ctx.isInactivateEtalon() || ctx.isInactivatePeriod() || ctx.isWipe())) {
            return true;
        }

        String entityName = keys.getEntityName();
        if (!ctx.isInactivatePeriod() && metaModelService.isEntity(entityName)) {
            final List<RelationDef> relationsToEntity = metaModelService.getRelationsByToEntityName(keys.getEntityName());

            List<String> relationNames = (ctx.isWipe() ?
                    relationsToEntity.stream().filter(r -> r.getRelType() != RelType.CONTAINS) :
                    relationsToEntity.stream()
            )
                    .map(RelationDef::getName)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(relationNames)) {

                final GetRelationsRequestContext relCtx = GetRelationsRequestContext.builder()
                        .etalonKey(keys.getEtalonKey().getId())
                        .relationNames(relationNames)
                        .build();

                final GetRelationsDTO relationsDTO = relationsComponent.getAllRelations(relCtx);

                final String relationIdDeleteStarted = ctx.getFromStorage(StorageId.DELETE_BY_RELATION);

                final Map<String, Long> entities = relationsDTO.getRelations().values().stream()
                        .flatMap(list -> {
                            Stream<GetRelationDTO> relationDTOStream = list.stream()
                                    .filter(r -> r.getRelationKeys().getEtalonStatus() == RecordStatus.ACTIVE);
                            if (relationIdDeleteStarted != null) {
                                relationDTOStream = relationDTOStream
                                        .filter(r -> !relationIdDeleteStarted.equals(r.getRelationKeys().getEtalonId()));
                            }
                            return relationDTOStream.map(r -> r.getRelationKeys().getFrom());
                        })
                        .filter(r -> r.getEtalonStatus() == RecordStatus.ACTIVE)
                        .map(e -> metaModelService.getEntityByIdNoDeps(e.getEntityName()).getDisplayName())
                        .reduce(
                                new HashMap<>(),
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
            DataRecord record = getCurrentEtalonRecord(ctx);

            // try get etalon by validity period
            if(record == null){
                record = getFirstPeriodEtalonRecord(ctx);
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
                        "The etalon record has references to it.",
                        ExceptionId.EX_DATA_ETALON_CONTAINS_LINKS_TO_YOURSELF_WITH_PERIODS,
                        linkedRecords
                );
            }

        }
        return true;
    }

    private boolean isLookupStringAttr(AttributeInfoHolder attrHolder) {
        return (attrHolder.isSimple() &&
                ((SimpleAttributeDef) attrHolder.getAttribute()).getLookupEntityCodeAttributeType() == SimpleDataType.STRING)
                || (attrHolder.isArray() &&
                ((ArrayAttributeDef) attrHolder.getAttribute()).getLookupEntityCodeAttributeType() == ArrayValueType.STRING);
    }

    @Nullable
    private Pair<String, Long> findLinks(Map.Entry<? extends AbstractEntityDef, Set<AttributeInfoHolder>> e,
                                         CodeAttribute<?> codeAttribute, FormFieldsGroup timeLineFormFields) {

        String linkedEntityName = e.getKey().getName();
        Set<AttributeInfoHolder> linkedAttrs = e.getValue();

        if (linkedAttrs.isEmpty()) {
            return null;
        }

        List<Object> values = new ArrayList<>();
        values.add(codeAttribute.getValue());
        if (codeAttribute.hasSupplementary()) {
            values.addAll(codeAttribute.getSupplementary());
        }

        FormFieldsGroup baseForm = createAndGroup(
                strictValue(BOOLEAN, FIELD_DELETED.getField(), FALSE),
                strictValue(BOOLEAN, FIELD_PUBLISHED.getField(), TRUE)
        );

        FormFieldsGroup linkedValuesForm = createOrGroup();
        linkedAttrs.forEach(linkedAttr -> linkedValuesForm.addFormField(FormField.strictValues(
                isLookupStringAttr(linkedAttr) ? SimpleDataType.STRING : SimpleDataType.INTEGER,
                linkedAttr.getPath(),
                values
        )));
        baseForm.addChildGroup(linkedValuesForm);

        TermsAggregationRequestContext taCtx = TermsAggregationRequestContext.builder()
                .name(AGGR_NAME)
                .path(FIELD_ETALON_ID.getField())
                .size(1)
                .build();

        SearchRequestContext ctx = SearchRequestContext.forEtalonData(linkedEntityName)
                .operator(SearchRequestOperator.OP_OR)
                .onlyQuery(true)
                .totalCount(true)
                .skipEtalonId(true)
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

    private DataRecord getFirstPeriodEtalonRecord(DeleteRequestContext ctx) {

        Map<TimeIntervalDTO, Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>>> data
                = ctx.getFromStorage(StorageId.DATA_INTERVALS_BEFORE);
        WorkflowTimelineDTO timeline
                = ctx.getFromStorage(StorageId.DATA_TIMELINE_BEFORE);

        if (MapUtils.isNotEmpty(data) && !timeline.getIntervals().isEmpty()) {
            for (TimeIntervalDTO interval : timeline.getIntervals()) {
                Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> entry = data.get(interval);
                if (Objects.nonNull(entry) && Objects.nonNull(entry.getKey())) {
                    return entry.getKey();
                }
            }
        }

        return null;
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
