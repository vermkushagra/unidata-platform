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

package com.unidata.mdm.backend.service.data.timeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.RelationIdentityContext;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.TimeInterval;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;
import com.unidata.mdm.backend.common.types.Timeline;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.driver.EtalonCompositionDriverType;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;

/**
 * @author Mikhail Mikhailov
 * Timeline service implementation.
 */
@Service
public class TimelineServiceImpl implements TimelineService {
    /**
     * This logger.
     */
    private static final Logger LOGGER =    LoggerFactory.getLogger(TimelineServiceImpl.class);
    /**
     * Origins vistory DAO.
     */
    @Autowired
    private OriginsVistoryDao originsVistoryDao;
    /**
     * Common functionality.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Etalons component.
     */
    @Autowired
    private EtalonRecordsComponent etalonComponent;
    /**
     * Etalon composer.
     */
    @Autowired
    private EtalonComposer etalonComposer;
    /**
     * Constructor.
     */
    public TimelineServiceImpl() {
        super();
    }
    /**
     * Does ensure record keys.
     * @param ctx the context
     * @return keys or throws
     */
    private RecordKeys ensureRecordKeys(RecordIdentityContext ctx) {
        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            if (keys == null) {

                keys = commonRecordsComponent.identify(ctx);
                if (keys == null) {
                    final String message = "Workflow timeline cannot identify record";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_TIMELINE_NO_IDENTITY);
                }
            }

