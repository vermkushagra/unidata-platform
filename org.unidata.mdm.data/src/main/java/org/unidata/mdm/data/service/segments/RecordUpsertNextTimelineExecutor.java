package org.unidata.mdm.data.service.segments;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.calculables.ModificationBox;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.calculables.impl.DataRecordHolder;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.data.impl.OriginRecordImpl;
import org.unidata.mdm.data.util.DataDiffUtils;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 7, 2019
 */
@Component(RecordUpsertNextTimelineExecutor.SEGMENT_ID)
public class RecordUpsertNextTimelineExecutor extends Point<UpsertRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_NEXT_TIMELINE_INIT]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.next.timeline.description";
    /**
     * MMS instance.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public RecordUpsertNextTimelineExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
        // TODO Auto-generated constructor stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {

        postProcessModbox(ctx);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }

    private void postProcessModbox(UpsertRequestContext ctx) {

        // 3. Finish origins part:
        // - Peek last modified OV from box and reset the box (we can store all abject in the future, if required)
        // - Check approval state and reset keys if needed
        ModificationBox<OriginRecord> box = ctx.modificationBox();
        CalculableHolder<OriginRecord> top = null;
        if (box.modifications(ctx.toBoxKey()) > 1) {

            // All versions. The box should be empty afterwards
            List<CalculableHolder<OriginRecord>> collected = box.reset(ctx.toBoxKey());
            top = collected.get(collected.size() - 1);
        } else {
            top = box.pop(ctx.toBoxKey());
        }

        // Check the upsert for being an update of the admin SS
        // If so, try to create diff origin.
        // Don't push on null diff (no changes)
        if (ctx.upsertAction() == UpsertAction.UPDATE
         && metaModelService.isAdminSourceSystem(top.getSourceSystem())) {
            top = ensureAdminInput(ctx, top);
        }

        // Re-insert only the very last version to the box,
        // as we don't store intermediate versions and thus can save space
        if (top != null) {
            box.push(top);
        }
    }

    /**
     * Returns diff origin if the data has been submitted
     * for the admin source system and the action is an update.
     * @param ctx the context
     * @return diff origin or null
     */
    private CalculableHolder<OriginRecord> ensureAdminInput(UpsertRequestContext ctx, CalculableHolder<OriginRecord> top) {

        Timeline<OriginRecord> current = ctx.currentTimeline();
        if (Objects.isNull(current) || current.isEmpty()) {
            return top;
        }

        List<TimeInterval<OriginRecord>> selection = current.selectBy(ctx.getValidFrom(), ctx.getValidTo());
        if (CollectionUtils.isEmpty(selection)
         || selection.size() > 1
         || !selection.get(0).isExact(ctx.getValidFrom(), ctx.getValidTo())
         // Special case, sometimes we need force create vistory. Example UN-9439
         || ctx.isApplyDraft()) {
            return top;
        }

        EtalonRecord prev = selection.get(0).getCalculationResult();
        if (Objects.isNull(prev)
                || (selection.get(0).isActive() != (top.getStatus() == RecordStatus.ACTIVE))) {
            // Forced to return original input on no data for a period
            // This is because we create a single revision for the whole period
            // and the only data version has to have all the necessary attributes.
            return top;
        }

        CalculableHolder<OriginRecord> base  = selection.get(0).unlock().peek(ctx.toBoxKey());
        boolean forceCodeAttributesCheck = ctx.operationType() == OperationType.COPY;
        DataRecord diff = DataDiffUtils.diffAsRecord(top.getTypeName(), top.getValue(), prev, base == null ? null : base.getValue(), forceCodeAttributesCheck);

        if (diff != null) {
            return new DataRecordHolder(new OriginRecordImpl()
                .withDataRecord(diff)
                .withInfoSection(top.getValue().getInfoSection()));
        }

        return null;
    }
}
