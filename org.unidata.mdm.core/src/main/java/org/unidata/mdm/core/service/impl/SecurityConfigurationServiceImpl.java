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
