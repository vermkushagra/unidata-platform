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

package com.unidata.mdm.backend.service.notification.notifiers.jms;

import com.unidata.mdm.backend.service.notification.configs.NotificationConfig;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.unidata.mdm.backend.util.JaxbUtils;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.api.UnidataMessageDef;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.notification.messages.UnidataMessage;
import com.unidata.mdm.backend.service.notification.notifiers.Notifier;

public class CamelMessageNotifier implements Notifier<UnidataMessage> {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CamelMessageNotifier.class);

    /**
     * The Constant INTERNAL_NOTIFICATION_QUEUE.
     */
    private static final String INTERNAL_NOTIFICATION_QUEUE = "vm:notification";

    /**
     * The Constant INTERNAL_ID.
     */
    private static final String SOURCE_SYSTEM = "SOURCE_SYSTEM";

    /**
     * The Constant INTERNAL_ID.
     */
    private static final String INTERNAL_ID = "RECORD_ID";

    /**
     * The Constant EXTERNAL_ID.
     */
    private static final String EXTERNAL_ID = "EXTERNAL_ID";

    /**
     * The Constant ENTITY_NAME.
     */
    private static final String ENTITY_NAME = "ENTITY_NAME";

    /**
     * The Constant PUBLISH_DATE.
     */
    private static final String PUBLISH_DATE = "PUBLISH_DATE";

    /**
     * The Constant EVENT_DATE.
     */
    private static final String EVENT_DATE = "EVENT_DATE";

    /**
     * The Constant EVENT_TYPE.
     */
    private static final String EVENT_TYPE = "EVENT_TYPE";

    /**
     * Notification producer.
     */
    @EndpointInject(uri = INTERNAL_NOTIFICATION_QUEUE)
    private ProducerTemplate notificationProducer;

    @Override
    public void notify(@Nonnull UnidataMessage notification, @Nullable NotificationConfig notificationConfig) {
        UnidataMessageDef message = notification.getUnidataMessage();
        if (notificationProducer == null || message == null) {
            return;
        }
        MeasurementPoint.start();
        try {
            String marshaledMessage = marshal(message);
            if (notificationConfig == null) {
                notificationProducer.sendBody(marshaledMessage);
            } else {
                Map<String, Object> overAllHeaders = new HashMap<>();
                overAllHeaders.putAll(notificationConfig.getUserHeaders());
                overAllHeaders.put(ENTITY_NAME, notificationConfig.getRecordKeys().getEntityName());
                overAllHeaders.put(EVENT_TYPE, message.getEventType().toString());
                overAllHeaders.put(EVENT_DATE, message.getEventDate().toXMLFormat());
                overAllHeaders.put(PUBLISH_DATE, message.getPublishDate().toXMLFormat());
                overAllHeaders.put(INTERNAL_ID, notificationConfig.getRecordKeys().getOriginKey() != null
                        ? notificationConfig.getRecordKeys().getOriginKey().getId()
                        : null);
                overAllHeaders.put(EXTERNAL_ID, notificationConfig.getRecordKeys().getOriginKey() != null
                        ? notificationConfig.getRecordKeys().getOriginKey().getExternalId()
                        : null);
                overAllHeaders.put(SOURCE_SYSTEM, notificationConfig.getRecordKeys().getOriginKey() != null
                        ? notificationConfig.getRecordKeys().getOriginKey().getSourceSystem()
                        : null);
                notificationProducer.sendBodyAndHeaders(marshaledMessage, overAllHeaders);
            }
        } catch (Exception e) {
            LOGGER.error("Notification over camel cannot be send: {}", e);
            throw new BusinessException("Notification over camel cannot be send", ExceptionId.EX_SYSTEM_NOTIFICATION_FAILED, e);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Serialize message to XML string.
     *
     * @param message unidata notification message.
     * @return XML representation of the unidata notification message(as a
     * String)
     * @throws JAXBException If marshalling wasn't successfully finished.
     */
    private String marshal(UnidataMessageDef message) throws JAXBException {
        final Marshaller marshaller = JaxbUtils.getAPIContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final StringWriter stringWriter = new StringWriter();
        marshaller.marshal(
                new JAXBElement<>(new QName("", "unidataMessage"), UnidataMessageDef.class, message),
                stringWriter);
        return stringWriter.toString();
    }
}
