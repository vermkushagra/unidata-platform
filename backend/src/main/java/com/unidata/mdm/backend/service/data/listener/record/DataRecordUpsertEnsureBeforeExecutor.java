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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.MatchingHeaderField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerCodeAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.common.types.impl.StringCodeAttributeImpl;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.AbstractIdGenerationStrategyExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.matching.MatchingService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.BVTMapWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.AbstractExternalIdGenerationStrategyDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 *         Old 'ensure before' part of the {@linkplain OriginRecordsComponent}.
 */
public class DataRecordUpsertEnsureBeforeExecutor extends AbstractIdGenerationStrategyExecutor
        implements DataRecordBeforeExecutor<UpsertRequestContext> {
    /**
     * Common functionality.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;
    /**
     * Origin component.
     */
    @Autowired
    private OriginRecordsComponent originComponent;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private SearchServiceExt searchService;

    /**
     * Constructor.
     */
    public DataRecordUpsertEnsureBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Pre-process
            String entityName = selectEntityName(ctx);
            BVTMapWrapper wrapper = selectBvtMapWrapper(entityName);

            // 2. Check for code attributes generation
            ensureCodeAttributeAutogeneration(wrapper, ctx);

            // 3. Check for "ext id autogeneration" rules turned on and apply them
            boolean batchInsertWithKeys = ctx.isBatchUpsert() && Objects.nonNull(ctx.keys());
            ensureExternalIdAutoGeneration(wrapper, ctx, batchInsertWithKeys);

            // 4. Identify record
            RecordKeys keys = batchInsertWithKeys
                    ? ctx.keys()
                    : commonComponent.identify(ctx);

            // 4.1 Identify record by matching (optional)
            if (keys == null && ctx.isResolveByMatching()) {
                EtalonKey etalonKey = indentifyRecordByMatchingRules(ctx.getRecord(), ctx.getEntityName(), ctx.getValidFrom(), ctx.getValidTo());
                if (etalonKey != null) {
                    OriginKey originKey = OriginKey.builder()
                            .entityName(ctx.getEntityName())
                            .externalId(ctx.getExternalId())
                            .sourceSystem(ctx.getSourceSystem())
                            .build();
                    keys = RecordKeys.builder()
                            .entityName(ctx.getEntityName())
                            .etalonKey(etalonKey)
                            .originKey(originKey)
                            .build();
                    ctx.putToStorage(ctx.keysId(), keys);
                }
            }

            // 5. Set record timestamp
            Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
            if (ts == null) {
                ts = new Date(System.currentTimeMillis());
            }

            // 6. Set up virtual origin record for legacy code,
            // expecting record with info section upon upsert
            OriginRecordInfoSection is = new OriginRecordInfoSection()
                    .withCreateDate(ts)
                    .withUpdateDate(ts)
                    .withCreatedBy(SecurityUtils.getCurrentUserName())
                    .withShift(DataShift.PRISTINE)
                    .withValidFrom(ctx.getValidFrom())
                    .withValidTo(ctx.getValidTo());

            if (ctx.isEtalon()) {
                OriginKey key = keys != null ? keys.getOriginKey() : null;
                if (key == null) {
                    // Impossible. The record will be rejected.
                }

                is.withOriginKey(key);
            } else if (ctx.isOrigin()) {
                OriginKey key = keys != null ? keys.getOriginKey() : null;
                if (key == null && ctx.isOriginExternalId()) {
                    key = OriginKey.builder()
                            .entityName(ctx.getEntityName())
                            .externalId(ctx.getExternalId())
                            .sourceSystem(ctx.getSourceSystem())
                            .build();
                }

                is.withOriginKey(key);
            } else if (ctx.isEnrichment()) {
                OriginKey key = keys != null ? keys.getOriginKey() : null;
                if (key == null) {
                    key = OriginKey.builder()
                            .enrichment(true)
                            .entityName(ctx.getEntityName())
                            .externalId(ctx.getExternalId())
                            .sourceSystem(ctx.getSourceSystem())
                            .build();
                }

                is.withOriginKey(key);
            }

            // 7. Set action type
            UpsertAction action = keys == null || batchInsertWithKeys && keys.getOriginKey().getRevision() == 0
                    ? UpsertAction.INSERT
                    : UpsertAction.UPDATE;

            // 8. Check for mergeWithPreviousVersion flag and re-construct the record if possible
            if (action == UpsertAction.UPDATE && ctx.isMergeWithPreviousVersion()) {
                ensureMergeWithPreviousVersion(ctx);
            }

            // 9. Set up record
            OriginRecord origin = new OriginRecordImpl()
                    .withDataRecord(ctx.getRecord())
                    .withInfoSection(is);

            ctx.putToStorage(StorageId.DATA_UPSERT_KEYS, keys);
            ctx.putToStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD, origin);
            ctx.putToStorage(StorageId.DATA_UPSERT_EXACT_ACTION, action);
            ctx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, ts);
            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Select the right N\BVT wrapper.
     *
     * @param entityName the entity name
     * @return wrapper or null
     */
    private BVTMapWrapper selectBvtMapWrapper(String entityName) {

        if (Objects.nonNull(entityName)) {
            // Try entity type first
            BVTMapWrapper wrapper = metaModelService.getValueById(entityName, EntityWrapper.class);

            // 2. Try lookup secondly.
            if (wrapper == null) {
                wrapper = metaModelService.getValueById(entityName, LookupEntityWrapper.class);
            }

            return wrapper;
        }

        return null;
    }

    /**
     * Selects entity name.
     *
     * @param ctx the context
     * @return entity name or null
     */
    private String selectEntityName(UpsertRequestContext ctx) {

        if (ctx.getEntityName() != null) {
            return ctx.getEntityName();
        } else {
            if (ctx.keys() != null) {

                if (ctx.keys().getEntityName() != null) {
                    return ctx.keys().getEntityName();
                }

                return ctx.keys().getOriginKey() != null
                        ? ctx.keys().getOriginKey().getEntityName()
                        : null;
            }
        }

        return null;
    }

    /**
     * Applies autogeneration rules for external ids.
     *
     * @param wrapper the wrapper
     * @param ctx the context
     * @param batchInsertWithKeys flag
     * @return true, if applied, false otherwise
     */
    private boolean ensureExternalIdAutoGeneration(BVTMapWrapper wrapper, UpsertRequestContext ctx, boolean batchInsertWithKeys) {

        if (!ctx.isEtalonRecordKey()
                && !ctx.isOriginRecordKey()
                && !ctx.isEnrichmentKey()
                && !ctx.isGsnKey()
                && (Objects.isNull(ctx.getExternalId()) && Objects.nonNull(ctx.getEntityName()) && Objects.nonNull(ctx.getSourceSystem()))) {

            AbstractExternalIdGenerationStrategyDef strategy = wrapper.isEntity()
                    ? ((EntityWrapper) wrapper).getEntity().getExternalIdGenerationStrategy()
                    : ((LookupEntityWrapper) wrapper).getEntity().getExternalIdGenerationStrategy();

            if (strategy == null) {
                return false;
            }

            Object externalId = applyAutogenerationStrategy(ctx.getRecord(), ctx.getEntityName(), strategy);
            if (externalId == null) {
                return false;
            }

            // 2.1 Check key length
            if (externalId.toString().length() > 512) {
                final String message = "Unable to generate externalId, using autogeneration strategy for entity {}. "
                        + "Generated key length exceeds the limit of 512 characters.";
                LOGGER.warn(message, ctx.getEntityName());
                throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_ID_GENERATION_STRATEGY_KEY_LENGTH, ctx.getEntityName());
            }

            ctx.setExternalId(externalId.toString());
            if (batchInsertWithKeys) {

                RecordKeys keys = RecordKeys.builder(ctx.keys())
                        .originKey(OriginKey.builder(ctx.keys().getOriginKey())
                                .externalId(externalId.toString())
                                .build())
                        .build();
                ctx.putToStorage(ctx.keysId(), keys);
            }

            return true;
        }

        return false;
    }

    /**
     * Applies autogeneration rules to lookup , if some defined.
     *
     * @param wrapper the wrapper
     * @param ctx the context
     * @return true, if applied, false otherwise
     */
    private boolean ensureCodeAttributeAutogeneration(BVTMapWrapper wrapper, UpsertRequestContext ctx) {

        if (Objects.nonNull(wrapper) && wrapper.isLookup() && Objects.nonNull(ctx.getRecord())) {

            LookupEntityDef lookup = ((LookupEntityWrapper) wrapper).getEntity();
            AbstractExternalIdGenerationStrategyDef strategy
                    = lookup.getCodeAttribute().getExternalIdGenerationStrategy();

            if (strategy == null) {
                return false;
            }

            // 1.1 Current value is set. Do not overwrite
            CodeAttribute<?> current = ctx.getRecord().getCodeAttribute(lookup.getCodeAttribute().getName());
            if (current != null && current.getValue() != null) {
                return false;
            }

            Object codeAttributeValue = applyAutogenerationStrategy(ctx.getRecord(), ctx.getEntityName(), strategy);
            if (codeAttributeValue == null) {
                return false;
            }

            switch (lookup.getCodeAttribute().getSimpleDataType()) {
                case STRING:

                    if (current != null) {
                        current.castValue(codeAttributeValue.toString());
                    } else {
                        ctx.getRecord()
                                .addAttribute(new StringCodeAttributeImpl(
                                        lookup.getCodeAttribute().getName(), codeAttributeValue.toString()));
                    }
                    break;
                case INTEGER:

                    if (current != null) {
                        current.castValue(codeAttributeValue);
                    } else {
                        ctx.getRecord()
                                .addAttribute(new IntegerCodeAttributeImpl(
                                        lookup.getCodeAttribute().getName(), (Long) codeAttributeValue));
                    }
                    break;
                default:
                    break;
            }

            return true;
        }

        return false;
    }

    /**
     * Loads previous version of the data and merges the input into it.
     *
     * @param ctx current context
     * @return merged record
     */
    private void ensureMergeWithPreviousVersion(UpsertRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        if (Objects.isNull(keys) || Objects.isNull(keys.getOriginKey())) {
            return;
        }

        Date recordFrom = ctx.getValidFrom();
        Date recordTo = ctx.getValidTo();
        Date asOf = nonNull(recordFrom) ? recordFrom : recordTo;
        OriginRecord prevOrigin = originComponent.loadOriginData(keys.getOriginKey().getId(), asOf, false);

        if (isNull(prevOrigin)) {
            return;
        }

        // 1st level only
        DataRecord upsert = ctx.getRecord();
        for (Attribute attr : prevOrigin.getAllAttributes()) {
            if (upsert.getAttribute(attr.getName()) != null) {
                continue;
            }
            upsert.addAttribute(attr);
        }
    }

    private EtalonKey indentifyRecordByMatchingRules(DataRecord record, String entityName, Date fromDate, Date toDate) {

        MeasurementPoint.start();
        try {
            EtalonRecordImpl etalonRecord = new EtalonRecordImpl()
                    .withDataRecord(record)
                    .withInfoSection(new EtalonRecordInfoSection()
                            .withEntityName(entityName)
                            .withEtalonKey(EtalonKey.builder()
                                    .id("NEW_ETALON")
                                    .build()));


            List<Cluster> clusters = new ArrayList<>(matchingService.construct(etalonRecord, null, null));

            if (CollectionUtils.isNotEmpty(clusters)) {
                clusters.sort(Comparator.comparing(o -> o.getMetaData().getRuleId()));
                for (Cluster cluster : clusters) {
                    String matchingHead = String.join(SearchUtils.PIPE_SEPARATOR, cluster.getData().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                    SearchRequestContext src = SearchRequestContext.builder(EntitySearchType.MATCHING, entityName)
                            .returnFields(Collections.singletonList(MatchingHeaderField.FIELD_ETALON_ID.getField()))
                            .form(FormFieldsGroup.createAndGroup()
                                    .addFormField(FormField.strictString(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField(), matchingHead))
                                    .addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getField(), null, toDate))
                                    .addFormField(FormField.range(SimpleDataType.TIMESTAMP, RecordHeaderField.FIELD_TO.getField(), fromDate, null)))
                            .routings(Collections.singletonList(matchingHead))
                            .page(0)
                            .count(1)
                            .onlyQuery(true)
                            .build();
                    SearchResultDTO searchResult = searchService.search(src);
                    if (CollectionUtils.isNotEmpty(searchResult.getHits())) {
                        String etalonKey = searchResult.getHits().get(0).getFieldValue(MatchingHeaderField.FIELD_ETALON_ID.getField())
                                .getFirstValue().toString();
                        return EtalonKey.builder()
                                .id(etalonKey)
                                .build();
                    }
                }
            }
            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
