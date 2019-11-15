package org.unidata.mdm.data.service.segments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.po.BinaryLargeObjectPO;
import org.unidata.mdm.core.po.CharacterLargeObjectPO;
import org.unidata.mdm.core.po.LargeObjectPO;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.SimpleAttribute.DataType;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.RecordUpsertChangeSet;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.type.support.IdentityHashSet;

/**
 * @author Mikhail Mikhailov
 */
@Component(RecordUpsertLobSubmitExecutor.SEGMENT_ID)
public class RecordUpsertLobSubmitExecutor extends Point<UpsertRequestContext> implements LobSubmitSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_LOB_SUBMIT]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.lob.submit.description";
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public RecordUpsertLobSubmitExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {

        if (ctx.upsertAction() == UpsertAction.NO_ACTION) {
            return;
        }

        // Only first level supported
        MeasurementPoint.start();
        try {

            CalculableHolder<OriginRecord> holder = ctx.modificationBox().peek(ctx.toBoxKey());
            if (Objects.isNull(holder) || Objects.isNull(holder.getValue())) {
                return;
            }

            List<Pair<Collection<Attribute>, AttributeModelElement>> collected = new ArrayList<>();
            Map<String, AttributeModelElement> attrs = metaModelService.getAttributesInfoMap(holder.getTypeName());

            for (Entry<String, AttributeModelElement> entry : attrs.entrySet()) {

                if (!entry.getValue().isBlob() && !entry.getValue().isClob()) {
                    continue;
                }

                Collection<Attribute> sas = holder.getValue().getAttributeRecursive(entry.getKey());
                if (CollectionUtils.isNotEmpty(sas)) {
                    collected.add(Pair.of(sas, entry.getValue()));
                }
            }

            if (CollectionUtils.isNotEmpty(collected)) {
                collectLobAttributes(ctx, holder, collected);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
    /**
     * @param ctx the context
     * @param collected attributes
     * @param id origin id
     */
    private void collectLobAttributes(UpsertRequestContext ctx, CalculableHolder<OriginRecord> origin,
            List<Pair<Collection<Attribute>, AttributeModelElement>> collected) {

        Collection<CalculableHolder<OriginRecord>> prev = Collections.emptyList();
        if (ctx.upsertAction() == UpsertAction.UPDATE) {

            Timeline<OriginRecord> current = ctx.currentTimeline();
            prev = current.stream()
                .map(TimeInterval::unlock)
                .map(i -> i.peek(ctx.toBoxKey()))
                .collect(Collectors.toCollection(IdentityHashSet::new));
        }

        RecordUpsertChangeSet set = ctx.changeSet();
        for (Pair<Collection<Attribute>, AttributeModelElement> entry : collected) {

            for (Attribute attr : entry.getKey()) {

                SimpleAttribute<?> lobAttr = attr.narrow();
                boolean isBinary = lobAttr.getDataType() == DataType.BLOB;
                String objectId = isBinary ? getBlobObjectId(lobAttr) : getClobObjectId(lobAttr);
                if (objectId == null) {
                    continue;
                }

                boolean activated = isALreadyActivated(prev, objectId, entry.getValue().getPath());
                if (!activated) {

                    LargeObjectPO po = isBinary ? new BinaryLargeObjectPO() : new CharacterLargeObjectPO();
                    po.setId(objectId);
                    po.setRecordId(origin.getValue().getInfoSection().getOriginKey().getId());
                    po.setState(ApprovalState.APPROVED);
                    set.getLargeObjectPOs().add(po);
                }
            }
        }
    }
}