            return keys;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does ensure record keys.
     * @param ctx the context
     * @return keys or throws
     */
    private RecordKeys ensureRelationKeys(RelationIdentityContext ctx) {
        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            if (keys == null) {

                keys = commonRecordsComponent.identify(ctx);
                if (keys == null) {
                    final String message = "Workflow timeline cannot identify record";
                    LOGGER.warn(message);
                    throw new DataProcessingException(message, ExceptionId.EX_DATA_TIMELINE_NO_IDENTITY);
                }
            }

            return keys;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<OriginRecord> loadTimeline(RecordIdentityContext ctx, boolean viewDrafts) {

        // TODO load timeline and versions data in one DB request.
        MeasurementPoint.start();
        try {

            // 1. Ensure keys
            RecordKeys keys = ensureRecordKeys(ctx);

            // 2. Load from DB.
            List<TimeIntervalPO> intervals = originsVistoryDao
                    .loadContributingRecordsTimeline(keys.getEtalonKey().getId(), keys.getEntityName(),
                            viewDrafts);

            // 3. Build the timeline and load data.
            List<TimeInterval<OriginRecord>> collected = new ArrayList<>(intervals != null ? intervals.size() : 0);
            for (int i = 0; intervals != null && i < intervals.size(); i++) {

                TimeIntervalPO tipo = intervals.get(i);
                Date point = tipo.getFrom() == null ? tipo.getTo() : tipo.getFrom();
                Pair<EtalonRecord, List<CalculableHolder<OriginRecord>>> data
                    = etalonComponent.loadEtalonDataFull(keys.getEtalonKey().getId(),
                        point, null, null, null, true, viewDrafts);

                EtalonRecord etalon
                    = data != null && Objects.nonNull(data.getKey()) ? data.getKey() : null;
                List<CalculableHolder<OriginRecord>> calculables
                    = data != null && CollectionUtils.isNotEmpty(data.getValue()) ? data.getValue() : Collections.emptyList();

                TimeInterval<OriginRecord> in = TimeInterval.of(calculables, etalon, tipo.getFrom(), tipo.getTo(),
                                etalonComposer.hasActive(EtalonCompositionDriverType.BVR, calculables));

                collected.add(in);
            }

            return Timeline.of(keys, collected);
            /*
            boolean timelineIsInPendingState = false;
            List<TimeIntervalDTO> result = new ArrayList<>(intervals != null ? intervals.size() : 0);
            for (int i = 0; intervals != null && i < intervals.size(); i++) {

                TimeIntervalPO tipo = intervals.get(i);
                List<ContributorDTO> contributors
                    = new ArrayList<>(tipo.getContributors() != null ? tipo.getContributors().length : 0);
                List<CalculableHolder<ContributorDTO>> calculables
                    = new ArrayList<>(tipo.getContributors() != null ? tipo.getContributors().length : 0);
                boolean hasPendingVersions = false;
                for (int j = 0; tipo.getContributors() != null && j < tipo.getContributors().length; j++) {
                    ContributorPO copo = tipo.getContributors()[j];
                    ContributorDTO cdto
                        = new ContributorDTO(copo.getOriginId(),
                            copo.getRevision(),
                            copo.getSourceSystem(),
                            copo.getStatus() == null ? null : copo.getStatus().toString(),
                            copo.getApproval() == null ? null : copo.getApproval().toString(),
                            copo.getOwner(),
                            copo.getLastUpdate(),
                            tipo.getName(),
                            copo.getOperationType());

                    calculables.add(new TimeIntervalContributorHolder(cdto));
                    contributors.add(cdto);
                    hasPendingVersions = !hasPendingVersions ? copo.getApproval() == ApprovalState.PENDING : hasPendingVersions;
                }

                WorkflowTimeIntervalDTO ti = new WorkflowTimeIntervalDTO(
                        tipo.getFrom(),
                        tipo.getTo(),
                        tipo.getPeriodId(),
                        etalonComposer.hasActive(EtalonCompositionDriverType.BVR, calculables), hasPendingVersions);

                timelineIsInPendingState = !timelineIsInPendingState ? hasPendingVersions : timelineIsInPendingState;
                if (hasPendingVersions) {
                    List<OriginsVistoryRecordPO> pending
                        = originsVistoryDao.loadPendingVersionsByEtalonId(
                                keys.getEtalonKey().getId(),
                                tipo.getFrom() != null ? tipo.getFrom() : tipo.getTo());

                    List<ContributorDTO> pendings = new ArrayList<>(pending.size());
                    for (OriginsVistoryRecordPO ppo : pending) {
                        ContributorDTO cdto
                            = new ContributorDTO(ppo.getOriginId(),
                                ppo.getRevision(),
                                ppo.getSourceSystem(),
                                ppo.getStatus() == null ? null : ppo.getStatus().toString(),
                                ppo.getApproval() == null ? null : ppo.getApproval().toString(),
                                ppo.getCreatedBy(),
                                ppo.getCreateDate(),
                                tipo.getName(),
                                ppo.getOperationType());

                        pendings.add(cdto);
                    }

                    ti.getPendings().addAll(pendings);
                }

                ti.getContributors().addAll(contributors);
                result.add(ti);
            }

            WorkflowTimelineDTO timeline
                = new WorkflowTimelineDTO(keys.getEtalonKey().getId(), timelineIsInPendingState, keys.isPublished());
            if (!result.isEmpty()) {
                timeline.getIntervals().addAll(result);
            }

            return timeline;
            */

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<OriginRelation> loadTimeline(RelationIdentityContext ctx, boolean viewDrafts) {
        MeasurementPoint.start();
        try {
            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<OriginRecord> loadTimeline(RecordIdentityContext ctx, Date from, Date to, boolean viewDrafts) {
        MeasurementPoint.start();
        try {

            Timeline<OriginRecord> timeline = loadTimeline(ctx, viewDrafts);
            if (!timeline.isEmpty()) {
                timeline.reduceBy(from, to);
            }

            return timeline;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<OriginRelation> loadTimeline(RelationIdentityContext ctx, Date from, Date to, boolean viewDrafts) {
        MeasurementPoint.start();
        try {

            Timeline<OriginRelation> timeline = loadTimeline(ctx, viewDrafts);
            if (!timeline.isEmpty()) {
                timeline.reduceBy(from, to);
            }

            return timeline;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<TimeIntervalContributorInfo> loadTimelineInfo(RecordIdentityContext ctx, boolean viewDrafts) {
        MeasurementPoint.start();
        try {
            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timeline<TimeIntervalContributorInfo> loadTimelineInfo(RelationIdentityContext ctx, boolean viewDrafts) {
        MeasurementPoint.start();
        try {
            return null;
        } finally {
            MeasurementPoint.stop();
        }
    }

}
