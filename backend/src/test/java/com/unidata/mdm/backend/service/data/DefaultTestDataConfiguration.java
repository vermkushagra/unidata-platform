package com.unidata.mdm.backend.service.data;

import java.io.IOException;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.mock.env.MockPropertySource;

import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.service.CleanseFunctionService;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.RoleService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.dao.AuditDao;
import com.unidata.mdm.backend.dao.ClassifiersDAO;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.dao.LargeObjectsDao;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.dao.RelationsDao;
import com.unidata.mdm.backend.dao.RoleDao;
import com.unidata.mdm.backend.dao.UserDao;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.cleanse.CleanseFunctionServiceExt;
import com.unidata.mdm.backend.service.cleanse.DataQualityServiceExt;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.data.util.DataUtils;
import com.unidata.mdm.backend.service.data.xlsximport.DataImportService;
import com.unidata.mdm.backend.service.matching.MatchingGroupsService;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;
import com.unidata.mdm.backend.service.matching.MatchingService;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ie.MetaIEService;
import com.unidata.mdm.backend.service.notification.NotificationService;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.settings.CustomStorageService;
import com.unidata.mdm.backend.service.statistic.StatServiceExt;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.MessageUtils;

/**
 * @author Mikhail Mikhailov
 * Spring configuration for testing data.
 */
@Configuration(value = "defaultTestDataConfiguration")
@Profile(DefaultTestDataConfiguration.TEST_DATA_PROFILE_NAME)
@PropertySource(
    factory = com.unidata.mdm.backend.service.data.DefaultTestDataConfiguration.MockPropertySourceFactory.class,
    value = { "" }
)
@ComponentScan(
    basePackages = {
            "com.unidata.mdm.backend.service",
            "com.unidata.mdm.backend.converter"
    },
    excludeFilters = {
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.job.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.wf.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.cleanse.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.model.impl.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.model.draft.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.notification.impl.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.search.impl.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.settings.impl.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.service.statistic.impl.*"),
            @Filter(type = FilterType.REGEX, pattern = "com.unidata.mdm.backend.dao.impl.*"),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.unidata.mdm.backend.service.configuration.MetaDataReindexComponent.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.unidata.mdm.backend.service.classifier.ClsfServiceImpl.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.unidata.mdm.backend.service.configuration.impl.ConfigurationServiceImpl.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.unidata.mdm.backend.service.matching.ClusterServiceImpl.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.unidata.mdm.backend.service.matching.MatchingGroupServiceImpl.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.unidata.mdm.backend.service.matching.MatchingRuleServiceImpl.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.unidata.mdm.backend.service.measurement.MeasurementServiceImpl.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = com.unidata.mdm.backend.service.model.draft.MetaDraftServiceImpl.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = MetaIEService.class),
            @Filter(type = FilterType.ASSIGNABLE_TYPE, value = DataImportService.class)
    }
)
// @Filter(type = FilterType.ASSIGNABLE_TYPE, value = MetaDependencyService.class),
//, "classpath:/spring/audit_stub.xml"
@ImportResource({ "classpath:/spring/core.xml", "classpath:/spring/matching.xml" })
public class DefaultTestDataConfiguration implements ApplicationContextAware {
    /**
     * Name of this profile.
     */
    public static final String TEST_DATA_PROFILE_NAME = "test-data";
    // Mocks.
    // DAO
    @Mock
    private DataRecordsDao dataRecordsDaoMock;
    @Mock
    private OriginsVistoryDao originsVistoryDaoMock;
    @Mock
    private RelationsDao relationsDaoMock;
    @Mock
    private ClassifiersDAO classifiersDaoMock;
    @Mock
    private LargeObjectsDao largeObjectsDaoMock;
    @Mock
    private UserDao userDaoMock;
    @Mock
    private RoleDao roleDaoMock;

    // Subsystems
    // Search
    @Mock
    private SearchServiceExt searchServiceExtMock;

    // Matching
    @Mock
    private ClusterService clusterServiceMock;
    @Mock
    private MatchingService matchingServiceMock;
    @Mock
    private MatchingGroupsService matchingGroupsServiceMock;
    @Mock
    private MatchingRulesService matchingRulesService;

    // MM
    @Mock
    private MetaModelServiceExt metaModelServiceMock;
    @Mock
    private MetaDraftService metaDraftServiceMock;
    @Mock
    private MetaMeasurementService metaMeasurementServiceMock;

    // HZ
    @Mock
    private HazelcastInstance hazelcastInstanceMock;

