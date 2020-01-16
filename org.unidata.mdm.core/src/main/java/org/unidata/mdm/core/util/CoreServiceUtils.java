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

package org.unidata.mdm.core.util;

import org.unidata.mdm.core.configuration.CoreConfiguration;
import org.unidata.mdm.core.service.ConfigurationService;
import org.unidata.mdm.core.service.SecurityService;
import org.unidata.mdm.system.service.PlatformConfiguration;

/**
 * @author Mikhail Mikhailov
 * Static services access.
 */
public final class CoreServiceUtils {
    /**
     * Security service instance.
     */
    private static SecurityService securityService;
    /**
     * Configuration service instance.
     */
    private static ConfigurationService configurationService;
    /**
     * Platform configuration instance.
     */
    private static PlatformConfiguration platformConfiguration;
    /**
     * Constructor.
     */
    private CoreServiceUtils() {
        super();
    }
    /**
     * The init method.
     * @param applicationContext
     */
    public static void init() {
        securityService = CoreConfiguration.getBean(SecurityService.class);
        configurationService = null;//CoreSpringConfiguration.getBean(ConfigurationService.class);//TODO @Modules
        platformConfiguration = CoreConfiguration.getBean(PlatformConfiguration.class);
    }
    /**
     * @return the securityService
     */
    public static SecurityService securityService() {
        return securityService;
    }
    /**
     * @return the configurationService
     */
    public static ConfigurationService configurationService() {
        return configurationService;
    }
    /**
     * @return the platformConfiguration
     */
    public static PlatformConfiguration platformConfiguration() {
        return platformConfiguration;
    }
}
