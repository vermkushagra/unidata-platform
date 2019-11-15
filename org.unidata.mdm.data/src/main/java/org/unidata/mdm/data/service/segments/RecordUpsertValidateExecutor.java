package org.unidata.mdm.data.service.segments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.RecordValidationService;
import org.unidata.mdm.data.type.keys.RecordKeys;
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
     * Logger for this bean.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordUpsertValidateExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_VALIDATE_POINT]";
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

            // 1. Check input (presence of records themselves)
            if (!ctx.isEtalon() && !ctx.isOrigin()) {
                final String message = "Invalid upsert request context. Either etalon data or origin data or keys invalid / missing. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_NO_INPUT, ctx);
            }

            RecordKeys keys = ctx.keys();

            // 2. Check supplied keys validity.
            if (keys == null && ((ctx.isOrigin() && ctx.isOriginRecordKey()) || ctx.isEtalonRecordKey())) {
                final String message = "Record can not be identified by supplied keys. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_INVALID_KEYS);
            }

            // 3. Origin is inactive, discard updates
            if (keys != null && keys.getOriginKey() != null && keys.getOriginKey().getStatus() == RecordStatus.INACTIVE) {
                final String message = "Origin [Ext. ID: {}, Source system: {}, Entity name: {}] is inactive. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_ORIGIN_INACTIVE,
                        keys.getOriginKey().getExternalId(),
                        keys.getOriginKey().getSourceSystem(),
                        keys.getOriginKey().getEntityName());
            } else if (keys != null && keys.getEtalonKey() != null && keys.getEtalonKey().getStatus() == RecordStatus.INACTIVE) {
                final String message = "Etalon [ID: {}] is inactive. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_ETALON_INACTIVE, keys.getEtalonKey().getId());
            }

            // 4. Check key combination validity
            if (ctx.isOrigin() && !ctx.isOriginExternalId() && !ctx.isOriginRecordKey()) {
                final String message = "Cannot upsert origin record. Neither valid external id nor origin record key has been supplied. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_INVALID_ORIGIN_INPUT);
            }

            // 5. Check source system.
            if (ctx.isOrigin() && ctx.isOriginExternalId() && metaModelService.getSourceSystemById(ctx.getSourceSystem()) == null) {
                String message = "Valid source system should be defined.";
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_UPSERT_NO_SOURCE_SYSTEM);
            }

            // 6. Check entity name and  supplied data consistency
            DataRecord record = ctx.getRecord();
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
