package com.unidata.mdm.backend.service.notification.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.MessageDao;
import com.unidata.mdm.backend.po.MessagePO;
import com.unidata.mdm.backend.po.MessagePO.MessageType;
import com.unidata.mdm.backend.service.notification.Notification;
import com.unidata.mdm.backend.service.notification.NotificationAsyncCommitCallbackExecutor;
import com.unidata.mdm.backend.service.notification.NotificationHandler;
import com.unidata.mdm.backend.service.notification.NotificationService;
import com.unidata.mdm.backend.service.notification.NotificationUtils;
import com.unidata.mdm.backend.service.notification.messages.NotificationCacheMessage;
import com.unidata.mdm.backend.service.notification.messages.UnidataMessage;

import reactor.core.publisher.Flux;

@Service
public class NotificationServiceImpl implements NotificationService, ConfigurationUpdatesConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private NotificationHandler notificationHandler;

    @Autowired
    private NotificationAsyncCommitCallbackExecutor asyncCommitCallbackExecutor;

    private AtomicBoolean enabled = new AtomicBoolean(
            (Boolean) UnidataConfigurationProperty.UNIDATA_NOTIFICATION_ENABLED.getDefaultValue().get()
    );

    @Override
    public void notify(@Nonnull Collection<Notification<?>> notifications) {
        if (!enabled.get()) {
            return;
        }
        notifications.stream()
                .filter(Objects::nonNull)
                .forEach(this::notify);
    }

    @Override
    public void notify(@Nonnull Notification<?> notification) {
        if (!enabled.get()) {
            return;
        }

        MessagePO msg = saveMessage(notification);
        executeAsync(msg);
    }

    /**
     *
     * @param notification
     * @return
     */
    private MessagePO saveMessage(Notification<?> notification) {
        MeasurementPoint.start();
        try {

            MessagePO msg = new MessagePO();

            String msgBody = null;

            if (notification.getNotificationMessage() instanceof UnidataMessage) {
                msgBody = NotificationUtils.marshalUnidataMessageDef(((UnidataMessage)notification
                    .getNotificationMessage()).getUnidataMessage());
            }

            NotificationCacheMessage cacheMsg = new NotificationCacheMessage(notification.getNotificationConfig(),
                msgBody);

            msg.setMessage(NotificationUtils.marshalNotificationCacheMessage(cacheMsg));
            msg.setType(MessageType.NOTIFICATION);

            messageDao.insert(msg);

            return msg;
        }
        catch (JsonProcessingException | JAXBException e) {
            throw new SystemRuntimeException("Failed to serialize notification message", ExceptionId.EX_MARSHALLING, e);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     *
     * @param msg
     */
    private void executeAsync(final MessagePO msg) {

        MeasurementPoint.start();
        try {

            final Runnable target = () -> {
                try {
                    notificationHandler.asyncHandleNotification(msg.getId());
                } catch (Throwable e) {
                    LOGGER.error("Notification cannot be handled: " + msg.getId(), e);
                }
            };

            // Submits a command which should be executed after the existing transaction is successfully committed;
            // command generates make call for handler in a separate thread
            asyncCommitCallbackExecutor.execute(target);
        } finally {
            MeasurementPoint.stop();
        }
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String key = UnidataConfigurationProperty.UNIDATA_NOTIFICATION_ENABLED.getKey();
        updates
                .filter(values -> values.containsKey(key) && values.get(key).isPresent())
                .map(values -> (Boolean) values.get(key).get())
                .subscribe(enabled::set);
    }
}
