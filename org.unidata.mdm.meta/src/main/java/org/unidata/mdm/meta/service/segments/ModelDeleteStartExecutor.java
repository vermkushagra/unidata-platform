package org.unidata.mdm.meta.service.segments;

import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author maria.chistyakova
 * @since  06.12.2019
 */
@Component(ModelDeleteStartExecutor.SEGMENT_ID)
public class ModelDeleteStartExecutor extends Start<DeleteModelRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_DELETE_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.start.description";
    /**
     * Constructor.
     */
    public ModelDeleteStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, DeleteModelRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(DeleteModelRequestContext ctx) {
        // NOOP. Start does nothing here.
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(DeleteModelRequestContext ctx) {
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
