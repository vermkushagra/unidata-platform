package org.unidata.mdm.data.notification;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.notification.NotificationSystemConstants;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.Start;

import java.util.function.BiConsumer;

/**
 * @author Alexander Malyshev
 */
@Component(DataSendNotificationFallback.SEGMENT_ID)
public class DataSendNotificationFallback extends Fallback<PipelineInput> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[AUDIT_DATA_FALLBACK]";

    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".audit.data.fallback";

    private BiConsumer<String, Object> dataSender;

    public DataSendNotificationFallback(final BiConsumer<String, Object> dataSender) {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
        this.dataSender = dataSender;
    }

    @Override
    public void accept(PipelineInput pipelineExecutionContext, Throwable throwable) {
        dataSender.accept(
                NotificationDataUtils.eventType(pipelineExecutionContext),
                Maps.of(
                        NotificationDataConstants.CONTEXT_FILED, pipelineExecutionContext,
                        NotificationSystemConstants.EXCEPTION, throwable
                )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return true;
    }
}
