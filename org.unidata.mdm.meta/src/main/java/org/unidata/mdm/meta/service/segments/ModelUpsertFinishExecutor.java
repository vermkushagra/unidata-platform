package org.unidata.mdm.meta.service.segments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.system.dto.VoidResultDto;
import org.unidata.mdm.meta.module.MetaModule;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Finish;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author maria.chistyakova
 * @since  06.12.2019
 */
@Component(ModelUpsertFinishExecutor.SEGMENT_ID)
public class ModelUpsertFinishExecutor extends Finish<UpdateModelRequestContext, VoidResultDto> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = MetaModule.MODULE_ID + "[MODEL_UPSERT_FINISH]";
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
    public ModelUpsertFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, VoidResultDto.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public VoidResultDto finish(UpdateModelRequestContext ctx) {
        metaModelService.upsertModel(ctx);
        return VoidResultDto.getInstance();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpdateModelRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}