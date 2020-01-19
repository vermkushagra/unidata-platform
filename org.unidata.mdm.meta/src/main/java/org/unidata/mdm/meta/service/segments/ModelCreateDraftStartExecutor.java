package org.unidata.mdm.meta.service.segments;

import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.CreateDraftModelRequestContext;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author maria.chistyakova
 * @since  13.01.2020
 */
@Component(ModelCreateDraftStartExecutor.SEGMENT_ID)
public class ModelCreateDraftStartExecutor extends Start<CreateDraftModelRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_CREATE_DRAFT_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.start.description";
    /**
     * Constructor.
     */
    public ModelCreateDraftStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, CreateDraftModelRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(CreateDraftModelRequestContext ctx) {
        // NOOP. Start does nothing here.
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(CreateDraftModelRequestContext ctx) {
        // No subject for this type of pipelines
        // This may be storage id in the future
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return start == this;
    }
}
