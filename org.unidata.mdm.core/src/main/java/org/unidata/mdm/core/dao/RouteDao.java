package org.unidata.mdm.core.dao;

import javax.annotation.Nonnull;
import java.util.Map;

public interface RouteDao {
    boolean upsertRoutes(@Nonnull Map<String, String> routes);
    Map<String, String> routesDefinitions();
}
