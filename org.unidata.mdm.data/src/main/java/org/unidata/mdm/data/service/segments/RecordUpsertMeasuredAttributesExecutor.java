package org.unidata.mdm.data.service.segments;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.impl.MeasuredAttributeValueConverter;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * Normalize measured attributes before upserting record
 */
@Component(RecordUpsertMeasuredAttributesExecutor.SEGMENT_ID)
public class RecordUpsertMeasuredAttributesExecutor extends Point<PipelineExecutionContext>
        implements RecordIdentityContextSupport, MeasurementMetaSettingSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_MEASURED_ATTRIBUTES]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.measured.attributes.description";
    /**
     * Measurement service
     */
    @Autowired
    private MetaMeasurementService metaMeasurementService;
    /**
     * Meta model service
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Measured attribute converter
     */
    @Autowired
    private MeasuredAttributeValueConverter measuredAttributeValueConverter;

    public RecordUpsertMeasuredAttributesExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    public boolean execute(DataRecord record, String entityName) {
        MeasurementPoint.start();
        try {
            if (record != null) {
                measuredAttributeValueConverter.enrichMeasuredAttributesByBase(record);
                processDataRecord(record, entityName, StringUtils.EMPTY);
            }
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaMeasurementService measurementService() {
        return metaMeasurementService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModelService modelService() {
        return metaModelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void point(PipelineExecutionContext ctx) {

        if (ctx instanceof UpsertRequestContext) {
            UpsertRequestContext urCtx = (UpsertRequestContext) ctx;
            execute(urCtx.getRecord(), selectEntityName(urCtx));
        } else if (ctx instanceof UpsertRelationRequestContext) {
            UpsertRelationRequestContext urCtx = (UpsertRelationRequestContext) ctx;
            execute(urCtx.getRelation(), urCtx.relationName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass())
            || UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass()) ;
    }
}
