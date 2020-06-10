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
     * The user service
     */
    private static UserService userService;

    /**
     * The role service
     */
    private static RoleService roleService;
    /**
     * The WF service.
     */
    private static WorkflowService workflowService;
    /**
     * The configuration service.
     */
    private static ConfigurationService configurationService;
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
     * @param securityService the service to set
     * @param userService the service to set
     * @param roleService the service to set
     * @param workflowService the service instance to set
     * @param configurationService the service to set
     */
    @SuppressWarnings("all")
    public static void init(
            PlatformConfiguration platformConfiguration,
            DataRecordsService dataRecordsService,
            MetaModelService metaModelService,
            CleanseFunctionService cleanseFunctionService,
            SearchService searchService,
            SecurityService securityService,
            UserService userService,
            RoleService roleService,
            WorkflowService workflowService,
            ConfigurationService configurationService
    ) {

        ServiceUtils.platformConfiguration = platformConfiguration;
        ServiceUtils.cleanseFunctionService = cleanseFunctionService;
        ServiceUtils.dataRecordsService = dataRecordsService;
        ServiceUtils.metaModelService = metaModelService;
        ServiceUtils.searchService = searchService;
        ServiceUtils.securityService = securityService;
        ServiceUtils.userService = userService;
        ServiceUtils.roleService = roleService;
        ServiceUtils.workflowService = workflowService;
        ServiceUtils.configurationService = configurationService;
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
    /**
     * @return the securityService
     */
    public static SecurityService getSecurityService() {
        return securityService;
    }
    /**
     * @return the userService
     */
    public static UserService getUserService() {
        return userService;
    }
    /**
     * @return the roleService
     */
    public static RoleService getRoleService() {
        return roleService;
    }
    /**
     * @return the workflowService
     */
    public static WorkflowService getWorkflowService() {
        return workflowService;
    }
    /**
     * @return the configurationService
     */
    public static ConfigurationService getConfigurationService() {
        return configurationService;
    }
}
