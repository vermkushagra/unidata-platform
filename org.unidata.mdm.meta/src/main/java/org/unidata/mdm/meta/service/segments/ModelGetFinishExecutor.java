package org.unidata.mdm.meta.service.segments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.GetModelRequestContext;
import org.unidata.mdm.meta.dto.GetModelDTO;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 28, 2019
 */
@Component(ModelGetFinishExecutor.SEGMENT_ID)
public class ModelGetFinishExecutor extends Finish<GetModelRequestContext, GetModelDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_GET_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = MetaModule.MODULE_ID + ".model.get.finish.description";
    /**
     * MMS. Cheap and dirty.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public ModelGetFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, GetModelDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public GetModelDTO finish(GetModelRequestContext ctx) {
        return metaModelService.getModel(ctx);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return GetModelRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
