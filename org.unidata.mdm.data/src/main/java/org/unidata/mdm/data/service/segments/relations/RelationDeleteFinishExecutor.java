package org.unidata.mdm.data.service.segments.relations;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.dto.DeleteRelationDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationDeleteFinishExecutor.SEGMENT_ID)
public class RelationDeleteFinishExecutor extends Finish<DeleteRelationRequestContext, DeleteRelationDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_DELETE_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.delete.finish.description";
    /**
     * Constructor.
     */
    public RelationDeleteFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, DeleteRelationDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteRelationDTO finish(DeleteRelationRequestContext ctx) {
        RelationKeys keys = ctx.relationKeys();
        return new DeleteRelationDTO(keys, keys.getRelationName(), keys.getRelationType());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