    // Audit
    @Mock
    private AuditDao auditDaoMock;

    // CLSF MM
    @Mock
    private ClsfService clsfServiceMock;

    // Notifications
    @Mock
    private NotificationService notificationServiceMock;

    // Ext. configuration
    @Mock
    private ConfigurationService configurationServiceMock;

    // Storage
    @Mock
    private CustomStorageService customStorageServiceMock;

    // Statistics
    @Mock
    private StatServiceExt statServiceExtMock;

    // Security
    @Mock
    private SecurityServiceExt securityServiceExtMock;

    // DQService
    @Mock
    private DataQualityServiceExt dataQualityServiceExtMock;

    @Mock
    private CleanseFunctionServiceExt cleanseFunctionServiceExtMock;

    private ApplicationContext applicationContext;

    /**
     * Constructor.
     */
    public DefaultTestDataConfiguration() {
        super();
        MockitoAnnotations.initMocks(this);
    }
    /**
     * @author Mikhail Mikhailov
     * Mock property source factory.
     */
    public static class MockPropertySourceFactory implements PropertySourceFactory {
        /**
         * {@inheritDoc}
         */
        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
            return new MockPropertySource()
                    .withProperty(ConfigurationConstants.UNIDATA_NODE_ID_PROPERTY, "777777777777")
                    .withProperty(ConfigurationConstants.UNIDATA_DUMP_TARGET_FORMAT_PROPERTY, "PROTOSTUFF")
                    .withProperty(ConfigurationConstants.PLATFORM_VERSION_PROPERTY, "4.7")
                    .withProperty(ConfigurationConstants.API_VERSION_PROPERTY, "5.0")
                    .withProperty(ConfigurationConstants.DB_CLEAN_PROPERTY, "false")
                    .withProperty(ConfigurationConstants.DB_MIGRATE_PROPERTY, "false")
                    .withProperty(ConfigurationConstants.SMOKE_STAND_FLAG_PROPERTY, "false")
                    .withProperty(ConfigurationConstants.DEFAULT_LOCALE_PROPERTY, "en")
                    .withProperty(ConfigurationConstants.SEARCH_CLUSTER_NAME_PROPERTY, "test-cluster")
                    .withProperty(ConfigurationConstants.SEARCH_NODES_NAME_PROPERTY, "localhost:9300");
        }
    }
    /**
     * Does reset mocks.
     */
    public void resetMocks() {

        Mockito.reset(
                dataRecordsDaoMock,
                originsVistoryDaoMock,
                relationsDaoMock,
                classifiersDaoMock,
                largeObjectsDaoMock,
                userDaoMock,
                roleDaoMock,
                searchServiceExtMock,
                clusterServiceMock,
                matchingServiceMock,
                matchingGroupsServiceMock,
                matchingRulesService,
                metaModelServiceMock,
                metaDraftServiceMock,
                metaMeasurementServiceMock,
                hazelcastInstanceMock,
                auditDaoMock,
                clsfServiceMock,
                notificationServiceMock,
                configurationServiceMock,
                customStorageServiceMock,
                statServiceExtMock,
                securityServiceExtMock,
                dataQualityServiceExtMock,
                cleanseFunctionServiceExtMock);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
        MessageUtils.init(this.applicationContext);
        IdUtils.init(this.applicationContext);
        SecurityUtils.init(this.applicationContext);
        DataUtils.init(this.applicationContext);
        DataRecordUtils.init(this.applicationContext);
    }
    /**
     * @return the applicationContext
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    /**
     * Properties placeholder configuration.
     * @return configurator
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContext ctx = event.getApplicationContext();

        // 1. "Known" utility class initializers
        MessageUtils.init(ctx);
        IdUtils.init(ctx);
        SecurityUtils.init(ctx);
        DataUtils.init(ctx);

        // 2. Utilities, requiring initialized services from above
        ServiceUtils.init(
                ctx.getBean(PlatformConfiguration.class),
                ctx.getBean(DataRecordsService.class),
                ctx.getBean(MetaModelService.class),
                ctx.getBean(CleanseFunctionService.class),
                ctx.getBean(SearchService.class),
                ctx.getBean(SecurityService.class),
                ctx.getBean(RoleService.class)
        );

        DataRecordUtils.init(ctx);
        // ValidityPeriodUtils.init(ctx);
    }

    // Mocks
    // DAO objects
    /**
     * @return the dataRecordsDaoMock
     */
    @Bean
    @Primary
    public DataRecordsDao getDataRecordsDaoMock() {
        return dataRecordsDaoMock;
    }
    /**
     * @return the originsVistoryDaoMock
     */
    @Bean
    @Primary
    public OriginsVistoryDao getOriginsVistoryDaoMock() {
        return originsVistoryDaoMock;
    }
    /**
     * @return the relationsDaoMock
     */
    @Bean
    @Primary
    public RelationsDao getRelationsDaoMock() {
        return relationsDaoMock;
    }
    /**
     * @return the classifiersDaoMock
     */
    @Bean
    @Primary
    public ClassifiersDAO getClassifiersDaoMock() {
        return classifiersDaoMock;
    }
    /**
     * @return the largeObjectsDaoMock
     */
    @Bean
    @Primary
    public LargeObjectsDao getLargeObjectsDaoMock() {
        return largeObjectsDaoMock;
    }
    /**
     * @return the userDaoMock
     */
    @Bean
    @Primary
    public UserDao getUserDaoMock() {
        return userDaoMock;
    }
    /**
     * @return the roleDaoMock
     */
    @Bean
    @Primary
    public RoleDao getRoleDaoMock() {
        return roleDaoMock;
    }
    /**
     * @return the searchServiceExtMock
     */
    @Bean
    @Primary
    public SearchServiceExt getSearchServiceExtMock() {
        return searchServiceExtMock;
    }
    /**
     * @return the clusterServiceMock
     */
    @Bean
    @Primary
    public ClusterService getClusterServiceMock() {
        return clusterServiceMock;
    }
    /**
     * @return the matchingServiceMock
     */
    @Bean
    @Primary
    public MatchingService getMatchingServiceMock() {
        return matchingServiceMock;
    }
    /**
     * @return the matchingGroupsServiceMock
     */
    @Bean
    @Primary
    public MatchingGroupsService getMatchingGroupsServiceMock() {
        return matchingGroupsServiceMock;
    }
    /**
     * @return the matchingRulesService
     */
    @Bean
    @Primary
    public MatchingRulesService getMatchingRulesService() {
        return matchingRulesService;
    }
    /**
     * @return the metaModelServiceMock
     */
    @Bean
    @Primary
    public MetaModelServiceExt getMetaModelServiceMock() {
        return metaModelServiceMock;
    }
    /**
     * @return the metaDraftServiceMock
     */
    @Bean
    @Primary
    public MetaDraftService getMetaDraftServiceMock() {
        return metaDraftServiceMock;
    }
    /**
     * @return the metaMeasurementServiceMock
     */
    @Bean
    @Primary
    public MetaMeasurementService getMetaMeasurementServiceMock() {
        return metaMeasurementServiceMock;
    }
    /**
     * @return the hazelcastInstanceMock
     */
    @Bean
    @Primary
    public HazelcastInstance getHazelcastInstanceMock() {
        return hazelcastInstanceMock;
    }
    /**
     * @return the auditDaoMock
     */
    @Bean
    @Primary
    public AuditDao getAuditDaoMock() {
        return auditDaoMock;
    }
    /**
     * @return the clsfServiceMock
     */
    @Bean
    @Primary
    public ClsfService getClsfServiceMock() {
        return clsfServiceMock;
    }
    /**
     * @return the notificationServiceMock
     */
    @Bean
    @Primary
    public NotificationService getNotificationServiceMock() {
        return notificationServiceMock;
    }
    /**
     * @return the configurationServiceMock
     */
    @Bean
    @Primary
    public ConfigurationService getConfigurationServiceMock() {
        return configurationServiceMock;
    }
    /**
     * @return the customStorageServiceMock
     */
    @Bean
    @Primary
    public CustomStorageService getCustomStorageServiceMock() {
        return customStorageServiceMock;
    }
    /**
     * @return the statServiceExtMock
     */
    @Bean
    @Primary
    public StatServiceExt getStatServiceExtMock() {
        return statServiceExtMock;
    }
    /**
     * @return the securityServiceExtMock
     */
    @Bean
    @Primary
    public SecurityServiceExt getSecurityServiceExtMock() {
        return securityServiceExtMock;
    }
    /**
     * @return the dataQualityServiceExtMock
     */
    @Bean
    @Primary
    public DataQualityServiceExt getDataQualityServiceExtMock() {
        return dataQualityServiceExtMock;
    }
    /**
     * @return the cleanseFunctionServiceExtMock
     */
    @Bean
    @Primary
    public CleanseFunctionServiceExt getCleanseFunctionServiceExtMock() {
        return cleanseFunctionServiceExtMock;
    }
}
