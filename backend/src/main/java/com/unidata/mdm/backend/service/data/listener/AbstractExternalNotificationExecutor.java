package com.unidata.mdm.backend.service.data.listener;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.context.CommonSendableContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.notification.Notification;
import com.unidata.mdm.backend.service.notification.NotificationService;
import com.unidata.mdm.backend.service.notification.ProcessedAction;
import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import com.unidata.mdm.backend.service.notification.messages.UnidataMessage;

/**
 * Abstract class for all notification executors.
 *
 * @param <T> the generic type
 */
public abstract class AbstractExternalNotificationExecutor<T extends CommonSendableContext>
        implements DataRecordExecutor<T> {


    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean execute(T context) {

        if (!context.sendNotification()) {
            return true;
        }

        MeasurementPoint.start();
        try {

            UnidataMessageDef message = createMessage(context);
            if (message != null) {
                UnidataMessage unidataMessage = new UnidataMessage(message);
                NotificationConfig notificationConfig = createNotificationConfig(context);
                Notification<UnidataMessage> notification = new Notification<>(notificationConfig, unidataMessage);

                notificationService.notify(notification);
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Creates the message.
     *
     * @param context the context
     * @return the unidata message def
     */
    protected abstract UnidataMessageDef createMessage(T context);

    /**
     * @return type of action.
     */
    protected abstract ProcessedAction getProcessedAction();

    /**
     * @param context -  context
     * @return config for right notification send
     */
    private NotificationConfig createNotificationConfig(T context) {
        RecordKeys keys = getRecordKeys(context);
        NotificationConfig notificationConfig = new NotificationConfig(getProcessedAction(), keys);
        Map<String, Object> headers = context.getCustomMessageHeaders();
        notificationConfig.addAllUserHeaders(headers);
        return notificationConfig;
    }

    /**
     * @param context - context
     * @return record keys
     */
    protected abstract RecordKeys getRecordKeys(T context);
}
