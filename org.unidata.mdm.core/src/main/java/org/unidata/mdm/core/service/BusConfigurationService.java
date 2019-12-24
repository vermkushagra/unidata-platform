package org.unidata.mdm.core.service;

import javax.annotation.Nonnull;
import java.util.Map;

public interface BusConfigurationService {
    void upsertRoute(@Nonnull String routeId, @Nonnull String routeDefinition);
    void upsertRoutes(@Nonnull String routesDefinitions);

    void installRoutes(@Nonnull Map<String, String> routes);

    Map<String, String> routesDefinitions();
}
