package org.unidata.mdm.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.service.SecurityConfigurationService;
import org.unidata.mdm.core.type.security.impl.SecurityDataSource;
import org.unidata.mdm.core.util.SecurityUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Malyshev
 */
@Service
public class SecurityConfigurationServiceImpl implements SecurityConfigurationService {

    /**
     * Configured security data sources.
     */
    private Map<String, SecurityDataSource> securityDataSources;

    /**
     * Standard security data source.
     */
    @Autowired
    private StandardSecurityDataProvider standardSecurityDataProvider;

    @PostConstruct
    public void init() {
        finishSystemDefaultSecurityDataSource(null);
    }

    /**
     * Init system default security data source.
     */
    private void finishSystemDefaultSecurityDataSource(Map<String, SecurityDataSource> sources) {

        Map<String, SecurityDataSource> collected = Objects.isNull(sources)
                ? new LinkedHashMap<>()
                : sources;

        collected.put(SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE,
                new SecurityDataSource(SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE,
                        "System default security data source.",
                        standardSecurityDataProvider,
                        standardSecurityDataProvider,
                        standardSecurityDataProvider));

        securityDataSources = Collections.unmodifiableMap(collected);
    }

    @Override
    public Map<String, SecurityDataSource> getSecurityDataSources() {
        return securityDataSources;
    }
}
