package org.unidata.mdm.meta.service.segments;

import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.PublishModelRequestContext;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author maria.chistyakova
 * @since  18.12.2019
 */
@Component(ModelPublishStartExecutor.SEGMENT_ID)
public class ModelPublishStartExecutor extends Start<PublishModelRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_PUBLISH_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.start.description";
    /**
     * Constructor.
     */
    public ModelPublishStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, PublishModelRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(PublishModelRequestContext ctx) {
        // NOOP. Start does nothing here.
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(PublishModelRequestContext ctx) {
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
