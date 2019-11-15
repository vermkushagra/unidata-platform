package org.unidata.mdm.core.service;

import org.unidata.mdm.core.type.security.impl.SecurityDataSource;

import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public interface SecurityConfigurationService {
    /**
     * Gets all registered security data sources.
     * @return map
     */
    Map<String, SecurityDataSource> getSecurityDataSources();
}
