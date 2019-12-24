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

    @Override
    public void upsertRoute(@Nonnull final String routeId, @Nonnull final String routeDefinition) {
        try {
            final RoutesDefinition routesDefinition = ModelHelper.loadRoutesDefinition(
                    camelContext,
                    new ByteArrayInputStream(routeDefinition.getBytes(StandardCharsets.UTF_8))
            );
            if (!routesDefinition.getRoutes().isEmpty()) {
                upsertRoute(routeId, routesDefinition.getRoutes().get(0));
            }
        } catch (Exception e) {
            LOGGER.error("Can't upsert route {} definition {}", routeId, routeDefinition, e);
        }
    }

    @Override
    public void upsertRoutes(final @Nonnull String routesDefinitions) {
        try {
            final RoutesDefinition routesDefinition = ModelHelper.loadRoutesDefinition(
                    camelContext,
                    new ByteArrayInputStream(routesDefinitions.getBytes(StandardCharsets.UTF_8))
            );
            routesDefinition.getRoutes().forEach(r -> upsertRoute(r.getId(), r));
        } catch (Exception e) {
            LOGGER.error("Can't upsert routes {}", routesDefinitions, e);
        }
    }

    private void upsertRoute(final String routeId, RouteDefinition routeDefinition) {
        try {
            final Route oldRoute = camelContext.getRoute(routeId);
            if (oldRoute != null) {
                final RouteController routeController = camelContext.getRouteController();
                routeController.stopRoute(routeId);
                camelContext.removeRoute(routeId);
            }
            camelContext.addRouteDefinition(routeDefinition);
        } catch (Exception e) {
            LOGGER.error("Can't upsert route {} definition {}", routeId, routeDefinition, e);
        }
    }
}
