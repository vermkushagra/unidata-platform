package org.unidata.mdm.meta.service.segments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.CreateDraftModelRequestContext;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.meta.service.MetaDraftService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.impl.MetaDraftServiceImpl;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.VoidPipelineOutput;

/**
 * @author maria.chistyakova
 * @since  13.01.2020
 */
@Component(ModelCreateDraftFinishExecutor.SEGMENT_ID)
public class ModelCreateDraftFinishExecutor extends Finish<CreateDraftModelRequestContext, VoidPipelineOutput> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelCreateDraftFinishExecutor.class);

    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_CREATE_DRAFT_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.finish.description";


    @Autowired
    private MetaDraftService metaDraftService;
    /**
     * Constructor.
     */
    public ModelCreateDraftFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, VoidPipelineOutput.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public VoidPipelineOutput finish(CreateDraftModelRequestContext ctx) {
        try {
            metaDraftService.loadActiveDraft();
        } catch (Exception e) {
            metaDraftService.removeDraft();
            LOGGER.error("Exception while loading draft from database.", e);
        }
        return VoidPipelineOutput.INSTANCE;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return CreateDraftModelRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
