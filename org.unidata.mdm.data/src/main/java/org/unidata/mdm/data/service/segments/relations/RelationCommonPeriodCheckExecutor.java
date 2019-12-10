package org.unidata.mdm.data.service.segments.relations;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.context.MutableValidityRangeContext;
import org.unidata.mdm.data.context.RelationIdentityContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.segments.ValidityRangeCheckSupport;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 * Check dates for relations.
 */
@Component(RelationCommonPeriodCheckExecutor.SEGMENT_ID)
public class RelationCommonPeriodCheckExecutor
    extends Point<PipelineExecutionContext>
    implements ValidityRangeCheckSupport<MutableValidityRangeContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_COMMON_PERIOD_CHECK]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.common.check.period.description";
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public RelationCommonPeriodCheckExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(PipelineExecutionContext ctx) {

        MeasurementPoint.start();
        try {

            RelationIdentityContext target = ctx.narrow();

            Date factoryValidFrom = null;
            Date factoryValidTo = null;

            // Take settings from the 'to' side for containments.
            // Check against system dates only otherwise.
            if (target.relationType() == RelationType.CONTAINS) {

                RelationDef def = metaModelService.getRelationById(target.relationName());
                EntityInfoHolder ew = metaModelService.getValueById(def.getToEntity(), EntityInfoHolder.class);
                if (Objects.nonNull(ew)) {
                    factoryValidFrom = ew.getValidityStart();
                    factoryValidTo = ew.getValidityEnd();
                }
            }

            execute(ctx.narrow(), factoryValidFrom, factoryValidTo);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return RelationIdentityContext.class.isAssignableFrom(start.getInputTypeClass())
            && MutableValidityRangeContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
