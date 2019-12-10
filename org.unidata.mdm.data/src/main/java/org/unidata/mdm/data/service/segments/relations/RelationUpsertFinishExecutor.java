package org.unidata.mdm.data.service.segments.relations;

import java.util.Date;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.TimeInterval;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.dto.UpsertRelationDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationUpsertFinishExecutor.SEGMENT_ID)
public class RelationUpsertFinishExecutor extends Finish<UpsertRelationRequestContext, UpsertRelationDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.upsert.finish.description";
    /**
     * Constructor.
     */
    public RelationUpsertFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, UpsertRelationDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRelationDTO finish(UpsertRelationRequestContext ctx) {

        RelationKeys keys = ctx.relationKeys();
        UpsertRelationDTO result = new UpsertRelationDTO(keys, keys.getRelationName(), keys.getRelationType());

        Date from = keys.getRelationType() == RelationType.CONTAINS ? ctx.containmentContext().getValidFrom() : ctx.getValidFrom();
        Date to = keys.getRelationType() == RelationType.CONTAINS ? ctx.containmentContext().getValidTo() : ctx.getValidTo();
        UpsertAction action = keys.getRelationType() == RelationType.CONTAINS ? ctx.containmentContext().upsertAction() : ctx.upsertAction();

        result.setRights(ctx.accessRight());
        result.setValidFrom(from);
        result.setValidTo(to);
        result.setAction(action);

        // Result can't be returned properly yet.
        // TODO return the whole array of period results in the future
        if (Objects.nonNull(ctx.nextTimeline()) && !ctx.nextTimeline().isEmpty()) {
            Date point = from == null ? to : from;
            TimeInterval<OriginRelation> selected = ctx.nextTimeline().selectAsOf(point);
            result.setEtalon(selected == null
                    ? ctx.nextTimeline().get(0).getCalculationResult()
                    : selected.getCalculationResult());
        }

        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
