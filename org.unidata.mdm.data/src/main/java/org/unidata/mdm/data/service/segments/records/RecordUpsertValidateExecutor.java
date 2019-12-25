package org.unidata.mdm.data.service.segments.records;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.RecordValidationService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 */
@Component(RecordUpsertValidateExecutor.SEGMENT_ID)
public class RecordUpsertValidateExecutor
        extends Point<UpsertRequestContext>
        implements RecordIdentityContextSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_VALIDATE]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.validate.point.description";
    /**
     * Meta model service instance.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Record against meta model validation service.
     */
    @Autowired
    private RecordValidationService recordValidationService;
    /**
     * Constructor.
     */
    public RecordUpsertValidateExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // Check supplied data consistency
            DataRecord record = ctx.getRecord();
            if (Objects.isNull(record)) {
                return;
            }

            String entityName = selectEntityName(ctx);

            if (metaModelService.isLookupEntity(entityName)) {
                recordValidationService.checkLookupDataRecord(record, entityName);
            } else {
                recordValidationService.checkEntityDataRecord(record, entityName);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
