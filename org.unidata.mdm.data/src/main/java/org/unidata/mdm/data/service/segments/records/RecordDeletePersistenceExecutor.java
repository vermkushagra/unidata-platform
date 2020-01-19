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

package org.unidata.mdm.data.service.segments.records;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.MutableTimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.po.EtalonRecordDraftStatePO;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.keys.RecordExternalKeysPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;
import org.unidata.mdm.data.po.keys.RecordOriginKeyPO;
import org.unidata.mdm.data.service.RecordChangeSetProcessor;
import org.unidata.mdm.data.service.impl.CommonRecordsComponent;
import org.unidata.mdm.data.service.impl.RecordComposerComponent;
import org.unidata.mdm.data.type.apply.RecordDeleteChangeSet;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.util.RecordFactoryUtils;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Nov 20, 2019
 * Former origin component code.
 */
@Component(RecordDeletePersistenceExecutor.SEGMENT_ID)
public class RecordDeletePersistenceExecutor extends Point<DeleteRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_DELETE_PERSISTENCE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.delete.persistence.description";
    /**
     * The CRC.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * The set processor.
     */
    @Autowired
    private RecordChangeSetProcessor recordChangeSetProcessor;
    /**
     * Calculator.
     */
    @Autowired
    private RecordComposerComponent recordComposerComponent;
    /**
     * Constructor.
     */
    public RecordDeletePersistenceExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(DeleteRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Prepare set
            prepareChangeSet(ctx);

            // 2. Apply changes
            applyChangeSet(ctx);

        } finally {
            MeasurementPoint.stop();
        }
    }

    private void prepareChangeSet(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        RecordDeleteChangeSet set = ctx.changeSet();
        Timeline<OriginRecord> current = ctx.currentTimeline();

        String user = SecurityUtils.getCurrentUserName();
        Date ts = ctx.timestamp();

        if (!ctx.isInactivatePeriod()) {
            // TODO recalculate for origin turn off
            ctx.nextTimeline(current);
        }

        if (ctx.isWipe()) {

            RecordKeysPO po = new RecordKeysPO();
            po.setId(keys.getEtalonKey().getId());
            po.setLsn(keys.getEtalonKey().getLsn());
            po.setName(keys.getEntityName());
            po.setShard(keys.getShard());
            po.setOriginKeys(keys.getSupplementaryKeys().stream()
                    .map(ok -> {

                        RecordExternalKeysPO extKey = new RecordExternalKeysPO();
                        extKey.setExternalId(ok.getExternalId(), ok.getEntityName(), ok.getSourceSystem());
                        set.getWipeExternalKeys().add(extKey);

                        RecordOriginKeyPO okpo = new RecordOriginKeyPO();
                        okpo.setExternalId(ok.getExternalId());
                        okpo.setId(UUID.fromString(ok.getId()));
                        okpo.setSourceSystem(ok.getSourceSystem());
                        return okpo;
                    })
                    .collect(Collectors.toList()));

            set.getWipeRecordKeys().add(po);
        } else if (ctx.isInactivateOrigin()) {
            // 1. Turn off requested origin
            set.getOriginRecordUpdatePOs().add(RecordFactoryUtils.createRecordOriginPO(ctx, keys, RecordStatus.INACTIVE));

            // 2. Turn off the whole record, if the origin was the only active one
            if (commonRecordsComponent.allOriginsAlreadyInactive(keys)) {
                RecordEtalonPO etalon = RecordFactoryUtils.createRecordEtalonPO(ctx, keys, RecordStatus.INACTIVE);
                set.setEtalonRecordUpdatePO(etalon);
            }
        } else {
            // @Modules
            /*
            WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_DELETE_WF_ASSIGNMENTS);
            ApprovalState state = !ctx.isInactivatePeriod()
                    ? DataRecordUtils.calculateRecordState(ctx, assignment)
                    : DataRecordUtils.calculateVersionState(ctx, keys, assignment);

            if (state == ApprovalState.PENDING) {
                ensurePendingState(ctx);
                ctx.skipNotification();
            }
            */
            ApprovalState state = ApprovalState.APPROVED;
            if (ctx.isInactivateEtalon()) {
                inactivateEtalon(keys, set, state, ts, user, ctx.getOperationId());
            } else if (ctx.isInactivatePeriod()) {

                MutableTimeInterval<OriginRecord> box = ctx.modificationBox();
                for (CalculableHolder<OriginRecord> holder : box) {

                    RecordVistoryPO version
                        = RecordFactoryUtils.createInactiveVistoryRecordPO(
                            holder.getValue().getInfoSection().getOriginKey().getId(),
                            holder.getValue().getInfoSection().getValidFrom(),
                            holder.getValue().getInfoSection().getValidTo(),
                            holder.getLastUpdate(),
                            holder.getValue(),
                            ctx);

                    set.getOriginsVistoryRecordPOs().add(version);
                }

                // Check for input. Timeline is not reduced for period delete,
                // because we'd like to recognize the situation of no active periods after this period delete.
                Timeline<OriginRecord> next = current.merge(box);
                next.forEach(i -> recordComposerComponent.toEtalon(keys, i, ts, user));
                ctx.nextTimeline(next);

                // Timeline is completely inactive. Inactivate etalon.
//                if (next.stream().noneMatch(TimeInterval::isActive)) {
//                    inactivateEtalon(keys, set, state, ts, user, ctx.getOperationId());
//                } else {
                    if (state == ApprovalState.PENDING) {

                        RecordEtalonPO result = new RecordEtalonPO();
                        result.setName(keys.getEntityName());
                        result.setStatus(keys.getEtalonKey().getStatus());
                        result.setApproval(state);
                        result.setId(keys.getEtalonKey().getId());
                        result.setUpdateDate(ts);
                        result.setUpdatedBy(user);
                        result.setOperationId(ctx.getOperationId());
                        set.setEtalonRecordUpdatePO(result);

                        EtalonRecordDraftStatePO draft = new EtalonRecordDraftStatePO();
                        draft.setCreateDate(ts);
                        draft.setCreatedBy(user);
                        draft.setEtalonId(keys.getEtalonKey().getId());
                        draft.setStatus(RecordStatus.INACTIVE);
                        set.getEtalonRecordDraftStatePOs().add(draft);
                    }
                //}
            }
        }

    }

    private void inactivateEtalon(RecordKeys keys, RecordDeleteChangeSet set, ApprovalState state, Date ts, String user, String operationId) {

        if (state == ApprovalState.PENDING) {

            RecordEtalonPO result = new RecordEtalonPO();
            result.setName(keys.getEntityName());
            result.setStatus(keys.getEtalonKey().getStatus());
            result.setApproval(state);
            result.setId(keys.getEtalonKey().getId());
            result.setShard(keys.getShard());
            result.setUpdateDate(ts);
            result.setUpdatedBy(user);
            result.setOperationId(operationId);
            set.setEtalonRecordUpdatePO(result);

            EtalonRecordDraftStatePO draft = new EtalonRecordDraftStatePO();
            draft.setCreateDate(ts);
            draft.setCreatedBy(user);
            draft.setEtalonId(keys.getEtalonKey().getId());
            draft.setStatus(RecordStatus.INACTIVE);
            set.getEtalonRecordDraftStatePOs().add(draft);
        } else {

            // 1. Generate inactive etalon
            RecordEtalonPO result = new RecordEtalonPO();
            result.setStatus(RecordStatus.INACTIVE);
            result.setApproval(ApprovalState.APPROVED);
            result.setId(keys.getEtalonKey().getId());
            result.setShard(keys.getShard());
            result.setUpdateDate(ts);
            result.setUpdatedBy(user);
            result.setOperationId(operationId);
            set.setEtalonRecordUpdatePO(result);

            // 2. Generate inactive records for all known keys.
            keys.getSupplementaryKeys().stream()
                .filter(k -> k.getStatus() != RecordStatus.INACTIVE)
                .map(k -> {
                    RecordOriginPO opo = new RecordOriginPO();
                    opo.setStatus(RecordStatus.INACTIVE);
                    opo.setUpdateDate(ts);
                    opo.setUpdatedBy(user);
                    opo.setId(k.getId());
                    opo.setEtalonId(keys.getEtalonKey().getId());
                    opo.setShard(keys.getShard());
                    return opo;
                })
                .collect(Collectors.toCollection(set::getOriginRecordUpdatePOs));
        }
    }

    private void applyChangeSet(DeleteRequestContext ctx) {

        // Will be applied later in batched fashion.
        if (ctx.isBatchOperation()) {
            return;
        }

        RecordDeleteChangeSet set = ctx.changeSet();
        recordChangeSetProcessor.apply(set);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
