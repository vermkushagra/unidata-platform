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

package com.unidata.mdm.backend.service.data;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_ATTRIBUTE_MEASURED_UNIT_NOT_PRESENT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_ATTRIBUTE_MEASURED_VALUE_NOT_PRESENT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_ENTITY_NOT_FOUND_BY_NAME;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_ENUM_ATTRIBUTE_INCORRECT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_IN_RANGE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_LOWER_BOUND;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_LARGE_OBJECT_VALUE_UNAVAILABLE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_MISSING_ATTRIBUTE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_NO_ID;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_RELS_INCORRECT_TO_SIDE_PERIOD;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_REQUIRED_ATTRS_IS_NOT_PRESENTED;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_REQUIRED_RELS_INCORRECT_TO_SIDE_PERIOD;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_REQUIRED_RELS_IS_NOT_PRESENTED;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_WRONG_SPECIAL_ATTRIBUTE_TYPE;
import static com.unidata.mdm.meta.SimpleDataType.BOOLEAN;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.meta.RelType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.context.FetchLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.ContributorDTO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.fields.RelationHeaderField;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayValue;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractLargeValue;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.service.data.binary.LargeObjectsServiceComponent;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.AttributesWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.RelationWrapper;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.EnumerationValue;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * The Class ValidationService.
 *
 * @author Ruslan Trachuk
 */
@Service
public class ValidationServiceImpl implements ValidationServiceExt {

    private static final String INFINITY_SYMBOL = Character.toString('\u221E');

    /**
     * Logger.
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationServiceImpl.class);
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
     * DataRecord service.
     */
    @Autowired
    private DataRecordsService dataRecordsService;
    /**
     * Measured service
     */
    @Autowired
    private MetaMeasurementService measurementService;

    /**
     * LOB component.
     */
    @Autowired
    private LargeObjectsServiceComponent lobComponent;

