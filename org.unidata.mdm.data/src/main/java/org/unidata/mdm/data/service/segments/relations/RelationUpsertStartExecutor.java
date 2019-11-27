package org.unidata.mdm.data.service.segments.relations;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationUpsertStartExecutor.SEGMENT_ID)
public class RelationUpsertStartExecutor extends Start<UpsertRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.upsert.start.description";
    /**
     * Constructor.
     */
    public RelationUpsertStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, UpsertRelationRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(UpsertRelationRequestContext ctx) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(UpsertRelationRequestContext ctx) {
        // TODO Auto-generated method stub
        return null;
    }

}
