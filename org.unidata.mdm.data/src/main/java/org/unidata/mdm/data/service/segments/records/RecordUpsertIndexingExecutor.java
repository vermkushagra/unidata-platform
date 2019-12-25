package org.unidata.mdm.data.service.segments.records;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.convert.RecordIndexingConverter;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.RecordHeaderField;
import org.unidata.mdm.meta.type.search.RecordIndexId;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.id.ManagedIndexId;
import org.unidata.mdm.search.type.indexing.Indexing;
import org.unidata.mdm.search.type.indexing.IndexingField;
import org.unidata.mdm.search.util.SearchUtils;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * Th
 * @author Mikhail Mikhailov on Nov 10, 2019
 */
@Component(RecordUpsertIndexingExecutor.SEGMENT_ID)
public class RecordUpsertIndexingExecutor extends Point<UpsertRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_INDEXING]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.indexing.description";
    /**
     * Delay for async audit operations.
     * TODO Move to {@link SearchService} and don't touch directly!
     */
    @Value("${unidata.data.refresh.immediate:true}")
    private Boolean refreshImmediate;
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public RecordUpsertIndexingExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 0. Detect 'no change' condition and skip collecting,
            // if we're called from regular upsert.
            // Proceed further for jobs anyway.
            if (!ctx.isRecalculateWholeTimeline() && ctx.upsertAction() == UpsertAction.NO_ACTION) {
                return;
            }

            RecordKeys keys = ctx.keys();
            RecordUpsertChangeSet cs = ctx.changeSet();

            IndexRequestContext irc = IndexRequestContext.builder()
                    .drop(UpsertAction.INSERT != ctx.upsertAction())
                    .entity(keys.getEntityName())
                    .delete(collectDeletes(ctx))
                    .index(collectUpdates(ctx))
                    .routing(keys.getEtalonKey().getId())
                    .refresh(!ctx.isBatchOperation() && refreshImmediate)
                    .build();

            cs.setIndexRequestContext(irc);

        } finally {
            MeasurementPoint.stop();
        }
    }

    private Collection<ManagedIndexId> collectDeletes(UpsertRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        Timeline<OriginRecord> current = ctx.currentTimeline();
        if (current.isEmpty()) {
            return Collections.emptyList();
        }

        return current.stream()
                .map(interval -> RecordIndexId.of(keys.getEntityName(), keys.getEtalonKey().getId(), interval.getPeriodId()))
                .collect(Collectors.toList());
    }

    private Collection<Indexing> collectUpdates(UpsertRequestContext ctx) {

        Timeline<OriginRecord> next = ctx.nextTimeline();
        if (next.isEmpty()) {
            return Collections.emptyList();
        }

        RecordKeys keys = ctx.keys();

        boolean isNew = UpsertAction.INSERT == ctx.upsertAction();
        boolean isPending = keys.isPending();
        Boolean isPublished = isNew ? !keys.isPending() : keys.isPublished();

        Map<EtalonRecord, Collection<IndexingField>> records = new IdentityHashMap<>(next.size());
        for (TimeInterval<OriginRecord> i : next) {

            EtalonRecord etalon = i.getCalculationResult();
            if (Objects.isNull(etalon)) {
                continue;
            }

            Collection<IndexingField> fields = new ArrayList<>(RecordHeaderField.values().length);
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_FROM.getName(), i.getValidFrom()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_TO.getName(), i.getValidTo()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_CREATED_AT.getName(), etalon.getInfoSection().getCreateDate()));

            if (!isNew) {
                fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_UPDATED_AT.getName(), etalon.getInfoSection().getUpdateDate()));
            }

            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PENDING.getName(), isPending));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PUBLISHED.getName(), isPublished));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_PERIOD_ID.getName(), PeriodIdUtils.periodIdFromDate(etalon.getInfoSection().getValidTo())));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_ORIGINATOR.getName(), etalon.getInfoSection().getUpdatedBy()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_DELETED.getName(), keys.getEtalonKey().getStatus() == RecordStatus.INACTIVE));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_INACTIVE.getName(), !i.isActive()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_ETALON_ID.getName(), keys.getEtalonKey().getId()));
            fields.add(IndexingField.of(EntityIndexType.RECORD, RecordHeaderField.FIELD_OPERATION_TYPE.getName(), etalon.getInfoSection().getOperationType().name()));
            fields.add(IndexingField.ofStrings(EntityIndexType.RECORD, RecordHeaderField.FIELD_EXTERNAL_KEYS.getName(),
                i.unlock().toCalculables().stream()
                    .filter(origin -> origin.getValue() != null && ApprovalState.APPROVED == origin.getApproval())
                    .map(origin -> origin.getSourceSystem() + SearchUtils.COLON_SEPARATOR + origin.getExternalId())
                    .collect(Collectors.toList())));

            records.put(etalon, fields);
        }

        return RecordIndexingConverter.convert(records);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
