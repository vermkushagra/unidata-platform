package org.unidata.mdm.meta.service.segments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.DropDraftModelRequestContext;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.VoidPipelineOutput;

/**
 * @author maria.chistyakova
 * @since  13.01.2020
 */
@Component(ModelDropDraftFinishExecutor.SEGMENT_ID)
public class ModelDropDraftFinishExecutor extends Finish<DropDraftModelRequestContext, VoidPipelineOutput> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_DROP_DRAFT_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.finish.description";
    /**
     * MMS. Cheap and dirty.
     */
    @Autowired
    private MetaDraftService metaModelService;
    /**
     * Constructor.
     */
    public ModelDropDraftFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, VoidPipelineOutput.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public VoidPipelineOutput finish(DropDraftModelRequestContext ctx) {
        metaModelService.removeDraft();
        return VoidPipelineOutput.INSTANCE;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DropDraftModelRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
