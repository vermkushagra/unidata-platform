package org.unidata.mdm.data.service.segments;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 * Single point for checking and possible adjustment of from - to dates.
 */
@Component(RecordDeletePeriodCheckExecutor.SEGMENT_ID)
public class RecordDeletePeriodCheckExecutor
    extends Point<DeleteRequestContext>
    implements ValidityRangeCheckSupport<DeleteRequestContext>, RecordIdentityContextSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_DELETE_PERIOD_CHECK]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.delete.period.check.description";
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public RecordDeletePeriodCheckExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(DeleteRequestContext ctx) {

        if (!ctx.isInactivatePeriod()) {
            return;
        }

        MeasurementPoint.start();
        try {

            String entityName = selectEntityName(ctx);

            EntityModelElement ew = metaModelService.getEntityModelElementById(entityName);
            if (Objects.isNull(ew)) {
                return;
            }

            execute(ctx, ew.getValidityStart(), ew.getValidityEnd());
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return DeleteRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
