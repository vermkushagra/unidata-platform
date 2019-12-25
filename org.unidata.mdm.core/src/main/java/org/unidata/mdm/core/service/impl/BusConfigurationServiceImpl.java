package org.unidata.mdm.core.service.impl;

import org.apache.camel.Route;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.ModelHelper;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.RouteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.dao.RouteDao;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.service.BusConfigurationService;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.service.AfterPlatformStartup;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusConfigurationServiceImpl implements BusConfigurationService, AfterPlatformStartup {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusConfigurationServiceImpl.class);

    private final ModelCamelContext camelContext;

    private final RouteDao routeDao;

    public BusConfigurationServiceImpl(
            final ModelCamelContext camelContext,
            final RouteDao routeDao
    ) {
        this.camelContext = camelContext;
        this.routeDao = routeDao;
    }

    @Transactional
    @Override
    public void upsertRoute(@Nonnull final String routeId, @Nonnull final String routeDefinition) {
        routeDao.upsertRoutes(Maps.of(routeId, routeDefinition));
    }

    @Transactional
    @Override
    public void upsertRoutes(final @Nonnull String routesDefinitions) {
        try {
            final RoutesDefinition routesDefinition = ModelHelper.loadRoutesDefinition(
                    camelContext,
                    new ByteArrayInputStream(routesDefinitions.getBytes(StandardCharsets.UTF_8))
            );
            routeDao.upsertRoutes(
                routesDefinition.getRoutes().stream()
                        .collect(Collectors.toMap(RouteDefinition::getRouteId, this::dumpRoute))
            );
        } catch (Exception e) {
            LOGGER.error("Can't upsert routes {}", routesDefinitions, e);
        }
    }

    private String dumpRoute(@Nonnull final RouteDefinition routeDefinition) {
        try {
            return ModelHelper.dumpModelAsXml(camelContext, routeDefinition);
        } catch (JAXBException e) {
            LOGGER.error("Can't dump route definition {}", routeDefinition, e);
            throw new PlatformFailureException(
                    "Can't dump route definition " + routeDefinition.getRouteId(),
                    e,
                    CoreExceptionIds.EX_FAIL_ROUTE_DUMP,
                    routeDefinition.getRouteId()
            );
        }
    }

    private void addToContext(final String routeId, final RouteDefinition routeDefinition) throws Exception {
        final Route oldRoute = camelContext.getRoute(routeId);
        if (oldRoute != null) {
            final RouteController routeController = camelContext.getRouteController();
            routeController.stopRoute(routeId);
            camelContext.removeRoute(routeId);
        }
        camelContext.addRouteDefinition(routeDefinition);
    }

    @Transactional
    @Override
    public void installRoutes(@Nonnull final Map<String, String> routes) {
        routeDao.upsertRoutes(routes);
        routes.forEach(this::addToContext);
    }

    private void addToContext(final String routeId, final String routeDefinition) {
        try {
            final RoutesDefinition routesDefinition = ModelHelper.loadRoutesDefinition(
                    camelContext,
                    new ByteArrayInputStream(routeDefinition.getBytes(StandardCharsets.UTF_8))
            );
            if (!routesDefinition.getRoutes().isEmpty()) {
                addToContext(routeId, routesDefinition.getRoutes().get(0));
            }
        } catch (Exception e) {
            LOGGER.error("Can't add route {} definition {} to context", routeId, routeDefinition, e);
        }
    }

    @Override
    public Map<String, String> routesDefinitions() {
        return routeDao.routesDefinitions();
    }

    @Override
    public void afterPlatformStartup() {
        routeDao.routesDefinitions().forEach(this::addToContext);
    }
}
