package org.unidata.mdm.data.service.segments;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.GetRecordIntervalRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRecordsComponent;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.pipeline.Pipeline;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov
 * Simple context validity checker and key finder.
 */
@Component(RecordGetStartExecutor.SEGMENT_ID)
public class RecordGetStartExecutor extends Start<GetRequestContext> implements RecordIdentityContextSupport {
    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordGetStartExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_GET_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.get.start.description";
    /**
     * The pipeline service.
     */
    @Autowired
    private PipelineService pipelineService;
    /**
     * Constructor.
     */
    public RecordGetStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, GetRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(GetRequestContext ctx) {

        // Keys already supplied. Load interval and exit.
        if (Objects.nonNull(ctx.keys())) {
            ensureTimeline(ctx);
            return;
        }

        // 1. Check input
        if (!ctx.isValidRecordKey()) {
            final String message = "Ivalid input. Request context is not capable for record identification.";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_GET_INVALID_INPUT, ctx);
        }

        // 2. Identify
        RecordKeys keys = ensureTimeline(ctx);
        if (keys == null) {
            final String message = "Record not found by supplied keys etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";
            LOGGER.warn(message, ctx.getEtalonKey(), ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName());
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_GET_NOT_FOUND_BY_SUPPLIED_KEYS,
                    ctx.getEtalonKey(), ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName());
        }

        // Additional DIT check
        if (ctx.getEntityName() != null &&  keys.getEntityName() != null && !ctx.getEntityName().equals(keys.getEntityName())) {
            throw new DataProcessingException("Etalon id found '{}', but supplied entity name '{}' and key's entity name '{}' do not match.",
                    DataExceptionIds.EX_ENTITY_NAME_AND_ETALON_ID_MISMATCH,
                    ctx.getEtalonKey(), ctx.getEntityName(), keys.getEntityName());
        }
    }

    private RecordKeys ensureTimeline(GetRequestContext ctx) {

        // 1. Load interval view.
        Timeline<OriginRecord> timeline = commonRecordsComponent.loadInterval(GetRecordIntervalRequestContext.builder(ctx)
                .fetchData(true)
                .build());

        ctx.currentTimeline(timeline);
        ctx.keys(timeline.getKeys());

        // 3. Return and fail, if keys are null
        return timeline.getKeys();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pipeline select(GetRequestContext ctx) {

        Pipeline result = null;

        // 1. Check for entity name, being present.
        String entityName = selectEntityName(ctx);
        if (Objects.nonNull(entityName)) {
            result = pipelineService.getPipeline(entityName);
        // 2. Do resolve (load keys and timeline), if entity name is not present.
        } else {
            RecordKeys keys = ensureTimeline(ctx);
            if (Objects.nonNull(keys)) {
                entityName = keys.getEntityName();
                result = pipelineService.getPipeline(keys.getEntityName());
            }
        }

        // 3. Throw, if nothing works, because this indicates invalid input
        if (Objects.isNull(result)) {
            final String message = "No configured pipeline for entity name '{}'.";
            LOGGER.warn(message, entityName);
            throw new PlatformFailureException(message, DataExceptionIds.EX_DATA_GET_RECORD_NO_SELECTABLE_PIPELINE, entityName);
        }

        return result;
    }
}
