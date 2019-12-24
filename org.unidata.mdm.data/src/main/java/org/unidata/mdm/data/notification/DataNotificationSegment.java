package org.unidata.mdm.data.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.service.BusService;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

import java.util.Collections;
import java.util.function.BiConsumer;

/**
 * @author Alexander Malyshev
 */
@Component(DataNotificationSegment.SEGMENT_ID)
public class DataNotificationSegment extends Point<PipelineInput> {

    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[AUDIT_DATA_SEGMENT]";

    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".audit.data.segment";

    @Autowired
    private BiConsumer<String, Object> dataSender;

    /**
     * Constructor.
     */
    public DataNotificationSegment() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    @Override
    public void point(PipelineInput ctx) {
        dataSender.accept(
                NotificationDataUtils.eventType(ctx),
                Collections.singletonMap(NotificationDataConstants.CONTEXT_FILED, ctx)
        );
    }

    @Override
    public boolean supports(Start<?> start) {
        final Class<?> inputTypeClass = start.getInputTypeClass();
        return UpsertRequestContext.class.isAssignableFrom(inputTypeClass)
                || GetRequestContext.class.isAssignableFrom(inputTypeClass)
                || DeleteRequestContext.class.isAssignableFrom(inputTypeClass);
    }
}
