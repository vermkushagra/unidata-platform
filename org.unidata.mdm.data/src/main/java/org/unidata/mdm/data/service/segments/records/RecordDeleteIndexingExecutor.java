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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.convert.RecordIndexingConverter;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.RecordDeleteChangeSet;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.EtalonIndexId;
import org.unidata.mdm.meta.type.search.RecordHeaderField;
import org.unidata.mdm.meta.type.search.RecordIndexId;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.id.ManagedIndexId;
import org.unidata.mdm.search.type.indexing.IndexingField;
import org.unidata.mdm.search.util.SearchUtils;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 *         Listener for indexing of DELETE record actions.
 */
@Component(RecordDeleteIndexingExecutor.SEGMENT_ID)
public class RecordDeleteIndexingExecutor extends Point<DeleteRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_DELETE_INDEXING]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.delete.indexing.description";
    /**
     * The SS.
     */
    @Autowired
    private SearchService searchService;
    /**
     * Constructor.
     */
    public RecordDeleteIndexingExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(DeleteRequestContext context) {

        MeasurementPoint.start();
        try {
            // Not yet supported
            if (context.isInactivateOrigin()) {
                return;
            }

            if (context.isInactivatePeriod()) {
                handlePeriodDelete(context);
            } else {
                handleRecordDelete(context);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    private void handlePeriodDelete(DeleteRequestContext context) {

        RecordKeys keys = context.keys();

        RecordDeleteChangeSet batchSet = context.changeSet();
        Timeline<OriginRecord> current = context.currentTimeline();
        Timeline<OriginRecord> next = context.nextTimeline();

        List<TimeInterval<OriginRecord>> removed = current.selectBy(context.getValidFrom(), context.getValidTo());
        List<TimeInterval<OriginRecord>> added = next.selectBy(removed.isEmpty()
                ? context.getValidFrom()
                : removed.get(0).getValidFrom(), removed.isEmpty()
                ? context.getValidTo()
                : removed.get(removed.size() - 1).getValidTo());

        Map<EtalonRecord, Collection<IndexingField>> updates = new IdentityHashMap<>(current.size());
        for (TimeInterval<OriginRecord> ti : added) {

            EtalonRecord etalon = ti.getCalculationResult();

            Boolean pending = ti.isPending();
            Boolean inactive = !ti.isActive();
            if (ti.isExact(context.getValidFrom(), context.getValidTo())) {
                pending = keys.isPending();
                inactive = !keys.isPending();
            }

            Collection<IndexingField> fields = new ArrayList<>(RecordHeaderField.values().length);

            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_FROM.getName(), ti.getValidFrom()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_TO.getName(), ti.getValidTo()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_CREATED_AT.getName(), etalon.getInfoSection().getCreateDate()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_UPDATED_AT.getName(), etalon.getInfoSection().getUpdateDate()));

            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PENDING.getName(), pending));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PUBLISHED.getName(), keys.isPublished()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PERIOD_ID.getName(), PeriodIdUtils.periodIdFromDate(ti.getValidTo())));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_ORIGINATOR.getName(), etalon.getInfoSection().getUpdatedBy()));


            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_DELETED.getName(), Boolean.FALSE));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_INACTIVE.getName(), !pending && inactive));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_ETALON_ID.getName(), keys.getEtalonKey().getId()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_OPERATION_TYPE.getName(), etalon.getInfoSection().getOperationType().name()));
            fields.add(IndexingField.ofStrings(EntityIndexType.RECORD, RecordHeaderField.FIELD_EXTERNAL_KEYS.getName(),
                    keys.getSupplementaryKeys().stream()
                        .map(originKey -> originKey.getSourceSystem() + SearchUtils.COLON_SEPARATOR + originKey.getExternalId())
                        .collect(Collectors.toList())));

            updates.put(etalon, fields);
        }

        batchSet.setIndexRequestContext(IndexRequestContext.builder()
                .entity(keys.getEntityName())
                .routing(keys.getEtalonKey().getId())
                .refresh(!context.isBatchOperation() && searchService.isRefreshImmediate())
                .drop(true)
                .delete(toIndexIds(removed))
                .index(RecordIndexingConverter.convert(updates))
                .build());
    }

    private void handleRecordDelete(DeleteRequestContext context) {

        RecordKeys keys = context.keys();
        RecordDeleteChangeSet set = context.changeSet();
        Timeline<OriginRecord> current = context.currentTimeline();

        List<ManagedIndexId> deletes = toIndexIds(current.getAll());
        Map<EtalonRecord, Collection<IndexingField>> updates = new IdentityHashMap<>(current.size());

        deletes.addAll(current.stream()
            .map(ti -> RecordIndexId.of(keys.getEntityName(), keys.getEtalonKey().getId(), ti.getPeriodId()))
            .collect(Collectors.toList()));

        if (context.isWipe()) {
            deletes.addAll(Collections.singletonList(EtalonIndexId.of(keys.getEntityName(), keys.getEtalonKey().getId())));
        } else if (context.isInactivateEtalon()) {


            for (TimeInterval<OriginRecord> ti : current) {

                EtalonRecord etalon = ti.getCalculationResult();

                Collection<IndexingField> fields = new ArrayList<>(RecordHeaderField.values().length);

                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_FROM.getName(), ti.getValidFrom()));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_TO.getName(), ti.getValidTo()));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_CREATED_AT.getName(), etalon.getInfoSection().getCreateDate()));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_UPDATED_AT.getName(), etalon.getInfoSection().getUpdateDate()));

                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PENDING.getName(), keys.isPending() ? Boolean.TRUE : Boolean.FALSE));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PUBLISHED.getName(), keys.isPublished()));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_DELETED.getName(), keys.isPending() ? Boolean.FALSE : Boolean.TRUE));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_INACTIVE.getName(), !ti.isActive()));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PERIOD_ID.getName(), PeriodIdUtils.periodIdFromDate(ti.getValidTo())));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_ORIGINATOR.getName(), etalon.getInfoSection().getUpdatedBy()));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_ETALON_ID.getName(), keys.getEtalonKey().getId()));
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_OPERATION_TYPE.getName(), etalon.getInfoSection().getOperationType().name()));
                fields.add(IndexingField.ofStrings(EntityIndexType.RECORD, RecordHeaderField.FIELD_EXTERNAL_KEYS.getName(),
                        keys.getSupplementaryKeys().stream()
                            .map(originKey -> originKey.getSourceSystem() + SearchUtils.COLON_SEPARATOR + originKey.getExternalId())
                            .collect(Collectors.toList())));

                updates.put(etalon, fields);
             }
        }

        set.setIndexRequestContext(IndexRequestContext.builder()
                .entity(keys.getEntityName())
                .routing(keys.getEtalonKey().getId())
                .delete(deletes)
                .index(RecordIndexingConverter.convert(updates))
                .drop(true)
                .build());
    }

    private List<ManagedIndexId> toIndexIds(List<TimeInterval<OriginRecord>> intervals) {

        if (CollectionUtils.isEmpty(intervals)) {
            return Collections.emptyList();
        }

        return intervals.stream()
                .map(TimeInterval::<EtalonRecord>getCalculationResult)
                .filter(Objects::nonNull)
                .map(er -> RecordIndexId.of(
                        er.getInfoSection().getEntityName(),
                        er.getInfoSection().getEtalonKey().getId(),
                        er.getInfoSection().getValidTo()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean supports(Start<?> start) {
        return DeleteRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
