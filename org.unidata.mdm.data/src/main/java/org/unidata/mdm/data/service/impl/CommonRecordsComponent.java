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

package org.unidata.mdm.data.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.core.type.keys.LSN;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.GetRecordIntervalRequestContext;
import org.unidata.mdm.data.context.GetRecordTimelineRequestContext;
import org.unidata.mdm.data.context.GetRecordsTimelinesRequestContext;
import org.unidata.mdm.data.context.JoinRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.dao.DataRecordsDao;
import org.unidata.mdm.data.dao.DataRecordsDao.IdSetType;
import org.unidata.mdm.data.dto.KeysJoinDTO;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordTimelinePO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;
import org.unidata.mdm.data.po.keys.RecordOriginKeyPO;
import org.unidata.mdm.data.service.RecordChangeSetProcessor;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.exception.PlatformBusinessException;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.util.IdUtils;

// @Modules Moved to commercial part
// import com.unidata.mdm.workflow.service.ext.WorkflowServiceExt;

/**
 * @author Mikhail Mikhailov
 * Common data functionality.
 */
@Component
public class CommonRecordsComponent {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRecordsComponent.class);
    /**
     * Data record DAO.
     */
    @Autowired
    private DataRecordsDao dataRecordsDao;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * WF service.
     */
// @Modules Moved to commercial part
//    @Autowired
//    private WorkflowServiceExt workflowService;
    /**
     * Record composer.
     */
    @Autowired
    private RecordComposerComponent composerComponent;
    /**
     * Apply change set processor.
     */
    @Autowired
    private RecordChangeSetProcessor changeSetProcessor;
    /**
     * Constructor.
     */
    public CommonRecordsComponent() {
        super();
    }

    /**
     * Does ensure record keys.
     *
     * @param ctx
     *            the context
     * @return keys or throws
     */
    public RecordKeys ensureKeys(RecordIdentityContext ctx) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            if (keys == null) {

                keys = identify(ctx);
                if (keys == null) {
                    final String message = "Ensure record keys failed. Record does not exist.";
                    LOGGER.warn(message, ctx);
                    throw new DataProcessingException(message, DataExceptionIds.EX_DATA_IDENTIFY_RECORD_FAILED, ctx);
                }
            }

            return keys;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads requested interval and keys (if {@link RecordIdentityContext#keys()} returns null) returning singleton timeline,
     * using slightly different and a more lightwight query.
     *
     * @param ctx the context to process
     * @return timeline, consisting of single interval or empty timeline
     */
    public Timeline<OriginRecord> loadInterval(GetRecordIntervalRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RecordTimelinePO po = null;

            // 0. Define record TL point
            Date point = Objects.isNull(ctx.getForDate()) ? new Date(System.currentTimeMillis()) : ctx.getForDate();

            // 1. Resolve or use keys
            RecordKeys keys = ctx.keys();

            // 2. Load TL
            if (Objects.nonNull(keys) || ctx.isEtalonRecordKey()) {
                po = loadIntervalByEtalonId(ctx, point);
                if (Objects.isNull(keys) && Objects.nonNull(po)) {
                    keys = composerComponent.toRecordKeys(po.getKeys(), etalonKeyPredicate());
                }
            } else if (ctx.isOriginExternalId()) {
                po = loadIntervalByExternalId(ctx, point);
                if (Objects.isNull(keys) && Objects.nonNull(po)) {
                    keys = composerComponent.toRecordKeys(po.getKeys(), externalIdPredicate(ctx.getExternalId(), ctx.getSourceSystem()));
                }
            } else if (ctx.isLsnKey()) {
                po = loadIntervalByLSN(ctx, point);
                if (Objects.isNull(keys) && Objects.nonNull(po)) {
                    keys = composerComponent.toRecordKeys(po.getKeys(), lsnPredicate());
                }
            }

            // 3. Transform PO objects and build the timeline.
            Timeline<OriginRecord> timeline = composerComponent.toRecordTimeline(keys, po == null ? null : po.getVistory());

            // 4. Select and postprocess the interval.
            timeline = timeline.reduceAsOf(point);

            if (!ctx.isSkipCalculations()) {
                for (TimeInterval<OriginRecord> interval : timeline) {

                    List<CalculableHolder<OriginRecord>> calculables = interval.toList();

                    interval.setActive(composerComponent.isActive(calculables));
                    interval.setPending(composerComponent.isPending(calculables));

                    if (ctx.isFetchData()) {
                        interval.setCalculationResult(composerComponent.toEtalon(keys, calculables, interval.getValidFrom(),
                                interval.getValidTo(), ctx.isIncludeInactive(), ctx.isIncludeWinners()));
                    }
                }
            }

            ctx.keys(keys);
            return timeline;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Loads timeline with data.
     *
     * @param ctx
     *            the context
     * @return timeline
     */
    public Timeline<OriginRecord> loadTimeline(GetRecordTimelineRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RecordTimelinePO po = null;

            // 1. Resolve or use keys
            RecordKeys keys = ctx.keys();
            boolean includeDrafts = ctx.isIncludeDrafts() || SecurityUtils.isAdminUser();

            // 2. Load TL
            if (Objects.nonNull(keys)) {
                po = dataRecordsDao.loadTimeline(UUID.fromString(keys.getEtalonKey().getId()), false, ctx.isFetchData(),
                        includeDrafts);
            } else if (ctx.isEtalonRecordKey()) {
                po = dataRecordsDao.loadTimeline(UUID.fromString(ctx.getEtalonKey()), true, ctx.isFetchData(), includeDrafts);
                if (Objects.nonNull(po)) {
                    keys = composerComponent.toRecordKeys(po.getKeys(), etalonKeyPredicate());
                }
            } else if (ctx.isOriginExternalId()) {
                ExternalId id = ExternalId.of(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem());
                po = dataRecordsDao.loadTimeline(id, true, ctx.isFetchData(), includeDrafts);
                if (Objects.nonNull(po)) {
                    keys = composerComponent.toRecordKeys(po.getKeys(),
                            externalIdPredicate(ctx.getExternalId(), ctx.getSourceSystem()));
                }
            } else if (ctx.isEnrichmentKey()) {
                po = dataRecordsDao.loadTimeline(ctx.getExternalIdAsObject(), true, ctx.isFetchData(), includeDrafts);
                if (Objects.nonNull(po)) {
                    keys = composerComponent.toRecordKeys(po.getKeys(),
                            enrichmentPredicate(ctx.getExternalId(), ctx.getSourceSystem()));
                }
            } else if (ctx.isLsnKey()) {
                po = dataRecordsDao.loadTimeline(ctx.getLsnAsObject(), true, ctx.isFetchData(), includeDrafts);
                if (Objects.nonNull(po)) {
                    keys = composerComponent.toRecordKeys(po.getKeys(), lsnPredicate());
                }
            }

            // 3. Transform PO objects and build the timeline.
            Timeline<OriginRecord> timeline = composerComponent.toRecordTimeline(keys, po == null ? null : po.getVistory());

            // 4. Possibly reduce TL by given boundaries.
            // Maybe a separate, more efficient request will be written later on.
            if (Objects.nonNull(ctx.getForDatesFrame())) {
                timeline = timeline.reduceBy(ctx.getForDatesFrame().getLeft(), ctx.getForDatesFrame().getRight());
            } else if (Objects.nonNull(ctx.getForDate())) {
                timeline = timeline.reduceAsOf(ctx.getForDate());
            }

            // 5. Calc suff, if not disabled
            RecordKeys rk = timeline.getKeys();
            if (!ctx.isSkipCalculations()) {
                timeline.forEach(ti -> {

                    List<CalculableHolder<OriginRecord>> calculables = ti.toList();

                    ti.setActive(composerComponent.isActive(calculables));
                    ti.setPending(composerComponent.isPending(calculables));

                    if (ctx.isFetchData()) {
                        ti.setCalculationResult(composerComponent.toEtalon(rk, calculables, ti.getValidFrom(),
                                ti.getValidTo(), true, false));
                    }
                });
            }

            return timeline;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Load several timelines at once.
     *
     * @param ctx
     *            the context
     * @return timelines
     */
    public List<Timeline<OriginRecord>> loadTimelines(GetRecordsTimelinesRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Collect keys
            List<String> recordEtalonIds = null;
            Map<String, RecordKeys> state = new HashMap<>();
            if (CollectionUtils.isNotEmpty(ctx.getEtalonKeys())) {
                recordEtalonIds = ctx.getEtalonKeys();
            } else {
                Collection<RecordKeys> keys = ctx.keys();
                if (CollectionUtils.isNotEmpty(keys)) {
                    recordEtalonIds = keys.stream().map(k -> {
                        state.put(k.getEtalonKey().getId(), k);
                        return k;
                    }).map(RecordKeys::getEtalonKey).map(RecordEtalonKey::getId).collect(Collectors.toList());
                }
            }

            if (CollectionUtils.isEmpty(recordEtalonIds)) {
                throw new DataProcessingException("Records timeline: no identity.",
                        DataExceptionIds.EX_DATA_TIMELINE_MASS_KEYS_NO_IDENTITY);
            }

            // 2. Set fields
            Map<String, RecordTimelinePO> tls = dataRecordsDao.loadTimelines(recordEtalonIds,
                    MapUtils.isEmpty(state), ctx.isFetchData(),
                        ctx.isIncludeDrafts() || SecurityUtils.isAdminUser());

            List<Timeline<OriginRecord>> result = new ArrayList<>();
            result.addAll(tls.entrySet().stream().map(en -> {

                final RecordKeys keys;
                if (Objects.nonNull(en.getValue().getKeys())) {
                    keys = composerComponent.toRecordKeys(en.getValue().getKeys(), etalonKeyPredicate());
                } else {
                    keys = state.get(en.getKey());
                }

                Timeline<OriginRecord> timeline
                    = composerComponent.toRecordTimeline(keys, en.getValue() == null ? null : en.getValue().getVistory());

                // 4.1. Possibly reduce TL by given boundaries.
                // Maybe a separate, more efficient request will be written
                // later on.
                if (Objects.nonNull(ctx.getForDatesFrame())) {
                    timeline = timeline.reduceBy(ctx.getForDatesFrame().getLeft(), ctx.getForDatesFrame().getRight());
                } else if (Objects.nonNull(ctx.getForDate())) {
                    timeline = timeline.reduceAsOf(ctx.getForDate());
                }

                // 4.2 Calc suff, if not disabled
                if (!ctx.isSkipCalculations()) {
                    timeline.forEach(ti -> {

                        List<CalculableHolder<OriginRecord>> versions = ti.toList();

                        ti.setActive(composerComponent.isActive(versions));
                        ti.setPending(composerComponent.isPending(versions));

                        if (ctx.isFetchData()) {
                            ti.setCalculationResult(composerComponent.toEtalon(keys, versions, ti.getValidFrom(),
                                    ti.getValidTo(), true, false));
                        }
                    });
                }

                return timeline;
            }).filter(tl -> !tl.isEmpty()).collect(Collectors.toList()));

            return result;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Identifies a record returning full key.
     *
     * @param key
     *            known origi key
     * @return full key
     */
    public RecordKeys identify(RecordOriginKey key) {

        if (key == null
         || key.getExternalId() == null
         || key.getEntityName() == null
         || key.getSourceSystem() == null) {
            return null;
        }

        RecordKeysPO po = dataRecordsDao.loadRecordKeysByExternalId(key.getExternalId(), key.getSourceSystem(),
                key.getEntityName());

        return composerComponent.toRecordKeys(po, externalIdPredicate(key.getExternalId(), key.getSourceSystem()));
    }

    /**
     * Identifies a record returning full key.
     *
     * @param key
     *            known origi key
     * @return full key
     */
    public RecordKeys identify(LSN key) {

        if (key == null || key.getLsn() <= 0) {
            return null;
        }

        RecordKeysPO po = dataRecordsDao.loadRecordKeysByLSN(key.getShard(), key.getLsn());
        return composerComponent.toRecordKeys(po, lsnPredicate());
    }

    /**
     * Identifies a record returning full key.
     *
     * @param key
     *            known etalon key
     * @return full key
     */
    public RecordKeys identify(RecordEtalonKey key) {

        if (key == null || key.getId() == null) {
            return null;
        }

        RecordKeysPO po = dataRecordsDao.loadRecordKeysByEtalonId(UUID.fromString(key.getId()));
        return composerComponent.toRecordKeys(po, etalonKeyPredicate());
    }

    /**
     * Identifies a data record.
     *
     * @param ctx
     *            the context
     * @return pair of keys
     */
    public RecordKeys identify(RecordIdentityContext ctx) {

        RecordKeys keys = null;
        if (ctx.isOriginExternalId()) {
            RecordKeysPO po = dataRecordsDao.loadRecordKeysByExternalId(ctx.getExternalId(), ctx.getSourceSystem(),
                    ctx.getEntityName());
            keys = composerComponent.toRecordKeys(po, externalIdPredicate(ctx.getExternalId(), ctx.getSourceSystem()));
        }

        if (keys == null && ctx.isEtalonRecordKey()) {
            RecordKeysPO po = dataRecordsDao.loadRecordKeysByEtalonId(UUID.fromString(ctx.getEtalonKey()));
            keys = composerComponent.toRecordKeys(po, etalonKeyPredicate());
        }

        if (keys == null && ctx.isEnrichmentKey()) {
            RecordKeysPO po = dataRecordsDao.loadRecordKeysByEtalonId(UUID.fromString(ctx.getEtalonKey()));
            keys = composerComponent.toRecordKeys(po, enrichmentPredicate(ctx.getExternalId(), ctx.getSourceSystem()));
        }

        if (keys == null && ctx.isLsnKey()) {
            RecordKeysPO po = dataRecordsDao.loadRecordKeysByLSN(ctx.getShard(), ctx.getLsn());
            keys = composerComponent.toRecordKeys(po, lsnPredicate());
        }

        ctx.keys(keys);
        return keys;
    }

    /**
     * Mass identify.
     *
     * @param ctxts
     *            the contexts
     * @return map of contexts and keys
     */
    public <T extends RecordIdentityContext> Map<T, RecordKeys> identify(List<T> ctxts) {

        if (CollectionUtils.isEmpty(ctxts)) {
            return Collections.emptyMap();
        }

        // Init state
        MassIdentityState<T> state = new MassIdentityState<>(ctxts.size());
        for (T ctx : ctxts) {
            addToState(ctx, state);
        }

        if (state.stateIsEmpty()) {
            return Collections.emptyMap();
        }

        Map<Object, RecordKeysPO> result = dataRecordsDao.loadRecordKeys(state.getInput());
        if (MapUtils.isEmpty(result)) {
            return Collections.emptyMap();
        }

        Map<T, RecordKeys> output = new IdentityHashMap<>(ctxts.size());
        for (Entry<Object, RecordKeysPO> entry : result.entrySet()) {

            T identified = getFromState(state, entry.getKey(), entry.getValue());
            if (Objects.isNull(identified)) {
                continue;
            }

            output.put(identified, identified.keys());
        }

        return output;
    }

    public BiPredicate<RecordKeysPO, RecordOriginKeyPO> etalonKeyPredicate() {
        return (po, okpo) -> StringUtils.equals(okpo.getSourceSystem(), metaModelService.getAdminSourceSystem().getName())
                && okpo.getInitialOwner().equals(UUID.fromString(po.getId())) && !okpo.isEnrichment();
    }

    public BiPredicate<RecordKeysPO, RecordOriginKeyPO> enrichmentPredicate(String externalId, String sourceSystem) {
        return (po, okpo) -> StringUtils.equals(externalId, okpo.getExternalId())
                && StringUtils.equals(sourceSystem, okpo.getSourceSystem()) && okpo.isEnrichment();
    }

    public BiPredicate<RecordKeysPO, RecordOriginKeyPO> externalIdPredicate(String externalId, String sourceSystem) {
        return (po, okpo) -> StringUtils.equals(externalId, okpo.getExternalId())
                && StringUtils.equals(sourceSystem, okpo.getSourceSystem());
    }

    public BiPredicate<RecordKeysPO, RecordOriginKeyPO> lsnPredicate() {
        return etalonKeyPredicate();
    }

    /**
     * Links DAO response to saved sate.
     *
     * @param state
     *            state
     * @param key
     *            the key
     * @param value
     *            the value
     * @return identified context or null
     */
    private <T extends RecordIdentityContext> T getFromState(MassIdentityState<T> state, Object key,
            RecordKeysPO value) {

        Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>> saved = null;
        for (IdSetType type : IdSetType.values()) {

            switch (type) {
            case ETALON_ID:
                saved = state.getEtalonState(key);
                break;
            case EXTERNAL_ID:
                saved = state.getExternalIdState(key);
                break;
            case LSN:
                saved = state.getLSNState(key);
                break;
            default:
                break;
            }

            RecordKeys keys = Objects.nonNull(saved) ? composerComponent.toRecordKeys(value, saved.getValue()) : null;
            if (Objects.nonNull(keys)) {
                saved.getKey().keys(keys);
                return saved.getKey();
            }
        }

        return null;
    }

    /**
     * Sets the context identity to {@linkplain MassIdentityState}.
     *
     * @param ctx
     *            the context to set
     * @param state
     *            the state to add to
     */
    private <T extends RecordIdentityContext> void addToState(T ctx, MassIdentityState<T> state) {

        // Etalon id
        if (ctx.isEtalonRecordKey()) {
            state.putEtalonState(ctx.getEtalonKey(), Pair.of(ctx, etalonKeyPredicate()));
            // Ext id
        } else if (ctx.isOriginExternalId()) {
            ExternalId id = ExternalId.of(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem());
            state.putExternalIdState(id, Pair.of(ctx, externalIdPredicate(ctx.getExternalId(), ctx.getSourceSystem())));
            // LSN
        } else if (ctx.isLsnKey()) {
            LSN lsn = LSN.of(ctx.getShard(), ctx.getLsn());
            state.putLSNState(lsn, Pair.of(ctx, lsnPredicate()));
        }
    }

    private RecordTimelinePO loadIntervalByEtalonId(GetRecordIntervalRequestContext ctx, Date point) {

        RecordKeys keys = ctx.keys();
        String etalonId = Objects.nonNull(keys) ? keys.getEtalonKey().getId() : ctx.getEtalonKey();

        if (Objects.isNull(etalonId)) {
            return null;
        }

        RecordTimelinePO po = null;
        if (Objects.nonNull(ctx.getForOperationId())) {
            po = dataRecordsDao.loadVersionsByEtalonIdAndOperationId(
                    UUID.fromString(etalonId), Objects.isNull(keys) && ctx.isFetchKeys(), point,
                    ctx.getForOperationId(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else if (Objects.nonNull(ctx.getForLastUpdate())) {
            po = dataRecordsDao.loadVersionsByEtalonIdAndLastUpdateDate(
                    UUID.fromString(etalonId), Objects.isNull(keys) && ctx.isFetchKeys(), point,
                    ctx.getForLastUpdate(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else if (Objects.nonNull(ctx.getForUpdatesAfter())) {
            po = dataRecordsDao.loadVersionsByEtalonIdAndUpdatesAfter(
                    UUID.fromString(etalonId), Objects.isNull(keys) && ctx.isFetchKeys(), point,
                    ctx.getForUpdatesAfter(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else {
            po = dataRecordsDao.loadVersionsByEtalonId(
                    UUID.fromString(etalonId), Objects.isNull(keys) && ctx.isFetchKeys(), point,
                    ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        }

        return po;
    }

    private RecordTimelinePO loadIntervalByLSN(GetRecordIntervalRequestContext ctx, Date point) {

        RecordKeys keys = ctx.keys();
        LSN lsn = Objects.nonNull(keys) ? LSN.of(keys.getShard(), keys.getEtalonKey().getLsn()) : LSN.of(ctx.getShard(), ctx.getLsn());

        if (lsn.getLsn() <= 0) {
            return null;
        }

        RecordTimelinePO po = null;
        if (Objects.nonNull(ctx.getForOperationId())) {
            po = dataRecordsDao.loadVersionsByLSNAndOperationId(
                    lsn, Objects.isNull(keys) && ctx.isFetchKeys(), point,
                    ctx.getForOperationId(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else if (Objects.nonNull(ctx.getForLastUpdate())) {
            po = dataRecordsDao.loadVersionsByLSNAndLastUpdateDate(
                    lsn, Objects.isNull(keys) && ctx.isFetchKeys(), point,
                    ctx.getForLastUpdate(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else if (Objects.nonNull(ctx.getForUpdatesAfter())) {
            po = dataRecordsDao.loadVersionsByLSNAndUpdatesAfter(
                    lsn, Objects.isNull(keys) && ctx.isFetchKeys(), point,
                    ctx.getForUpdatesAfter(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else {
            po = dataRecordsDao.loadVersionsByLSN(
                    lsn, Objects.isNull(keys) && ctx.isFetchKeys(), point, ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        }

        return po;
    }

    private RecordTimelinePO loadIntervalByExternalId(GetRecordIntervalRequestContext ctx, Date point) {

        ExternalId externalId = ctx.getExternalIdAsObject();
        if (Objects.isNull(externalId)) {
            return null;
        }

        RecordTimelinePO po = null;
        if (Objects.nonNull(ctx.getForOperationId())) {
            po = dataRecordsDao.loadVersionsByExternalIdAndOperationId(
                    externalId, point,
                    ctx.getForOperationId(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else if (Objects.nonNull(ctx.getForLastUpdate())) {
            po = dataRecordsDao.loadVersionsByExternalIdAndLastUpdateDate(
                    externalId, point,
                    ctx.getForLastUpdate(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else if (Objects.nonNull(ctx.getForUpdatesAfter())) {
            po = dataRecordsDao.loadVersionsByExternalIdAndUpdatesAfter(
                    externalId, point,
                    ctx.getForUpdatesAfter(), ctx.isIncludeDrafts(),
                    SecurityUtils.getCurrentUserName());
        } else {
            po = dataRecordsDao.loadVersionsByExternalId(externalId, point, ctx.isIncludeDrafts(), SecurityUtils.getCurrentUserName());
        }

        return po;
    }

    /**
     * Changes etalon state.
     *
     * @param etalonId
     *            the etalon id
     * @param state
     *            the state
     * @return true, if successful, false otherwise
     */
    @Transactional
    public boolean changeApproval(String etalonId, ApprovalState state) {
        return dataRecordsDao.changeEtalonApproval(etalonId, state);
    }

    /**
     * Does etalon draft cleanup.
     *
     * @param etalonId
     *            the etalon id
     * @return
     */
    public boolean cleanupEtalonStateDrafts(String etalonId) {
        return dataRecordsDao.cleanupEtalonStateDrafts(etalonId);
    }

    /**
     * Puts draft state for an etalon.
     *
     * @param etalonId
     * @param status
     * @param user
     * @return
     */
    public boolean putEtalonStateDraft(String etalonId, RecordStatus status, String user) {
        return dataRecordsDao.putEtalonStateDraft(etalonId, status, user);
    }

    /**
     * Joins a new external id to an existing etalon key.
     *
     * @param ctx
     *            the context
     * @return result
     */
    public KeysJoinDTO join(JoinRequestContext ctx) {

        // 1. Check
        RecordEtalonKey etalonKey = RecordEtalonKey.builder().id(ctx.getEtalonKey()).build();
        RecordKeys existingByEtalonId = identify(etalonKey);
        if (Objects.isNull(existingByEtalonId)) {
            final String message = "External ID can not be joined. Etalon ID not found.";
            LOGGER.warn(message);
            throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_JOIN_ETALON_ID_NOT_FOUND);
        }

        if (StringUtils.isBlank(ctx.getExternalId()) || StringUtils.isBlank(ctx.getSourceSystem())
                || StringUtils.isBlank(ctx.getEntityName())) {
            final String message = "External ID can not be joined. Invalid input.";
            LOGGER.warn(message);
            throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_JOIN_INVALID_INPUT);
        }

        if (!StringUtils.equals(existingByEtalonId.getEntityName(), ctx.getEntityName())) {
            final String message = "External ID can not be joined. Target register and the supplied one do not match.";
            LOGGER.warn(message);
            throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_JOIN_TARGET_REGISTER_DONT_MATCH);
        }

        for (RecordOriginKey ok : existingByEtalonId.getSupplementaryKeysWithoutEnrichments()) {
            if (ok.getEntityName().equals(ctx.getEntityName()) && ok.getExternalId().equals(ctx.getExternalId())
                    && ok.getSourceSystem().equals(ctx.getSourceSystem())) {
                final String message = "External ID can not be joined. The key is already defined for the target.";
                LOGGER.warn(message);
                throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_JOIN_KEY_IS_ALREADY_DEFINED_IN_TARGET);
            }
        }

        RecordKeys existingByExternalId = identify(RecordOriginKey.builder()
                .entityName(ctx.getEntityName())
                .externalId(ctx.getExternalId())
                .sourceSystem(ctx.getSourceSystem())
                .build());

        if (Objects.nonNull(existingByExternalId)) {
            final String message = "External ID can not be joined. The key is already used by another record.";
            LOGGER.warn(message);
            throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_JOIN_KEY_IS_ALREADY_USED_BY_ANOTHER);
        }

        // 2. Join
        RecordOriginPO result = new RecordOriginPO();
        result.setId(IdUtils.v1String());
        result.setEtalonId(existingByEtalonId.getEtalonKey().getId());
        result.setShard(existingByEtalonId.getShard());
        result.setInitialOwner(UUID.fromString(existingByEtalonId.getEtalonKey().getId()));
        result.setExternalId(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem());
        result.setEnrichment(false);
        result.setStatus(RecordStatus.ACTIVE);
        result.setCreateDate(new Date(System.currentTimeMillis()));
        result.setCreatedBy(SecurityUtils.getCurrentUserName());

        RecordExternalKeysPO rekpo = new RecordExternalKeysPO();
        rekpo.setEtalonId(UUID.fromString(existingByEtalonId.getEtalonKey().getId()));
        rekpo.setExternalId(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem());

        RecordUpsertChangeSet set = new RecordUpsertChangeSet();
        set.getOriginRecordInsertPOs().add(result);
        set.getExternalKeysInsertPOs().add(rekpo);

        changeSetProcessor.apply(set);

        // 3. Re-Fetch keys
        RecordKeys refetched = identify(etalonKey);

        return new KeysJoinDTO(
                Objects.nonNull(
                        refetched.findByExternalId(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem())),
                refetched);
    }

    /**
     * Tells, whether all origins of the record are already inactive.
     *
     * @param keys
     *            the keys to check
     * @return true, if all inactive, false otherwise
     */
    public boolean allOriginsAlreadyInactive(RecordKeys keys) {
        return keys.getSupplementaryKeys().stream()
                .allMatch(supplementaryKey -> RecordStatus.INACTIVE == supplementaryKey.getStatus()
                        || supplementaryKey.getRevision() == 0
                        || supplementaryKey.getId().equals(keys.getOriginKey().getId()));
    }

    /**
     * @author Mikhail Mikhailov Data, collected during mass identification
     *         process.
     */
    @NotThreadSafe
    private class MassIdentityState<T extends RecordIdentityContext> {
        /**
         * Input buffer.
         */
        private final Map<IdSetType, List<Object>> input = new EnumMap<>(IdSetType.class);
        /**
         * Max response size.
         */
        private final int maxSize;
        /**
         * Etalon keys.
         */
        private Map<String, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>>> interimEtalons = null;
        /**
         * External ids.
         */
        private Map<ExternalId, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>>> interimExternalIds = null;
        /**
         * LSNs.
         */
        private Map<LSN, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>>> interimLSNs = null;

        /**
         * Constructor. Normally, only one type of key (identification type) is
         * set for batch operations, so only one interim map will be
         * instantiated.
         *
         * @param maxSize
         *            the maximum size of output buffer.
         */
        public MassIdentityState(int maxSize) {
            super();
            this.maxSize = maxSize;
        }

        /**
         * @return the input
         */
        public Map<IdSetType, List<Object>> getInput() {
            return input;
        }

        /**
         * No state collected.
         *
         * @return true, if so, false otherwise
         */
        public boolean stateIsEmpty() {
            return MapUtils.isEmpty(interimEtalons) && MapUtils.isEmpty(interimExternalIds)
                    && MapUtils.isEmpty(interimLSNs);
        }

        /**
         * @return the interimEtalons
         */
        private Map<String, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>>> getInterimEtalons() {
            if (interimEtalons == null) {
                interimEtalons = new IdentityHashMap<>(maxSize);
            }
            return interimEtalons;
        }

        /**
         * @return the interimExternalIds
         */
        private Map<ExternalId, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>>> getInterimExternalIds() {
            if (interimExternalIds == null) {
                interimExternalIds = new IdentityHashMap<>(maxSize);
            }
            return interimExternalIds;
        }

        /**
         * @return the interimGSNs
         */
        private Map<LSN, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>>> getInterimLSNs() {
            if (interimLSNs == null) {
                interimLSNs = new IdentityHashMap<>(maxSize);
            }
            return interimLSNs;
        }

        /**
         * Gets the saved state for etalon id.
         *
         * @param key
         *            the id
         * @return state or null
         */
        public Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>> getEtalonState(Object key) {
            if (MapUtils.isEmpty(interimEtalons)) {
                return null;
            }
            return interimEtalons.get(key);
        }

        /**
         * Gets the saved state for external id.
         *
         * @param key
         *            the id
         * @return state or null
         */
        public Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>> getExternalIdState(Object key) {
            if (MapUtils.isEmpty(interimExternalIds)) {
                return null;
            }
            return interimExternalIds.get(key);
        }

        /**
         * Gets the saved state for LSN.
         *
         * @param key
         *            the id
         * @return state or null
         */
        public Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>> getLSNState(Object key) {
            if (MapUtils.isEmpty(interimLSNs)) {
                return null;
            }
            return interimLSNs.get(key);
        }

        /**
         * Puts etalon state.
         *
         * @param etalonId
         *            the etalon id.
         * @param state
         *            the query state
         */
        public void putEtalonState(String etalonId, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>> state) {
            getInterimEtalons().put(etalonId, state);
            getInput().computeIfAbsent(IdSetType.ETALON_ID, key -> new ArrayList<>()).add(etalonId);
        }

        /**
         * Puts external id state.
         *
         * @param externalId
         *            the external id
         * @param state
         *            the state
         */
        public void putExternalIdState(ExternalId eid, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>> state) {
            getInterimExternalIds().put(eid, state);
            getInput().computeIfAbsent(IdSetType.EXTERNAL_ID, key -> new ArrayList<>()).add(eid);
        }

        /**
         * Puts GSN state.
         *
         * @param externalId
         *            the external id
         * @param state
         *            the state
         */
        public void putLSNState(LSN lsn, Pair<T, BiPredicate<RecordKeysPO, RecordOriginKeyPO>> state) {
            getInterimLSNs().put(lsn, state);
            getInput().computeIfAbsent(IdSetType.LSN, key -> new ArrayList<>()).add(lsn);
        }
    }
}
