package com.unidata.mdm.backend.common.service;

import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;

/**
 * @author Mikhail Mikhailov
 * Some interfaces, visible also from UE and integration code, among other.
 */
public class ServiceUtils {
    /**
     * Data records service.
     */
    private static DataRecordsService dataRecordsService;
    /**
     * Meta model service.
     */
    private static MetaModelService metaModelService;
    /**
     * Cleanse function service.
     */
    private static CleanseFunctionService cleanseFunctionService;
    /**
     * The search service.
     */
    private static SearchService searchService;
    /**
     * The platform configuration.
     */
    private static PlatformConfiguration platformConfiguration;
    /**
     * The security service
     */
    private static SecurityService securityService;
    /**
     * The role service
     */
    private static RoleService roleService;

    /**
     * Constructor.
     */
    private ServiceUtils() {
        super();
    }
    /**
     * Init.
     * @param platformConfiguration current platform configuration
     * @param dataRecordsService the service to set
     * @param metaModelService the service to set
     * @param cleanseFunctionService the service to set
     * @param searchService the service to set
     */
    public static void init(
            PlatformConfiguration platformConfiguration,
            DataRecordsService dataRecordsService,
            MetaModelService metaModelService,
            CleanseFunctionService cleanseFunctionService,
            SearchService searchService,
            SecurityService securityService,
            RoleService roleService
    ) {

        ServiceUtils.platformConfiguration = platformConfiguration;
        ServiceUtils.cleanseFunctionService = cleanseFunctionService;
        ServiceUtils.dataRecordsService = dataRecordsService;
        ServiceUtils.metaModelService = metaModelService;
        ServiceUtils.searchService = searchService;
        ServiceUtils.securityService = securityService;
        ServiceUtils.roleService = roleService;
    }
    /**
     * @return the platformConfiguration
     */
    public static PlatformConfiguration getPlatformConfiguration() {
        return platformConfiguration;
    }
    /**
     * @return the dataRecordsService
     */
    public static DataRecordsService getDataRecordsService() {
        return dataRecordsService;
    }
    /**
     * @return the metaModelService
     */
    public static MetaModelService getMetaModelService() {
        return metaModelService;
    }
    /**
     * @return the cleanseFunctionService
     */
    public static CleanseFunctionService getCleanseFunctionService() {
        return cleanseFunctionService;
    }
    /**
     * @return the searchService
     */
    public static SearchService getSearchService() {
        return searchService;
    }

    public static SecurityService getSecurityService() {
        return securityService;
    }

    public static RoleService getRoleService() {
        return roleService;
    }
}
