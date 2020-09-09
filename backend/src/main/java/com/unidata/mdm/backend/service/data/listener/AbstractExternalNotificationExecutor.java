/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
