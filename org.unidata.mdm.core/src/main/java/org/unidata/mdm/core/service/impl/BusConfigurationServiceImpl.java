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
import org.unidata.mdm.core.dao.BusRouteDao;
import org.unidata.mdm.core.dto.BusRoutesDefinition;
import org.unidata.mdm.core.service.BusConfigurationService;
import org.unidata.mdm.system.service.AfterPlatformStartup;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service
public class BusConfigurationServiceImpl implements BusConfigurationService, AfterPlatformStartup {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusConfigurationServiceImpl.class);

    private final ModelCamelContext camelContext;

    private final BusRouteDao busRouteDao;

    public BusConfigurationServiceImpl(
            final ModelCamelContext camelContext,
            final BusRouteDao busRouteDao
    ) {
        this.camelContext = camelContext;
        this.busRouteDao = busRouteDao;
    }

    @Transactional
    @Override
    public void upsertBusRoutesDefinition(@Nonnull BusRoutesDefinition busRoutesDefinition) {
        busRouteDao.upsertBusRoutesDefinitions(Collections.singleton(busRoutesDefinition));
    }

//    @Transactional
//    @Override
//    public void upsertRoutes(final @Nonnull String routesDefinitions) {
//        try {
//            final RoutesDefinition routesDefinition = ModelHelper.loadRoutesDefinition(
//                    camelContext,
//                    new ByteArrayInputStream(routesDefinitions.getBytes(StandardCharsets.UTF_8))
//            );
//            routeDao.upsertBusRoutesDefinitions(
//                routesDefinition.getRoutes().stream()
//                        .collect(Collectors.toMap(RouteDefinition::getRouteId, this::dumpRoute))
//            );
//        } catch (Exception e) {
//            LOGGER.error("Can't upsert routes {}", routesDefinitions, e);
//        }
//    }

//    private String dumpRoute(@Nonnull final RouteDefinition routeDefinition) {
//        try {
//            return ModelHelper.dumpModelAsXml(camelContext, routeDefinition);
//        } catch (JAXBException e) {
//            LOGGER.error("Can't dump route definition {}", routeDefinition, e);
//            throw new PlatformFailureException(
//                    "Can't dump route definition " + routeDefinition.getRouteId(),
//                    e,
//                    CoreExceptionIds.EX_FAIL_ROUTE_DUMP,
//                    routeDefinition.getRouteId()
//            );
//        }
//    }

    private void addToContext(final RouteDefinition routeDefinition) {
        try {
            final Route oldRoute = camelContext.getRoute(routeDefinition.getRouteId());
            if (oldRoute != null) {
                final RouteController routeController = camelContext.getRouteController();
                routeController.stopRoute(routeDefinition.getRouteId());
                camelContext.removeRoute(routeDefinition.getRouteId());
            }
            camelContext.addRouteDefinition(routeDefinition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public void installBusRoutesDefinition(@Nonnull final BusRoutesDefinition busRoutesDefinition) {
        upsertBusRoutesDefinition(busRoutesDefinition);
        addToContext(busRoutesDefinition);
    }

    private void addToContext(final BusRoutesDefinition busRoutesDefinition) {
        try {
            final RoutesDefinition routesDefinition = ModelHelper.loadRoutesDefinition(
                    camelContext,
                    new ByteArrayInputStream(
                            busRoutesDefinition.getRoutesDefinition().getBytes(StandardCharsets.UTF_8)
                    )
            );
            if (!routesDefinition.getRoutes().isEmpty()) {
                routesDefinition.getRoutes().forEach(this::addToContext);
            }
        } catch (Exception e) {
            LOGGER.error(
                    "Can't add bus routes {} definition {} to context",
                    busRoutesDefinition.getRoutesDefinitionId(),
                    busRoutesDefinition.getRoutesDefinition(),
                    e
            );
        }
    }

    @Override
    public List<BusRoutesDefinition> busRoutesDefinitions() {
        return busRouteDao.fetchBusRoutesDefinitions();
    }

    @Override
    public void afterPlatformStartup() {
        busRouteDao.fetchBusRoutesDefinitions().forEach(this::addToContext);
    }
}
