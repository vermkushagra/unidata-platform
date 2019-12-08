package org.unidata.mdm.meta.service.segments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.system.dto.NullResultDto;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author maria.chistyakova
 * @since  06.12.2019
 */
@Component(ModelDeleteFinishExecutor.SEGMENT_ID)
public class ModelDeleteFinishExecutor extends Finish<DeleteModelRequestContext, NullResultDto> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_DELETE_FINISH]";
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
    public ModelDeleteFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, NullResultDto.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public NullResultDto finish(DeleteModelRequestContext ctx) {
        metaModelService.deleteModel(ctx);
        return NullResultDto.getInstance();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpdateModelRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
