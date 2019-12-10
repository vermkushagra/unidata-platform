package org.unidata.mdm.data.service.segments.relations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.RecordValidationService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

@Component(RelationUpsertValidateExecutor.SEGMENT_ID)
public class RelationUpsertValidateExecutor extends Point<UpsertRelationRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_VALIDATE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relations.upsert.validate.description";
    /**
     * Validation service.
     */
    @Autowired
    private RecordValidationService validationService;
    /**
     * Constructor.
     */
    public RelationUpsertValidateExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    @Override
    public void point(UpsertRelationRequestContext ctx) {
        // Check record validity
        validationService.checkRelationDataRecord(ctx.getRelation(), ctx.relationName());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
