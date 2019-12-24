package org.unidata.mdm.meta.service.segments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.PublishModelRequestContext;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.service.MetaDraftServiceExt;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.VoidPipelineOutput;

/**
 * @author maria.chistyakova
 * @since  18.12.2019
 */
@Component(ModelPublishFinishExecutor.SEGMENT_ID)
public class ModelPublishFinishExecutor extends Finish<PublishModelRequestContext, VoidPipelineOutput> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_PUBLISH_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.finish.description";
    /**
     * MMS. Cheap and dirty.
     */
    @Autowired
    private MetaDraftServiceExt metaModelService;
    /**
     * Constructor.
     */
    public ModelPublishFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, VoidPipelineOutput.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public VoidPipelineOutput finish(PublishModelRequestContext ctx) {
        metaModelService.apply();
        return VoidPipelineOutput.INSTANCE;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return PublishModelRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
