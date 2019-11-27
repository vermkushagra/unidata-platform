package org.unidata.mdm.data.service.segments.relations;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.dto.UpsertRelationDTO;
import org.unidata.mdm.data.module.DataModule;
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
        // TODO Auto-generated method stub
        return new UpsertRelationDTO();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
