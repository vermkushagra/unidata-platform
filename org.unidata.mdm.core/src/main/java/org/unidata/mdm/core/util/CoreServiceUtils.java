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