    @Nullable
    @Override
    public Multimap<AttributeInfoHolder, Object> getMissedLinkedLookupEntities(String etalonId, Date asOf) {

        GetRequestContext ctx = new GetRequestContext.GetRequestContextBuilder()
                .etalonKey(etalonId)
                .forDate(asOf)
                .fetchRelations(false)
                .build();

        GetRecordDTO record = dataRecordsService.getRecord(ctx);
        if (record.getEtalon() == null) {
            return null;
        }

        Map<LookupEntityDef, Set<AttributeInfoHolder>> toLookups = Collections.emptyMap();
        String entityName = record.getEtalon().getInfoSection().getEntityName();
        if (metaModelService.isLookupEntity(entityName)) {
            toLookups = metaModelService.getLookupEntityToLinkedLookups(entityName);
        } else if (metaModelService.isEntity(entityName)) {
            toLookups = metaModelService.getEntityToLinkedLookups(entityName);
        }

        if (toLookups.isEmpty()) {
            return null;
        }

        Multimap<AttributeInfoHolder, Object> result = HashMultimap.create();
        //todo rewrite! (complex search request + reduce number of loops)
        for (Map.Entry<LookupEntityDef, Set<AttributeInfoHolder>> entry : toLookups.entrySet()) {
            for (AttributeInfoHolder attributeHolder : entry.getValue()) {
                Collection<Attribute> attrs = record.getEtalon().getAttributeRecursive(attributeHolder.getPath());
                for (Attribute attribute : attrs) {
                    switch (attribute.getAttributeType()) {
                        case SIMPLE:
                            SimpleAttribute<?> simpleAttribute = (SimpleAttribute<?>) attribute;
                            Object ref = simpleAttribute.getValue();
                            if (ref == null) {
                                break;
                            }
                            SearchRequestContext requestContext = getLookupEntityValueSearchRequest(entry.getKey(), ref);
                            if (searchService.search(requestContext).getTotalCount() == 0) {
                                result.put(attributeHolder, ref);
                            }
                            break;
                        case ARRAY:
                            ArrayAttribute<?> arrayAttribute = (ArrayAttribute<?>) attribute;
                            for (Object arrayRef : arrayAttribute) {
                                ArrayValue<?> arrayValue = (ArrayValue<?>) arrayRef;
                                if (arrayValue.getValue() == null) {
                                    continue;
                                }
                                SearchRequestContext requestCtx = getLookupEntityValueSearchRequest(entry.getKey(),
                                        arrayValue.getValue());
                                if (searchService.search(requestCtx).getTotalCount() == 0) {
                                    result.put(attributeHolder, arrayValue.getValue());
                                }
                            }
                        default:
                            break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Search the specified lookup entities by code attribute value.
     *
     * @param lookupEntityDef lookup entity definition
     * @param codeAttrValue code attribute value
     * @return search request
     */
    private SearchRequestContext getLookupEntityValueSearchRequest(LookupEntityDef lookupEntityDef, Object codeAttrValue) {
        SimpleDataType simpleDataType = lookupEntityDef.getCodeAttribute().getSimpleDataType();
        String codeAttrName = lookupEntityDef.getCodeAttribute().getName();
        FormField codeAttrForm = FormField.strictValue(simpleDataType, codeAttrName, codeAttrValue);
        FormField deletedForm = FormField.strictValue(BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), false);
        FormField published = FormField.strictValue(BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), true);
        FormFieldsGroup groupFormFields = FormFieldsGroup.createAndGroup(codeAttrForm, deletedForm, published);
        return forEtalonData(lookupEntityDef.getName()).returnFields(Collections.singletonList(codeAttrName))
                .operator(SearchRequestOperator.OP_AND)
                .totalCount(true)
                .countOnly(true)
                .onlyQuery(true)
                .count(1)
                .page(0)
                .form(groupFormFields)
                .build();
    }


    @Override
    public void checkEntityDataRecord(DataRecord record, String id) {
        checkDataRecord(record, id, EntityWrapper.class);
    }

    @Override
    public void checkLookupDataRecord(DataRecord record, String id) {
        checkDataRecord(record, id, LookupEntityWrapper.class);
    }

    @Override
    public void checkRelationDataRecord(DataRecord record, String id) {
        checkDataRecord(record, id, RelationWrapper.class);
    }

    private <T extends AttributesWrapper> void checkDataRecord(DataRecord record, String id, Class<T> cachedType) {
        if (id == null) {
            final String message = "Invalid upsert request context. No entity name was supplied. Upsert rejected.";
            LOGGER.warn(message);
            throw new DataProcessingException(message, EX_DATA_UPSERT_NO_ID);
        }
        AttributesWrapper attributesWrapper = metaModelService.getValueById(id, cachedType);

        if (attributesWrapper == null) {
            final String message = "Invalid upsert request context. Entity was not found by name. Upsert rejected.";
            LOGGER.warn(message, id);
            throw new DataProcessingException(message, EX_DATA_UPSERT_ENTITY_NOT_FOUND_BY_NAME, id);
        }

        checkAttributes(record, attributesWrapper, StringUtils.EMPTY, 0);
    }

    @Override
    public void checkRelations(UpsertRequestContext ctx) {
        if (ctx.isSkipConsistencyChecks()) {
            return;
        }

        List<RelationDef> relsFromDef = metaModelService.getRelationsByFromEntityName(ctx.keys().getEntityName());
        boolean relationsChanged = (ctx.getRelations() != null && MapUtils.isNotEmpty(ctx.getRelations().getRelations()))
                || (ctx.getRelationDeletes() != null && MapUtils.isNotEmpty(ctx.getRelationDeletes().getRelations()));
        boolean forceCheck = true;
        WorkflowTimelineDTO previousTimeline = ctx.getFromStorage(StorageId.DATA_UPSERT_PREVIOUS_TIMELINE);
        if (previousTimeline!= null && CollectionUtils.isNotEmpty(previousTimeline.getIntervals())) {
            List<Range<Date>> periods = previousTimeline.getIntervals().stream()
                    .map(timeIntervalDTO -> Range.between(
                            timeIntervalDTO.getValidFrom() == null
                                    ? SearchUtils.ES_MIN_DATE
                                    : timeIntervalDTO.getValidFrom(),
                            timeIntervalDTO.getValidTo() == null
                                    ? SearchUtils.ES_MAX_DATE
                                    : timeIntervalDTO.getValidTo()))
                    .collect(Collectors.toList());
            long recordFrom = ctx.getValidFrom() == null ? SearchUtils.ES_MIN_DATE.getTime() : ctx.getValidFrom().getTime();
            long recordTo = ctx.getValidTo() == null ? SearchUtils.ES_MAX_DATE.getTime() : ctx.getValidTo().getTime();
            forceCheck = !isCovered(recordFrom, recordTo, periods);
        }

        forceCheck = forceCheck || isMetaInfoForRelationsWasDeprecated(previousTimeline, relsFromDef);

        if (forceCheck || relationsChanged) {
            Map<String, String> forCheckFromRequired = relsFromDef.stream()
                    .filter(RelationDef::isRequired)
                    .collect(Collectors.toMap(AbstractEntityDef::getName, AbstractEntityDef::getDisplayName));

            if (!forceCheck) {
                List<String> updatedRelNames = new ArrayList<>();
                if (ctx.getRelations() != null && MapUtils.isNotEmpty(ctx.getRelations().getRelations())) {
                    updatedRelNames.addAll(ctx.getRelations().getRelations().keySet());
                }
                if (ctx.getRelationDeletes() != null && MapUtils.isNotEmpty(ctx.getRelationDeletes().getRelations())) {
                    updatedRelNames.addAll(ctx.getRelationDeletes().getRelations().keySet());
                }

                forCheckFromRequired.keySet().removeIf(relName -> !updatedRelNames.contains(relName));
            }

            if (!forCheckFromRequired.isEmpty()) {
                checkRequiredRelationPeriods(ctx, forCheckFromRequired);
            }

            List<String> validationErrors = new ArrayList<>();
            if (ctx.getRelations() != null && MapUtils.isNotEmpty(ctx.getRelations().getRelations())) {
                ctx.getRelations().getRelations().values().stream()
                        .flatMap(Collection::stream)
                        .forEach(s -> checkRelationToSideRecordPeriod(s, false, validationErrors));
            }
            if (CollectionUtils.isNotEmpty(validationErrors)) {
                throw new BusinessException("Invalid upsert request context. Not correct validity period linked record.",
                        EX_DATA_UPSERT_RELS_INCORRECT_TO_SIDE_PERIOD,
                        validationErrors, validationErrors.size());
            }

        }
    }

    /** Check if need force validation by update metamodel for record
     *
     * @param previousTimeline
     * @param relsFromDef
     * @return
     */
    private boolean isMetaInfoForRelationsWasDeprecated(WorkflowTimelineDTO previousTimeline, List<RelationDef> relsFromDef) {
        boolean result = false;
        Date lastUpdateRecord = null;
        if (previousTimeline != null) {
            lastUpdateRecord = previousTimeline.getIntervals()
                    .stream()
                    .filter(interval -> CollectionUtils.isNotEmpty(interval.getContributors()))
                    .flatMap(interval -> interval.getContributors().stream())
                    .map(ContributorDTO::getLastUpdate)
                    .max(Comparator.naturalOrder()).orElse(null);
        }
        if (lastUpdateRecord == null) {
            return result;
        }
        Date lastCreateModel = relsFromDef.stream()
                .filter(RelationDef::isRequired)
                .map(relationDef -> JaxbUtils.xmlGregorianCalendarToDate(relationDef.getCreateAt()))
                .max(Comparator.naturalOrder()).orElse(null);
        Date lastUpdateModel = relsFromDef.stream()
                .filter(RelationDef::isRequired)
                .filter(relationDef -> relationDef.getUpdatedAt() != null)
                .map(relationDef -> JaxbUtils.xmlGregorianCalendarToDate(relationDef.getUpdatedAt()))
                .max(Comparator.naturalOrder()).orElse(null);
        if (lastCreateModel != null && lastUpdateRecord.before(lastCreateModel)) {
            result = true;
        } else if (lastUpdateModel != null) {
            result = lastUpdateRecord.before(lastUpdateModel);
        }
        return result;
    }
    /**
     * Check required relation periods
     * @param ctx record upsert context
     * @param forCheckFromRequired list  relations for check
     */
    private void checkRequiredRelationPeriods(UpsertRequestContext ctx, Map<String, String> forCheckFromRequired) {
        SearchRequestContext searchCtx = SearchRequestContext.forEtalonRelation(ctx.keys().getEntityName())
                .routings(Collections.singletonList(ctx.keys().getEtalonKey().getId()))
                .form(FormFieldsGroup.createAndGroup(
                        FormField.strictString(RelationHeaderField.FIELD_FROM_ETALON_ID.getField(),
                                ctx.keys().getEtalonKey().getId()),
                        FormField.strictValues(SimpleDataType.STRING, RelationHeaderField.REL_NAME.getField(),
                                forCheckFromRequired.keySet()),
                        FormField.strictValue(SimpleDataType.BOOLEAN, RelationHeaderField.FIELD_DELETED.getField(), false))
                )
                .returnFields(RelationHeaderField.FIELD_FROM.getField(),
                        RelationHeaderField.FIELD_TO.getField(),
                        RelationHeaderField.REL_NAME.getField(),
                        RelationHeaderField.FIELD_TO_ETALON_ID.getField())
                .runExits(false)
                .onlyQuery(true)
                .count(SearchRequestContext.MAX_PAGE_SIZE)
                .build();

        SearchResultDTO dto = searchService.search(searchCtx);
        if (CollectionUtils.isEmpty(dto.getHits())) {
            throw new BusinessException("Invalid upsert request context. Some of required relations not present",
                    EX_DATA_UPSERT_REQUIRED_RELS_IS_NOT_PRESENTED,
                    forCheckFromRequired.values());
        }

        Map<String, List<Range<Date>>> relationCovering = new HashMap<>();
        Map<String, Pair<String, Range<Date>>> relationPeriodsInverse = new HashMap<>();
        for (SearchResultHitDTO hit : dto.getHits()) {
            Range<Date> range = SearchUtils.getDateRange(hit, RelationHeaderField.FIELD_FROM.getField(), RelationHeaderField.FIELD_TO.getField());
            if (range != null) {
                String relName = hit.getFieldFirstValue(RelationHeaderField.REL_NAME.getField());
                relationPeriodsInverse.put(hit.getFieldFirstValue(RelationHeaderField.FIELD_TO_ETALON_ID.getField()), Pair.of(relName, range));
                relationCovering.computeIfAbsent(relName, s -> new ArrayList<>())
                        .add(range);
            }
        }

        long recordFrom = ctx.getValidFrom() == null ? SearchUtils.ES_MIN_DATE.getTime() : ctx.getValidFrom().getTime();
        long recordTo = ctx.getValidTo() == null ? SearchUtils.ES_MAX_DATE.getTime() : ctx.getValidTo().getTime();
        Set<String> failed = new HashSet<>();
        for (String relName : forCheckFromRequired.keySet()) {
            List<Range<Date>> periods = relationCovering.get(relName);
            boolean covered = isPartialCovered(recordFrom, recordTo, periods);
            if (!covered) {
                failed.add(forCheckFromRequired.get(relName));
            }
        }

        if (CollectionUtils.isNotEmpty(failed)) {
            throw new BusinessException("Invalid upsert request context. Some of required relations not present",
                    EX_DATA_UPSERT_REQUIRED_RELS_IS_NOT_PRESENTED,
                    failed);
        }

        Map<String, List<String>> toSideEtalonsByEntityName = new HashMap<>();

        for(Map.Entry<String, Pair<String, Range<Date>>> entry : relationPeriodsInverse.entrySet()) {
            RelationDef relationDef = metaModelService.getRelationById(entry.getValue().getLeft());
            toSideEtalonsByEntityName.computeIfAbsent(relationDef.getToEntity(), s -> new ArrayList<>())
                    .add(entry.getKey());
        }

        Map<String, List<Range<Date>>> toSideRecordsCovering = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : toSideEtalonsByEntityName.entrySet()) {
            SearchRequestContext toSideSearchRequest =  SearchRequestContext.forEtalonData(entry.getKey())
                    .form(FormFieldsGroup.createAndGroup(
                            FormField.strictValues(SimpleDataType.STRING, RecordHeaderField.FIELD_ETALON_ID.getField(),
                                    entry.getValue()),
                            FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), true),
                            FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), false),
                            FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_INACTIVE.getField(), false)
                    ))
                    .returnFields(RecordHeaderField.FIELD_FROM.getField(),
                            RecordHeaderField.FIELD_TO.getField(),
                            RecordHeaderField.FIELD_ETALON_ID.getField())
                    .runExits(false)
                    .onlyQuery(true)
                    .facets(Arrays.asList(FacetName.FACET_NAME_PUBLISHED_ONLY, FacetName.FACET_NAME_ACTIVE_ONLY, FacetName.FACET_UN_RANGED))
                    .count(SearchRequestContext.MAX_PAGE_SIZE)
                    .build();
            SearchResultDTO toSideRecords = searchService.search(toSideSearchRequest);
            for (SearchResultHitDTO hit : toSideRecords.getHits()) {
                Range<Date> range = SearchUtils.getDateRange(hit, RecordHeaderField.FIELD_FROM.getField(), RecordHeaderField.FIELD_TO.getField());
                if (range != null) {
                    toSideRecordsCovering.computeIfAbsent(hit.getFieldFirstValue(RecordHeaderField.FIELD_ETALON_ID.getField()), s -> new ArrayList<>())
                            .add(range);
                }
            }
        }



        for (Map.Entry<String, Pair<String, Range<Date>>> entry : relationPeriodsInverse.entrySet()) {
            long relFrom = entry.getValue().getRight().getMinimum() == null ? SearchUtils.ES_MIN_DATE.getTime() : entry.getValue().getRight().getMinimum().getTime();
            long relTo = entry.getValue().getRight().getMaximum() == null ? SearchUtils.ES_MAX_DATE.getTime() : entry.getValue().getRight().getMaximum().getTime();
            if (!isCovered(relFrom, relTo, toSideRecordsCovering.get(entry.getKey()))) {
                failed.add(MessageUtils.getMessage("app.data.upsert.rels.incorrect.toside.information",
                        forCheckFromRequired.get(entry.getValue().getLeft()),
                        entry.getKey(),
                        entry.getValue().getRight().getMinimum() == null
                            || entry.getValue().getRight().getMinimum().getTime() == SearchUtils.ES_MIN_DATE.getTime()
                                ? "-" + INFINITY_SYMBOL
                                : entry.getValue().getRight().getMinimum(),
                        entry.getValue().getRight().getMaximum() == null
                            || entry.getValue().getRight().getMaximum().getTime() == SearchUtils.ES_MAX_DATE.getTime()
                                ? "+" + INFINITY_SYMBOL
                                : entry.getValue().getRight().getMaximum()));
            }
        }

        if (CollectionUtils.isNotEmpty(failed)) {
            throw new BusinessException("Invalid upsert request context. Not correct validity period linked record.",
                    EX_DATA_UPSERT_REQUIRED_RELS_INCORRECT_TO_SIDE_PERIOD,
                    failed, failed.size());
        }
    }


        /**
         * Check to side periods for relation
         * For required relation full covered, for not required intersection.
         * @param urrc relation context for check
         */
    private void checkRelationToSideRecordPeriod(UpsertRelationRequestContext urrc, boolean required, List<String> validationErrors) {
        RecordKeys toKeys = urrc.relationKeys().getTo();
        RelationDef relationDef = urrc.getFromStorage(StorageId.RELATIONS_META_DEF);
        if (relationDef == null) {
            relationDef = metaModelService.getRelationById(urrc.getRelationName());
        }
        if (relationDef.isRequired() != required) {
            return;
        }

        SearchRequestContext searchCtx = SearchRequestContext.forEtalonData(toKeys.getEntityName())
                .routings(Collections.singletonList(toKeys.getEtalonKey().getId()))
                .form(FormFieldsGroup.createAndGroup(
                        FormField.strictString(RecordHeaderField.FIELD_ETALON_ID.getField(),
                                toKeys.getEtalonKey().getId()),
                        FormField.range(SimpleDataType.DATE, RecordHeaderField.FIELD_TO.getField(),
                                urrc.getValidFrom(), null),
                        FormField.range(SimpleDataType.DATE, RecordHeaderField.FIELD_FROM.getField(),
                                null, urrc.getValidTo()),
                        FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(),
                                true),
                        FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(),
                                false),
                        FormField.strictValue(SimpleDataType.BOOLEAN, RecordHeaderField.FIELD_INACTIVE.getField(),
                                false)
                ))
                .returnFields(RecordHeaderField.FIELD_FROM.getField(),
                        RecordHeaderField.FIELD_TO.getField())
                .runExits(false)
                .onlyQuery(true)
                .facets(Arrays.asList(FacetName.FACET_NAME_PUBLISHED_ONLY, FacetName.FACET_NAME_ACTIVE_ONLY, FacetName.FACET_UN_RANGED))
                .count(SearchRequestContext.MAX_PAGE_SIZE)
                .build();
        SearchResultDTO searchResult = searchService.search(searchCtx);

        if (searchResult.getTotalCount() == 0) {
            validationErrors.add(MessageUtils.getMessage("app.data.upsert.rels.incorrect.toside.information",
                    relationDef.getDisplayName(),
                    toKeys.getEtalonKey().getId(),
                    urrc.getValidFrom() == null || urrc.getValidFrom().getTime() == SearchUtils.ES_MIN_DATE.getTime()
                            ? "-" + INFINITY_SYMBOL
                            : urrc.getValidFrom(),
                    urrc.getValidTo() == null || urrc.getValidTo().getTime() == SearchUtils.ES_MAX_DATE.getTime()
                            ? "+" + INFINITY_SYMBOL
                            : urrc.getValidTo()));
        }

        if (relationDef.isRequired()) {
            List<Range<Date>> periods = searchResult.getHits().stream()
                    .map(hit -> SearchUtils.getDateRange(hit, RelationHeaderField.FIELD_FROM.getField(),
                            RelationHeaderField.FIELD_TO.getField()))
                    .collect(Collectors.toList());
            long relationFrom = urrc.getValidFrom() == null ? SearchUtils.ES_MIN_DATE.getTime() : urrc.getValidFrom().getTime();
            long relationTo = urrc.getValidTo() == null ? SearchUtils.ES_MAX_DATE.getTime() : urrc.getValidTo().getTime();
            if (!isCovered(relationFrom, relationTo, periods)) {
                validationErrors.add(MessageUtils.getMessage("app.data.upsert.rels.incorrect.toside.information",
                        relationDef.getDisplayName(),
                        toKeys.getEtalonKey().getId(),
                        urrc.getValidFrom() == null || urrc.getValidFrom().getTime() == SearchUtils.ES_MIN_DATE.getTime()
                                ? "-" + INFINITY_SYMBOL
                                : urrc.getValidFrom(),
                        urrc.getValidTo() == null || urrc.getValidTo().getTime() == SearchUtils.ES_MAX_DATE.getTime()
                                ? "+" + INFINITY_SYMBOL
                                : urrc.getValidTo()));
            }
        }
    }
    /**
     * Check list periods full covered interval
     * @param from left side interval
     * @param to right side interval
     * @param periods periods for check
     * @return true if full covered, else false
     */
    private boolean isCovered(long from, long to, List<Range<Date>> periods) {
        boolean covered = false;
        if (periods != null) {
            periods.sort((o1, o2) -> {

                if (o1.getComparator() == null && o2.getMinimum() == null) {
                    return 0;
                } else if (o1.getMinimum() == null) {
                    return -1;
                } else if (o2.getMinimum() == null) {
                    return +1;
                }

                return o1.getMinimum().compareTo(o2.getMinimum());
            });
            long checkFrom = from;
            long checkTo = to;
            long delta = TimeUnit.MILLISECONDS.toMillis(1);

            for (Range<Date> period : periods) {
                if (period.getMinimum().getTime() > checkFrom + delta) {
                    break;
                }
                if (checkTo - delta < period.getMaximum().getTime()) {
                    covered = true;
                    break;
                }
                checkFrom = period.getMaximum().getTime();
            }
        }
        return covered;
    }

    private boolean isPartialCovered(long from, long to, List<Range<Date>> periods) {
        boolean result = false;
        if (CollectionUtils.isNotEmpty(periods)) {
            result = periods.stream().anyMatch(dateRange -> {
                if (dateRange.getMinimum() == null) {
                    if (dateRange.getMaximum() == null) {
                        return true;
                    } else {
                        return dateRange.getMaximum().getTime() > from;
                    }
                } else {
                    if (dateRange.getMaximum() == null) {
                        return to > dateRange.getMinimum().getTime();
                    } else {
                        return dateRange.getMaximum().getTime() > from && to > dateRange.getMinimum().getTime();
                    }
                }
            });
        }
        return result;
    }


    /**
     * Checks attributes for validity
     *
     * @param record the record
     * @param attributesWrapper attributes wrapper
     * @param prefix the prefix
     * @param level current level
     * @throws DataProcessingException
     */
    private void checkAttributes(
            final DataRecord record,
            final AttributesWrapper attributesWrapper,
            final String prefix,
            final int level
    ) {
        final Map<String, AttributeInfoHolder> attrs = attributesWrapper.getAttributes();

        Collection<String> requiredInLevelAttrs = attrs.entrySet().stream()
                .filter(attr -> attr.getValue().getLevel() == level)
                .filter(attr -> attr.getValue().isOfPath(prefix))
                .filter(attr -> isRequiredAttr(attr.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (Attribute attr : record.getAllAttributes()) {
            if (attr.getAttributeType() == Attribute.AttributeType.COMPLEX) {
                continue;
            }

            String attrPath = ModelUtils.getAttributePath(level, prefix, attr.getName());
            AttributeInfoHolder infoHolder = attrs.get(attrPath);
            checkFlatAttribute(attr, infoHolder, attrPath);

            if (valueExists(attr)) {
                requiredInLevelAttrs.remove(attrPath);
            }
        }

        if (!requiredInLevelAttrs.isEmpty()) {
            final String message = "Some required attributes are not present. {}";
            LOGGER.warn(message, requiredInLevelAttrs);
            throw new BusinessException(message, EX_DATA_UPSERT_REQUIRED_ATTRS_IS_NOT_PRESENTED,
                    requiredInLevelAttrs.stream()
                            .map(name -> attrs.get(name).getAttribute().getDisplayName())
                            .collect(Collectors.toList())
            );
        }

        Map<String, Integer> count = new HashMap<>();
        for (ComplexAttribute attr : record.getComplexAttributes()) {
            String attrPath = ModelUtils.getAttributePath(level, prefix, attr.getName());
            for (DataRecord nested : attr) {
                checkAttributes(
                        nested,
                        attributesWrapper,
                        attrPath,
                        level + 1
                );
            }
            count.putIfAbsent(attrPath, 0);
            count.put(attrPath, count.get(attrPath) + attr.size());
        }

        attrs.entrySet().stream()
                .filter(entity -> entity.getValue().getLevel() == level)
                .filter(entity -> entity.getValue().isOfPath(prefix))
                .filter(entity -> entity.getValue().isComplex())
                .forEach(entity -> checkCountOfComplexAttrs(count.get(entity.getKey()), (ComplexAttributeDef) entity.getValue().getAttribute()));
    }

    private boolean valueExists(Attribute attr) {

        if (attr.getAttributeType() == Attribute.AttributeType.ARRAY) {
            return !((ArrayAttribute<?>) attr).isEmpty();
        } else if (attr.getAttributeType() == Attribute.AttributeType.CODE) {
            return ((CodeAttribute<?>) attr).getValue() != null;
        } else if (attr.getAttributeType() == Attribute.AttributeType.SIMPLE) {
            return ((SimpleAttribute<?>) attr).getValue() != null;
        }

        return false;
    }

    private boolean isRequiredAttr(AttributeInfoHolder holder) {
        if (holder.isSimple()) {
            return !((SimpleAttributeDef) holder.getAttribute()).isNullable();
        } else if (holder.isCode()) {
            return !((CodeAttributeDef) holder.getAttribute()).isNullable();
        } else if (holder.isArray()) {
            return !((ArrayAttributeDef) holder.getAttribute()).isNullable();
        }
        return false;
    }

    /**
     * Check number of complex attributes.
     *
     * @param realCount real number of complex attributes in entity
     * @param complexAttribute - definition of complex attributes
     */
    private void checkCountOfComplexAttrs(@Nullable Integer realCount, @Nonnull ComplexAttributeDef complexAttribute) {
        BigInteger count = realCount == null ? BigInteger.ZERO : BigInteger.valueOf(realCount);
        BigInteger minCount = complexAttribute.getMinCount();
        BigInteger maxCount = complexAttribute.getMaxCount();
        if ((minCount != null && count.compareTo(minCount) < 0) || (maxCount != null && count.compareTo(maxCount) > 0)) {
            final String message = "Quantity of complex attributes '{}' should be in range {} - {} but current value is {}. Upsert rejected.";
            LOGGER.warn(message, complexAttribute.getName(), minCount, maxCount, count);
            if (maxCount == null) {
                throw new BusinessException(message, EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_LOWER_BOUND,
                        complexAttribute.getDisplayName(), minCount, count);
            } else {
                throw new BusinessException(message, EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_IN_RANGE,
                        complexAttribute.getDisplayName(), minCount, maxCount, count);
            }
        }
    }

    /**
     * Check simple attribute.
     *
     * @param attr - value
     * @param attrDef -  attribute definition
     * @param attr path
     */
    @SuppressWarnings("unchecked")
    private void checkFlatAttribute(Attribute attr, AttributeInfoHolder attrDef, String attrPath) {

        if (attrDef == null) {
            final String message = "Attribute '{}' supplied for upsert is missing in the model. Upsert rejected.";
            LOGGER.warn(message, attrPath);
            throw new DataProcessingException(message, EX_DATA_UPSERT_MISSING_ATTRIBUTE,
                    attrPath);
        }

        boolean wrongType =
                (attrDef.isSimple() && attr.getAttributeType() != Attribute.AttributeType.SIMPLE)
                        || (attrDef.isCode() && attr.getAttributeType() != Attribute.AttributeType.CODE)
                        || (attrDef.isArray() && attr.getAttributeType() != Attribute.AttributeType.ARRAY);

        if (wrongType) {
            final String message = "Attribute {} supplied for upsert is of the wrong type {} compared to the model (type {} is expected). Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getAttributeType().name(),
                    attrDef.isSimple()
                            ? Attribute.AttributeType.SIMPLE.name()
                            : attrDef.isCode() ? Attribute.AttributeType.CODE.name() : Attribute.AttributeType.ARRAY.name());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_WRONG_ATTRIBUTE_TYPE,
                    attrPath,
                    attr.getAttributeType().name(),
                    attrDef.isSimple()
                            ? Attribute.AttributeType.SIMPLE.name()
                            : attrDef.isCode() ? Attribute.AttributeType.CODE.name() : Attribute.AttributeType.ARRAY.name());
        }

        if (attrDef.isSimple()) {

            SimpleAttributeDef sDef = attrDef.narrow();
            boolean isDataType = sDef.getSimpleDataType() != null;
            if (isDataType) {
                checkSimpleAttributeValue((SimpleAttribute<?>) attr, sDef.getSimpleDataType(), attrPath);

                if (sDef.getSimpleDataType() == SimpleDataType.MEASURED) {
                    checkMeasuredAttr((MeasuredSimpleAttributeImpl) attr, sDef);
                }

                if (sDef.getSimpleDataType() == SimpleDataType.BLOB || sDef.getSimpleDataType() == SimpleDataType.CLOB) {
                    checkLargeObjectAttr((SimpleAttribute<AbstractLargeValue>) attr, sDef);
                }
            } else {
                if (attrDef.isEnumValue() || attrDef.isLinkTemplate()) {
                    checkStringValueAndType((SimpleAttribute<?>) attr, attrPath);
                }

                if (attrDef.isEnumValue()) {
                    checkEnumAttr((SimpleAttribute<?>) attr, sDef);
                }

                if (attrDef.isLookupLink()) {
                    checkValueForLookupTarget((SimpleAttribute<?>) attr, sDef);
                }
            }
        } else if (attrDef.isCode()) {
            checkCodeAttributeValue((CodeAttribute<?>) attr, ((AbstractSimpleAttributeDef) attrDef.getAttribute()).getSimpleDataType(), attrPath);
        } else if (attrDef.isArray()) {
            checkArrayAttributeValue((ArrayAttribute<?>) attr, attrDef.narrow(), attrPath);
        }
    }

    /**
     * TODO remove this as soon specific types exist.
     *
     * @param attr
     * @param sDef
     */
    private void checkValueForLookupTarget(SimpleAttribute<?> attr, SimpleAttributeDef sDef) {

        String targetName = sDef.getLookupEntityType();
        if (isBlank(targetName)) {
            return;
        }

        LookupEntityWrapper ew = metaModelService.getValueById(targetName, LookupEntityWrapper.class);
        SimpleDataType targetType = ew.getEntity().getCodeAttribute().getSimpleDataType();
        if ((targetType == SimpleDataType.INTEGER && attr.getDataType() != SimpleAttribute.DataType.INTEGER)
                || (targetType == SimpleDataType.STRING && attr.getDataType() != SimpleAttribute.DataType.STRING)) {
            final String message = "Wrong code attribute link value type {}, referencing {}. Upsert rejected.";
            LOGGER.warn(message, attr.getValue(), targetName);
            throw new DataProcessingException(message,
                    ExceptionId.EX_DATA_UPSERT_WRONG_SIMPLE_CODE_ATTRIBUTE_REFERENCE_VALUE,
                    attr.getDataType().name(), targetName);
        }
    }

    //check is it enum, link or reference
    private void checkStringValueAndType(SimpleAttribute<?> attr, String attrPath) {

        boolean notAStringAttrType = attr.getDataType() != null && attr.getDataType() != SimpleAttribute.DataType.STRING;
        if (notAStringAttrType) {
            final String message =
                    "Attribute '{}' supplied for upsert is either Enumeration or calculated Link in the model, "
                            + "and has to have type 'String' "
                            + "while type attribute '{}' is supplied for upsert. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name());
            throw new DataProcessingException(message, EX_DATA_UPSERT_WRONG_SPECIAL_ATTRIBUTE_TYPE, attrPath,
                    attr.getDataType().name());
        }
    }

    // check is enum value present
    private void checkEnumAttr(SimpleAttribute<?> attr, SimpleAttributeDef sDef) {
        String value = attr.castValue();
        String enumId = sDef.getEnumDataType();
        if (isBlank(enumId)) {
            return;
        }
        EnumerationDataType enumeration = metaModelService.getEnumerationById(enumId);
        Collection<EnumerationValue> enumValues = enumeration == null ? emptyList() : enumeration.getEnumVal();
        boolean isEnumPresent = value == null ? true : enumValues.stream().anyMatch(val -> val.getName().equals(value));
        if (!isEnumPresent) {
            throw new BusinessException("Enum value doesn't present", EX_DATA_UPSERT_ENUM_ATTRIBUTE_INCORRECT,
                    value, enumId);
        }
    }

    private void checkMeasuredAttr(MeasuredSimpleAttributeImpl measAttr, SimpleAttributeDef sDef) {
        String valueAttrId = measAttr.getValueId();
        String unitId = measAttr.getInitialUnitId();
        if (valueAttrId == null && unitId == null) {
            return;
        }
        String valueId = sDef.getMeasureSettings().getValueId();
        MeasurementValue measurementValue = measurementService.getValueById(valueId);
        if (measurementValue == null) {
            return;
        }
        if (valueAttrId != null && !valueAttrId.equals(valueId)) {
            throw new DataProcessingException("measured value is not present",
                    EX_DATA_ATTRIBUTE_MEASURED_VALUE_NOT_PRESENT, measAttr.getName());
        }
        if (unitId != null && !measurementValue.present(unitId)) {
            throw new DataProcessingException("measured unit is not present",
                    EX_DATA_ATTRIBUTE_MEASURED_UNIT_NOT_PRESENT, measAttr.getName());
        }
    }

    /**
     * @param attr - value
     * @param type - attr type
     * @param attrPath - path to value
     */
    private void checkCodeAttributeValue(@Nonnull CodeAttribute<?> attr, @Nonnull SimpleDataType type, @Nonnull String attrPath) {

        CodeAttribute.CodeDataType expectedType = CodeAttribute.CodeDataType.valueOf(type.name());
        if (attr.getDataType() != expectedType) {
            final String message = "Code attribute {} supplied for upsert has type {} "
                    + "while type attribute {} is expected from the model. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_WRONG_CODE_ATTRIBUTE_VALUE_TYPE,
                    attrPath,
                    attr.getDataType().name(),
                    expectedType.name());
        }
    }

    /**
     * @param attr - value
     * @param type - attr type
     * @param attrPath - path to value
     */
    private void checkSimpleAttributeValue(@Nonnull SimpleAttribute<?> attr, @Nonnull SimpleDataType type, @Nonnull String attrPath) {
        SimpleAttribute.DataType expectedType = SimpleAttribute.DataType.valueOf(type.name());
        if (attr.getDataType() != expectedType) {
            final String message = "Attribute {} supplied for upsert has type {} "
                    + "while type attribute {} is expected from the model. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
            throw new DataProcessingException(message, EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE,
                    attrPath,
                    attr.getDataType().name(),
                    expectedType.name());
        }
    }

    /**
     * Check expected value and type attribute.
     *
     * @param attr the attribute
     * @param def attribute definition
     * @param attrPath attribute path
     */
    private void checkArrayAttributeValue(ArrayAttribute<?> attr, ArrayAttributeDef def, String attrPath) {

        ArrayValueType modelType = def.getArrayValueType();
        if (modelType != null) {
            ArrayAttribute.ArrayDataType expectedType = ArrayAttribute.ArrayDataType.valueOf(modelType.name());
            if (attr.getDataType() != expectedType) {
                final String message = "Array attribute {} supplied for upsert has type {} "
                        + "while type attribute {} is expected from the model. Upsert rejected.";
                LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
                throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_WRONG_ARRAY_ATTRIBUTE_VALUE_TYPE,
                        attrPath,
                        attr.getDataType().name(),
                        expectedType.name());
            }
        } else {
            String targetName = def.getLookupEntityType();
            if (isBlank(targetName)) {
                return;
            }

            LookupEntityWrapper ew = metaModelService.getValueById(targetName, LookupEntityWrapper.class);
            SimpleDataType targetType = ew.getEntity().getCodeAttribute().getSimpleDataType();
            if ((targetType == SimpleDataType.INTEGER && attr.getDataType() != ArrayAttribute.ArrayDataType.INTEGER)
                    || (targetType == SimpleDataType.STRING && attr.getDataType() != ArrayAttribute.ArrayDataType.STRING)) {
                final String message = "Wrong array code attribute link value type {}, referencing {}. Upsert rejected.";
                LOGGER.warn(message, attr.getValue(), targetName);
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_UPSERT_WRONG_ARRAY_CODE_ATTRIBUTE_REFERENCE_VALUE,
                        attr.getDataType().name(), targetName);
            }
        }
    }


    private void checkLargeObjectAttr(SimpleAttribute<AbstractLargeValue> attr, SimpleAttributeDef sDef) {

        if (attr.getValue() == null) {
            return;
        }

        boolean isExist = lobComponent.checkExistLargeObject(new FetchLargeObjectRequestContext.FetchLargeObjectRequestContextBuilder()
                .recordKey(attr.getValue().getId())
                .binary(sDef.getSimpleDataType() == SimpleDataType.BLOB)
                .build());

        if (!isExist) {
            throw new DataProcessingException("Can't find large object for attribute", EX_DATA_UPSERT_LARGE_OBJECT_VALUE_UNAVAILABLE, attr.getName());
        }
    }
}
