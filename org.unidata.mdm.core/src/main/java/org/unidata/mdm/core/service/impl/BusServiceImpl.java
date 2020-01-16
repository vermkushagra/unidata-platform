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

package org.unidata.mdm.core.service.impl;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.ModelHelper;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.RouteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.dto.BusMessage;
import org.unidata.mdm.core.notification.NotificationSystemConstants;
import org.unidata.mdm.core.service.BusService;
import org.unidata.mdm.core.util.SystemInfoUtil;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class BusServiceImpl implements BusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusServiceImpl.class);

    private final ModelCamelContext camelContext;

    public BusServiceImpl(final ModelCamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    public void send(@Nonnull String target, @Nonnull BusMessage busMessage) {
        final Map<String, Object> headers = new HashMap<>(busMessage.getHeaders());
        headers.putAll(SystemInfoUtil.systemInfo());
        headers.put(NotificationSystemConstants.WHEN_HAPPENED, LocalDateTime.now());
        try (final ProducerTemplate producerTemplate = camelContext.createProducerTemplate()) {
            producerTemplate.sendBodyAndHeaders(target, busMessage.getBody(), headers);
        } catch (IOException e) {
            LOGGER.error("Can't send message to {}", target, e);
        }
    }

    @Override
    public Consumer<BusMessage> sender(@Nonnull final String target) {
        return busMessage -> send(target, busMessage);
    }
}
