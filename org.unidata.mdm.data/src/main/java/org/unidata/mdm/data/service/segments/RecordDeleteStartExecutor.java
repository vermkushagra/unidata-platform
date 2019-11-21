package org.unidata.mdm.data.service.segments;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetRecordTimelineRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRecordsComponent;
import org.unidata.mdm.data.type.apply.RecordDeleteChangeSet;
import org.unidata.mdm.data.type.calculables.impl.DataRecordHolder;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.OriginRecordInfoSection;
import org.unidata.mdm.data.type.data.impl.OriginRecordImpl;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.timeline.RecordTimeInterval;
import org.unidata.mdm.system.service.PlatformConfiguration;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 *         'Delete' pre-check validator.
 */
@Component(RecordDeleteStartExecutor.SEGMENT_ID)
public class RecordDeleteStartExecutor extends Start<DeleteRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordDeleteStartExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_DELETE_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.delete.start.description";
    /**
     * Platform configuration instance.
     */
    @Autowired
    private PlatformConfiguration platformConfiguration;
    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Constructor.
     */
    public RecordDeleteStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, DeleteRequestContext.class);
    }
    /**
     * Execute.
     */
    @Override
    public void start(DeleteRequestContext ctx) {

        ensureExecutionContext(ctx);

        RecordKeys keys = ctx.keys();
        if (keys == null) {
            final String message = "Record submitted for (soft) deletion cannot be identified by supplied keys - etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_INVALID_DELETE_INPUT,
                    ctx.getEtalonKey(),
                    ctx.getOriginKey(),
                    ctx.getExternalId(),
                    ctx.getSourceSystem(),
                    ctx.getEntityName());
        }

        if (keys.isPending() && (!ctx.isWorkflowAction() && ctx.getApprovalState() != ApprovalState.PENDING)) {
            if (ctx.isBatchOperation()) {
                throw new DataProcessingException("Record in pending state. Delete disabled.",
                        DataExceptionIds.EX_DATA_DELETE_PERIOD_NOT_ACCEPTED_HAS_PENDING_RECORD);
            }
        }
    }

    private void ensureExecutionContext(DeleteRequestContext ctx) {

        Timeline<OriginRecord> timeline = ctx.currentTimeline();
        RecordKeys keys = null;
        if (timeline == null) {
            timeline = commonRecordsComponent.loadTimeline(
                    GetRecordTimelineRequestContext.builder(ctx)
                            .fetchData(true)
                            .build());

            keys = Objects.nonNull(timeline) ? timeline.getKeys() : null;
        } else {
            keys = timeline.getKeys();
        }

        if (Objects.isNull(keys)) {
            return;
        }

        if (Objects.isNull(ctx.changeSet())) {
            ctx.changeSet(new RecordDeleteChangeSet());
        }

        ctx.currentTimeline(timeline);
        ctx.keys(keys);
        ctx.timestamp(new Date());

        if (ctx.isInactivatePeriod()) {

            String user = SecurityUtils.getCurrentUserName();

            List<TimeInterval<OriginRecord>> current = timeline.selectBy(ctx.getValidFrom(), ctx.getValidTo());
            HashMap<String, CalculableHolder<OriginRecord>> base = new HashMap<>();

            OperationType operationType = ctx.operationType() == null ? OperationType.DIRECT : ctx.operationType();
            for (TimeInterval<OriginRecord> ti : current) {

                for (CalculableHolder<OriginRecord> ch : ti) {

                    if (ch.getStatus() == RecordStatus.INACTIVE || base.containsKey(ch.toBoxKey())) {
                        continue;
                    }

                    Date ts = ctx.localTimestamp();
                    OriginRecord clone = new OriginRecordImpl()
                            .withDataRecord(ch.getValue())
                            .withInfoSection(new OriginRecordInfoSection()
                                    .withApproval(ApprovalState.APPROVED)
                                    .withCreateDate(ts)
                                    .withCreatedBy(user)
                                    .withMajor(platformConfiguration.getPlatformMajor())
                                    .withMinor(platformConfiguration.getPlatformMinor())
                                    .withOperationType(operationType)
                                    .withOriginKey(ch.getValue().getInfoSection().getOriginKey())
                                    .withShift(DataShift.PRISTINE)
                                    .withStatus(RecordStatus.INACTIVE)
                                    .withUpdateDate(ts)
                                    .withUpdatedBy(user)
                                    .withValidFrom(ctx.getValidFrom())
                                    .withValidTo(ctx.getValidTo()));

                    CalculableHolder<OriginRecord> inactive = new DataRecordHolder(clone);
                    base.put(inactive.toBoxKey(), inactive);
                }
            }

            RecordTimeInterval box = new RecordTimeInterval(ctx.getValidFrom(), ctx.getValidTo(), base.values());
            ctx.modificationBox(box);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pipeline select(DeleteRequestContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }
}
