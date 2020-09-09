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

package com.unidata.mdm.backend.service.data.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.JoinRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.dto.KeysJoinDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.Calculable;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.TimeInterval;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;
import com.unidata.mdm.backend.common.types.Timeline;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.dao.DataRecordsDao.IdSetType;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.RecordKeysPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.driver.EtalonCompositionDriverType;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowServiceExt;
import com.unidata.mdm.backend.util.IdUtils;

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
     * Origin vistory DAO.
     */
    @Autowired
    private OriginsVistoryDao originsVistoryDao;

    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * WF service instance.
     */
    @Autowired
    private WorkflowServiceExt workflowService;

    /**
     * Etalon composer.
     */
    @Autowired
    private EtalonComposer etalonComposer;

    /**
     * Etalon component.
     */
    @Autowired
    private EtalonRecordsComponent etalonRecordsComponent;

    /**
     * Constructor.
     */
    public CommonRecordsComponent() {
        super();
    }

    /**
     * Identifies a record returning full key.
     * @param key known origi key
     * @return full key
     */
    public RecordKeys identify(OriginKey key) {

        if (key == null) {
            return null;
        }

        RecordKeys keys = null;
        if (key.getExternalId() != null
         && key.getEntityName() != null
         && key.getSourceSystem() != null) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByExternalId(key.getExternalId(),
                    key.getSourceSystem(), key.getEntityName());

            Predicate<RecordKeysPO> byExternalId = po ->
                    StringUtils.equals(key.getExternalId(), po.getOriginExternalId())
                 && StringUtils.equals(key.getEntityName(), po.getEtalonName())
                 && StringUtils.equals(key.getSourceSystem(), po.getOriginSourceSystem());

            keys = pos2keys(pos, byExternalId);
        }

        if (keys == null && key.getId() != null) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByOriginId(key.getId());

            Predicate<RecordKeysPO> byOriginId = po -> StringUtils.equals(po.getOriginId(), key.getId());

            keys = pos2keys(pos, byOriginId);
        }

        return keys;
    }

    /**
     * Identifies a record returning full key.
     * @param key known etalon key
     * @return full key
     */
    public RecordKeys identify(EtalonKey key) {

        if (key == null) {
            return null;
        }

        RecordKeys keys = null;
        if (key.getId() != null) {
            String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByEtalonId(key.getId());

            Predicate<RecordKeysPO> bySource = po -> StringUtils.equals(po.getOriginSourceSystem(), adminSourceSystem)
                    && (po.isEnriched() == null || !po.isEnriched());

            keys = pos2keys(pos, bySource);
        }

        return keys;
    }

    /**
     * Identifies a record returning full key.
     * @param key known etalon key
     * @return full key
     */
    public RecordKeys identify(long gsn) {

        String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
        List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByGSN(gsn);

        Predicate<RecordKeysPO> bySource = po -> StringUtils.equals(po.getOriginSourceSystem(), adminSourceSystem)
                && (po.isEnriched() == null || !po.isEnriched());

        return pos2keys(pos, bySource);
    }

    /**
     * Identifies a data record.
     * @param ctx the context
     * @return pair of keys
     */
    public RecordKeys identify(RecordIdentityContext ctx) {

        RecordKeys keys = null;
        if (ctx.isOriginExternalId()) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByExternalId(ctx.getExternalId(),
                    ctx.getSourceSystem(), ctx.getEntityName());

            Predicate<RecordKeysPO> byExternalId = po ->
                    StringUtils.equals(ctx.getExternalId(), po.getOriginExternalId())
                            && StringUtils.equals(ctx.getEntityName(), po.getEtalonName())
                            && StringUtils.equals(ctx.getSourceSystem(), po.getOriginSourceSystem());

            keys = pos2keys(pos, byExternalId);
        }

        if (keys == null && ctx.isOriginRecordKey()) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByOriginId(ctx.getOriginKey());

            Predicate<RecordKeysPO> byOriginId = po -> StringUtils.equals(po.getOriginId(), ctx.getOriginKey());

            keys = pos2keys(pos, byOriginId);

        }

        if (keys == null && ctx.isEtalonRecordKey()) {
            String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByEtalonId(ctx.getEtalonKey());

            Predicate<RecordKeysPO> bySource = po -> StringUtils.equals(po.getOriginSourceSystem(), adminSourceSystem)
                    && (po.isEnriched() == null || !po.isEnriched());

            keys = pos2keys(pos, bySource);
        }

        if (keys == null && ctx.isEnrichmentKey()) {

            List<RecordKeysPO> pos = dataRecordsDao.loadRecordKeysByEtalonId(ctx.getEtalonKey());

            Predicate<RecordKeysPO> byExternalId = po ->
                    StringUtils.equals(ctx.getExternalId(), po.getOriginExternalId())
                            && StringUtils.equals(ctx.getEntityName(), po.getEtalonName())
                            && StringUtils.equals(ctx.getSourceSystem(), po.getOriginSourceSystem())
                            && (po.isEnriched() != null && po.isEnriched());

            keys = pos2keys(pos, byExternalId);
        }

        if (keys == null && ctx.getGsn() != null) {
            keys = identify(ctx.getGsn());
        }

        if (keys != null) {
            ((CommonRequestContext) ctx).putToStorage(ctx.keysId(), keys);
        }

        return keys;
    }

    /**
     * Mass identify.
     * @param ctxts the contexts
     * @return map of contexts and keys
     */
    public Map<RecordIdentityContext, RecordKeys> identify(List<? extends RecordIdentityContext> ctxts) {

        if (CollectionUtils.isEmpty(ctxts)) {
            return Collections.emptyMap();
        }

        // Init state
        MassIdentityState state = new MassIdentityState(ctxts.size());
        for (RecordIdentityContext ctx : ctxts) {
            addToState(ctx, state);
        }

        if (state.stateIsEmpty()) {
            return Collections.emptyMap();
        }

        Map<Object, List<RecordKeysPO>> result = dataRecordsDao.loadRecordKeys(state.getInput());
        if (MapUtils.isEmpty(result)) {
            return Collections.emptyMap();
        }

        Map<RecordIdentityContext, RecordKeys> output = new IdentityHashMap<>(ctxts.size());
        for (Entry<Object, List<RecordKeysPO>> entry : result.entrySet()) {

            RecordIdentityContext identified = getFromState(state, entry.getKey(), entry.getValue());
            if (Objects.isNull(identified)) {
                continue;
            }

            output.put(identified, identified.keys());
        }

        return output;
    }
    /**
     * Loads timeline with data.
     * @param keys record keys
     * @param pos interval data
     * @param loadDrafts load draft versions or not
     * @return intervals
     */
    private List<TimeInterval<OriginRecord>> loadTimelineIntervalsWithData(
            RecordKeys keys, List<TimeIntervalPO> pos,  boolean loadDrafts) {

        MeasurementPoint.start();
        try {

            if (CollectionUtils.isEmpty(pos)) {
                return Collections.emptyList();
            }

            return pos.stream()
                .filter(Objects::nonNull)
                .map(po -> {

                    Date asOf = po.getFrom() == null ? po.getTo() : po.getFrom();
                    final Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> data =
                            etalonRecordsComponent.loadEtalonDataFull(keys.getEtalonKey().getId(),
                                    asOf, null, null, null, true, loadDrafts);

                    List<CalculableHolder<OriginRecord>> contributors = data == null ? Collections.emptyList() : data.getValue();
                    EtalonRecord result = data == null ? null : data.getKey();

                    boolean isActive = etalonComposer.hasActive(EtalonCompositionDriverType.BVR, contributors);
                    return TimeInterval.<OriginRecord>of(contributors, result, po.getFrom(), po.getTo(), isActive);
                })
                .collect(Collectors.toList());

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Loads tim intervals with info.
     * @param intervals interval POs
     * @return converted time intervals
     */
    private List<TimeInterval<TimeIntervalContributorInfo>> loadTimelineIntervalsWithInfo(List<TimeIntervalPO> intervals) {
        MeasurementPoint.start();
        try {

            if (CollectionUtils.isEmpty(intervals)) {
                return Collections.emptyList();
            }

            return intervals.stream()
                .filter(Objects::nonNull)
                .map(po -> {

                    List<CalculableHolder<TimeIntervalContributorInfo>> contributors =
                            po.getContributors() == null || po.getContributors().length == 0
                            ? Collections.emptyList()
                            : Arrays.stream(po.getContributors())
                                .map(contributor -> CalculableHolder.<TimeIntervalContributorInfo>of(
                                    new TimeIntervalContributorInfo()
                                            .withApprovalState(contributor.getApproval())
                                            .withCreateDate(contributor.getLastUpdate())
                                            .withCreatedBy(contributor.getOwner())
                                            .withOperationType(contributor.getOperationType())
                                            .withOriginId(contributor.getOriginId())
                                            .withRevision(contributor.getRevision())
                                            .withSourceSystem(contributor.getSourceSystem())
                                            .withStatus(contributor.getStatus())))
                                .collect(Collectors.toList());

                    boolean isActive = etalonComposer.hasActive(EtalonCompositionDriverType.BVR, contributors);
                    return TimeInterval.<TimeIntervalContributorInfo>of(contributors, po.getFrom(), po.getTo(), isActive);
                })
                .collect(Collectors.toList());

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Loads (calculates) contributing records time line for an etalon ID.
     * @param ctx the ctx
     * @return time line
     */
    public<T extends Calculable> Timeline<T> loadTimeline(GetRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            if (keys == null) {
                keys = identify(ctx);
            }

            if (keys == null) {
                String message = "Record not found.";
                LOGGER.warn(message);
                throw new BusinessException(message, ExceptionId.EX_DATA_TIMELINE_NOT_EXIST);
            }

            boolean hasEditTasks = false;
            if (ctx.isTasks() && workflowService != null) {
                hasEditTasks = workflowService.hasEditTasks(keys.getEtalonKey().getId());
            }

            boolean loadDrafts = ctx.isTasks() && (SecurityUtils.isAdminUser() || hasEditTasks) || ctx.isIncludeDrafts();
            List<TimeIntervalPO> intervals
                    = originsVistoryDao.loadContributingRecordsTimeline(
                    keys.getEtalonKey().getId(),
                    keys.getEntityName(),
                    ctx.isTasks() && (SecurityUtils.isAdminUser() || hasEditTasks) || ctx.isIncludeDrafts());

            if (ctx.isFetchTimelineData()) {
                return Timeline.of(keys, loadTimelineIntervalsWithData(keys, intervals, loadDrafts));
            }

            return Timeline.of(keys, loadTimelineIntervalsWithInfo(intervals));
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Links DAO response to saved sate.
     * @param state state
     * @param key the key
     * @param value the value
     * @return identified context or null
     */
    private RecordIdentityContext getFromState(MassIdentityState state, Object key, List<RecordKeysPO> value) {

        Pair<RecordIdentityContext, Predicate<RecordKeysPO>> saved = null;
        for (IdSetType type : IdSetType.values()) {

            switch (type) {
            case ETALON_ID:
                saved = state.getEtalonState(key);
                break;
            case EXTERNAL_ID:
                saved = state.getExternalIdState(key);
                break;
            case GSN:
                saved = state.getGsnState(key);
                break;
            case ORIGIN_ID:
                saved = state.getOriginState(key);
                break;
            default:
                break;
            }

            RecordKeys keys = Objects.nonNull(saved) ? pos2keys(value, saved.getValue()) : null;
            if (Objects.nonNull(keys)) {
                ((CommonRequestContext) saved.getKey()).putToStorage(saved.getKey().keysId(), keys);
                return saved.getKey();
            }
        }

        return null;
    }
    /**
     * Sets the context identity to {@linkplain MassIdentityState}.
     * @param ctx the context to set
     * @param state the state to add to
     */
    private void addToState(RecordIdentityContext ctx, MassIdentityState state) {

        // Etalon id
        if (ctx.isEtalonRecordKey()) {
            Predicate<RecordKeysPO> etalonIdPredicate = po -> StringUtils.equals(po.getOriginSourceSystem(),
                    metaModelService.getAdminSourceSystem().getName())
                    && (po.isEnriched() == null || !po.isEnriched());

            state.putEtalonState(ctx.getEtalonKey(), new ImmutablePair<>(ctx, etalonIdPredicate));
        // Origin id
        } else if (ctx.isOriginRecordKey()) {
            state.putOriginState(ctx.getOriginKey(),
                    new ImmutablePair<>(ctx, po -> StringUtils.equals(po.getOriginId(), ctx.getOriginKey())));
        // Ext id
        } else if (ctx.isOriginExternalId()) {

            Predicate<RecordKeysPO> byExternalId = po ->
                    StringUtils.equals(ctx.getExternalId(), po.getOriginExternalId())
                            && StringUtils.equals(ctx.getEntityName(), po.getEtalonName())
                            && StringUtils.equals(ctx.getSourceSystem(), po.getOriginSourceSystem());

            Triple<String, String, String> id
                = new ImmutableTriple<>(ctx.getSourceSystem(), ctx.getExternalId(), ctx.getEntityName());

            state.putExternalIdState(id, new ImmutablePair<>(ctx, byExternalId));
        // GSN
        } else if (ctx.getGsn() != null) {
            Predicate<RecordKeysPO> etalonIdPredicate = po -> StringUtils.equals(po.getOriginSourceSystem(),
                    metaModelService.getAdminSourceSystem().getName())
                    && (po.isEnriched() == null || !po.isEnriched());

            state.putGsnState(ctx.getGsn(), new ImmutablePair<>(ctx, etalonIdPredicate));
        }
    }
    /**
     * Converts PO to keys object.
     * @param pos the collection of PO
     * @return key object
     */
    private RecordKeys pos2keys(List<RecordKeysPO> pos, Predicate<RecordKeysPO> mainOriginPredicate) {

        if (pos.isEmpty()) {
            return null;
        }

        RecordKeysPO anyPo = pos.get(0);
        RecordKeysPO requestedPo = pos.stream().filter(mainOriginPredicate).findAny().orElse(null);

        EtalonKey etalonKey = EtalonKey.builder()
                .id(anyPo.getEtalonId())
                .gsn(anyPo.getEtalonGsn())
                .status(anyPo.getEtalonStatus())
                .build();

        OriginKey originKey = toOriginKey(requestedPo);
        List<OriginKey> supplementaryKeys = pos.stream()
                                               .map(this::toOriginKey)
                                               .filter(Objects::nonNull)
                                               .collect(Collectors.toList());
        return RecordKeys.builder()
                         .etalonKey(etalonKey)
                         .originKey(originKey)
                         .supplementaryKeys(supplementaryKeys)
                         .entityName(anyPo.getEtalonName())
                         .etalonStatus(anyPo.getEtalonStatus())
                         .originStatus(requestedPo == null ? null : requestedPo.getOriginStatus())
                         .etalonState(anyPo.getEtalonState())
                         .published(originKey != null && originKey.hasApprovedRevisions()
                             ? true
                             : supplementaryKeys.stream().anyMatch(OriginKey::hasApprovedRevisions))
                         .build();
    }

    private OriginKey toOriginKey(RecordKeysPO po) {

        if (po == null || StringUtils.isBlank(po.getOriginId())) {
            return null;
        }

        return OriginKey.builder()
                        .id(po.getOriginId())
                        .externalId(po.getOriginExternalId())
                        .entityName(po.getOriginName())
                        .sourceSystem(po.getOriginSourceSystem())
                        .enrichment(po.isEnriched() == null ?  false : po.isEnriched())
                        .approvedRevisions(po.hasApprovedRevisions() == null ? false : po.hasApprovedRevisions())
                        .gsn(po.getOriginGsn())
                        .revision(po.getOriginRevision())
                        .status(po.getOriginStatus())
                        .build();
    }

    /**
     * Creates a system origin record.
     *
     * @param etalonId the etalon ID
     * @param entityName the entity name
     * @return new key
     */
    public OriginKey createSystemOriginRecord(String etalonId, String entityName) {

        String name = entityName;
        if (name == null) {
            EtalonRecordPO epo = dataRecordsDao.loadEtalonRecord(etalonId, false, false);
            name = epo.getName();
        }

        String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
        UpsertRequestContext uCtx = new UpsertRequestContextBuilder()
                .sourceSystem(adminSourceSystem)
                .entityName(name)
                .build();

        OriginRecordPO po = DataRecordUtils.createSystemOriginRecordPO(uCtx, etalonId);
        dataRecordsDao.upsertOriginRecords(Collections.singletonList(po), true);

        return OriginKey.builder()
                .entityName(name)
                .externalId(po.getExternalId())
                .sourceSystem(adminSourceSystem)
                .id(po.getId())
                .build();
    }

    /**
     * Changes etalon state.
     * @param etalonId the etalon id
     * @param state the state
     * @return true, if successful, false otherwise
     */
    @Transactional
    public boolean changeApproval(String etalonId, ApprovalState state) {
        return dataRecordsDao.changeEtalonApproval(etalonId, state);
    }

    /**
     * Possibly reset keys.
     * @param ctx the context
     * @param storageKey storage key
     * @param newState the new state
     * @return new keys
     */
    public RecordKeys possiblyResetApprovalState(CommonRequestContext ctx, StorageId storageKey, ApprovalState newState) {

        RecordKeys keys = ctx.getFromStorage(storageKey);
        boolean resetState = keys.getEtalonState() != newState;
        if (resetState) {

            RecordKeys newKeys = RecordKeys.builder(keys)
                    .etalonState(newState)
                    .build();

            ctx.putToStorage(storageKey, newKeys);
            return newKeys;
        }

        return keys;
    }

    /**
     * Does etalon draft cleanup.
     * @param etalonId the etalon id
     * @return
     */
    public boolean cleanupEtalonStateDrafts(String etalonId) {
        return dataRecordsDao.cleanupEtalonStateDrafts(etalonId);
    }

    /**
     * Puts draft state for an etalon.
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
     * @param ctx the context
     * @return result
     */
    public KeysJoinDTO join(JoinRequestContext ctx) {

        // 1. Check
        EtalonKey etalonKey = EtalonKey.builder().id(ctx.getEtalonKey()).build();
        RecordKeys existingByEtalonId = identify(etalonKey);
        if (Objects.isNull(existingByEtalonId)) {
            final String message = "External ID can not be joined. Etalon ID not found.";
            LOGGER.warn(message);
            throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_ETALON_ID_NOT_FOUND);
        }

        if (StringUtils.isBlank(ctx.getExternalId()) || StringUtils.isBlank(ctx.getSourceSystem()) || StringUtils.isBlank(ctx.getEntityName())) {
            final String message = "External ID can not be joined. Invalid input.";
            LOGGER.warn(message);
            throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_INVALID_INPUT);
        }

        if (!StringUtils.equals(existingByEtalonId.getEntityName(), ctx.getEntityName())) {
            final String message = "External ID can not be joined. Target register and the supplied one do not match.";
            LOGGER.warn(message);
            throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_TARGET_REGISTER_DONT_MATCH);
        }

        for (OriginKey ok : existingByEtalonId.getNotEnrichedSupplementaryKeys()) {
            if (ok.getEntityName().equals(ctx.getEntityName())
             && ok.getExternalId().equals(ctx.getExternalId())
             && ok.getSourceSystem().equals(ctx.getSourceSystem())) {
                final String message = "External ID can not be joined. The key is already defined for the target.";
                LOGGER.warn(message);
                throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_KEY_IS_ALREADY_DEFINED_IN_TARGET);
            }
        }

        RecordKeys existingByExternalId = identify(OriginKey.builder()
                .entityName(ctx.getEntityName())
                .externalId(ctx.getExternalId())
                .sourceSystem(ctx.getSourceSystem())
                .build());
        if (Objects.nonNull(existingByExternalId)) {
            final String message = "External ID can not be joined. The key is already used by another record.";
            LOGGER.warn(message);
            throw new BusinessException(message, ExceptionId.EX_DATA_JOIN_KEY_IS_ALREADY_USED_BY_ANOTHER);
        }

        // 2. Join
        OriginRecordPO result = new OriginRecordPO();
        result.setId(IdUtils.v1String());
        result.setEtalonId(existingByEtalonId.getEtalonKey().getId());
        result.setExternalId(ctx.getExternalId());
        result.setSourceSystem(ctx.getSourceSystem());
        result.setName(ctx.getEntityName());
        result.setEnrichment(false);
        result.setStatus(RecordStatus.ACTIVE);
        result.setCreateDate(new Date(System.currentTimeMillis()));
        result.setCreatedBy(SecurityUtils.getCurrentUserName());
        result.setVersion(1);

        dataRecordsDao.upsertOriginRecords(Collections.singletonList(result), true);

        // 3. Re-Fetch keys
        RecordKeys refetched = identify(etalonKey);

        return new KeysJoinDTO(
                Objects.nonNull(refetched.findByExternalId(ctx.getExternalId(), ctx.getEntityName(), ctx.getSourceSystem())),
                refetched);
    }

    /**
     * Tells, whether all origins of the record are already inactive.
     * @param keys the keys to check
     * @return true, if all inactive, false otherwise
     */
    public boolean allOriginsAlreadyInactive(RecordKeys keys) {
        return keys.getSupplementaryKeys()
                .stream()
                .allMatch(supplementaryKey -> RecordStatus.INACTIVE == supplementaryKey.getStatus() ||
                        supplementaryKey.getRevision() == 0 ||
                        supplementaryKey.getId().equals(keys.getOriginKey().getId()));
    }
    /**
     * @author Mikhail Mikhailov
     * Data, collected during mass identification process.
     */
    @NotThreadSafe
    private class MassIdentityState {
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
        private Map<String, Pair<RecordIdentityContext, Predicate<RecordKeysPO>>> interimEtalons = null;
        /**
         * Origin keys.
         */
        private Map<String, Pair<RecordIdentityContext, Predicate<RecordKeysPO>>> interimOrigins = null;
        /**
         * External ids.
         */
        private Map<Triple<String, String, String>, Pair<RecordIdentityContext, Predicate<RecordKeysPO>>> interimExternalIds = null;
        /**
         * GSNs.
         */
        private Map<Long, Pair<RecordIdentityContext, Predicate<RecordKeysPO>>> interimGSNs = null;
        /**
         * Constructor.
         * Normally, only one type of key (identification type) is set for batch operations,
         * so only one interim map will be instantiated.
         * @param maxSize the maximum size of output buffer.
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
         * @return true, if so, false otherwise
         */
        public boolean stateIsEmpty() {
            return MapUtils.isEmpty(interimEtalons)
                && MapUtils.isEmpty(interimOrigins)
                && MapUtils.isEmpty(interimExternalIds)
                && MapUtils.isEmpty(interimGSNs);
        }
        /**
         * @return the interimEtalons
         */
        private Map<String, Pair<RecordIdentityContext, Predicate<RecordKeysPO>>> getInterimEtalons() {
            if (interimEtalons == null) {
                interimEtalons = new IdentityHashMap<>(maxSize);
            }
            return interimEtalons;
        }
        /**
         * @return the interimOrigins
         */
        private Map<String, Pair<RecordIdentityContext, Predicate<RecordKeysPO>>> getInterimOrigins() {
            if (interimOrigins == null) {
                interimOrigins = new IdentityHashMap<>(maxSize);
            }
            return interimOrigins;
        }
        /**
         * @return the interimExternalIds
         */
        private Map<Triple<String, String, String>, Pair<RecordIdentityContext, Predicate<RecordKeysPO>>> getInterimExternalIds() {
            if (interimExternalIds == null) {
                interimExternalIds = new IdentityHashMap<>(maxSize);
            }
            return interimExternalIds;
        }
        /**
         * @return the interimGSNs
         */
        private Map<Long, Pair<RecordIdentityContext, Predicate<RecordKeysPO>>> getInterimGsns() {
            if (interimGSNs == null) {
                interimGSNs = new IdentityHashMap<>(maxSize);
            }
            return interimGSNs;
        }
        /**
         * Gets the saved state for etalon id.
         * @param key the id
         * @return state or null
         */
        public Pair<RecordIdentityContext, Predicate<RecordKeysPO>> getEtalonState(Object key) {
            if (MapUtils.isEmpty(interimEtalons)) {
                return null;
            }
            return interimEtalons.get(key);
        }
        /**
         * Gets the saved state for origin id.
         * @param key the id
         * @return state or null
         */
        public Pair<RecordIdentityContext, Predicate<RecordKeysPO>> getOriginState(Object key) {
            if (MapUtils.isEmpty(interimOrigins)) {
                return null;
            }
            return interimOrigins.get(key);
        }
        /**
         * Gets the saved state for external id.
         * @param key the id
         * @return state or null
         */
        public Pair<RecordIdentityContext, Predicate<RecordKeysPO>> getExternalIdState(Object key) {
            if (MapUtils.isEmpty(interimExternalIds)) {
                return null;
            }
            return interimExternalIds.get(key);
        }
        /**
         * Gets the saved state for GSN.
         * @param key the id
         * @return state or null
         */
        public Pair<RecordIdentityContext, Predicate<RecordKeysPO>> getGsnState(Object key) {
            if (MapUtils.isEmpty(interimGSNs)) {
                return null;
            }
            return interimGSNs.get(key);
        }
        /**
         * Puts etalon state.
         * @param etalonId the etalon id.
         * @param state the query state
         */
        public void putEtalonState(String etalonId, Pair<RecordIdentityContext, Predicate<RecordKeysPO>> state) {
            getInterimEtalons().put(etalonId, state);
            getInput().computeIfAbsent(IdSetType.ETALON_ID, key -> new ArrayList<>()).add(etalonId);
        }
        /**
         * Puts etalon state.
         * @param originId the origin id
         * @param state the state
         */
        public void putOriginState(String originId, Pair<RecordIdentityContext, Predicate<RecordKeysPO>> state) {
            getInterimOrigins().put(originId, state);
            getInput().computeIfAbsent(IdSetType.ORIGIN_ID, key -> new ArrayList<>()).add(originId);
        }
        /**
         * Puts external id state.
         * @param externalId the external id
         * @param state the state
         */
        public void putExternalIdState(Triple<String, String, String> externalId, Pair<RecordIdentityContext, Predicate<RecordKeysPO>> state) {
            getInterimExternalIds().put(externalId, state);
            getInput().computeIfAbsent(IdSetType.EXTERNAL_ID, key -> new ArrayList<>()).add(externalId);
        }
        /**
         * Puts GSN state.
         * @param externalId the external id
         * @param state the state
         */
        public void putGsnState(Long gsn, Pair<RecordIdentityContext, Predicate<RecordKeysPO>> state) {
            getInterimGsns().put(gsn, state);
            getInput().computeIfAbsent(IdSetType.GSN, key -> new ArrayList<>()).add(gsn);
        }
    }
}
