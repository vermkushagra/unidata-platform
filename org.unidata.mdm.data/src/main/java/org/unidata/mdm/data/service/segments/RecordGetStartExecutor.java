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

        // 1. Already processed by .subject
        if (Objects.nonNull(ctx.keys()) && Objects.nonNull(ctx.currentTimeline())) {
            return;
        }

        // 2. Keys already supplied, but the TL is still to be loaded. Load interval and exit.
        if (Objects.nonNull(ctx.keys()) && Objects.isNull(ctx.currentTimeline())) {
            ensureTimeline(ctx);
            return;
        }

        // 3. Check input
        if (!ctx.isValidRecordKey()) {
            final String message = "Ivalid input. Request context is not capable for record identification.";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, DataExceptionIds.EX_DATA_GET_INVALID_INPUT, ctx);
        }

        // 4. Identify and load interval
        ensureTimeline(ctx);
    }

    /**
     * Both - validate and loading the timeline
     * @param ctx
     * @return
     */
    private RecordKeys ensureTimeline(GetRequestContext ctx) {

        // 1. Load interval view.
        Timeline<OriginRecord> timeline = commonRecordsComponent.loadInterval(GetRecordIntervalRequestContext.builder(ctx)
                .fetchData(true)
                .build());

        RecordKeys keys = timeline.getKeys();
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

        ctx.currentTimeline(timeline);
        ctx.keys(keys);

        // 3. Return and fail, if keys are null
        return timeline.getKeys();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(GetRequestContext ctx) {

        // 1. Check for entity name, being present.
        String entityName = selectEntityName(ctx);
        if (Objects.nonNull(entityName)) {
            return entityName;
        // 2. Do resolve (load keys and timeline), if entity name is not present.
        } else {
            RecordKeys keys = ensureTimeline(ctx);
            if (Objects.nonNull(keys)) {
                entityName = keys.getEntityName();
                return entityName;
            }
        }

        return null;
    }
}
