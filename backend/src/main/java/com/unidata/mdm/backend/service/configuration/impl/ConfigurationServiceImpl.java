package com.unidata.mdm.backend.service.configuration.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Route;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationProvider;
import com.unidata.mdm.backend.common.integration.auth.AuthorizationProvider;
import com.unidata.mdm.backend.common.integration.auth.ProfileProvider;
import com.unidata.mdm.backend.common.integration.exits.AfterJoinListener;
import com.unidata.mdm.backend.common.integration.exits.AfterSplitListener;
import com.unidata.mdm.backend.common.integration.exits.BeforeJoinListener;
import com.unidata.mdm.backend.common.integration.exits.BeforeSplitListener;
import com.unidata.mdm.backend.common.integration.exits.DeleteListener;
import com.unidata.mdm.backend.common.integration.exits.DeleteRelationListener;
import com.unidata.mdm.backend.common.integration.exits.MergeListener;
import com.unidata.mdm.backend.common.integration.exits.SearchListener;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.integration.exits.UpsertRelationListener;
import com.unidata.mdm.backend.common.integration.notification.SinkComponent;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessSupport;
import com.unidata.mdm.backend.common.security.SecurityInterceptionProvider;
import com.unidata.mdm.backend.conf.impl.DeleteImpl;
import com.unidata.mdm.backend.conf.impl.JoinImpl;
import com.unidata.mdm.backend.conf.impl.MergeImpl;
import com.unidata.mdm.backend.conf.impl.SearchImpl;
import com.unidata.mdm.backend.conf.impl.SplitImpl;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.conf.impl.WorkflowProcessDefinitionImpl;
import com.unidata.mdm.backend.exchange.StandaloneConfiguration;
import com.unidata.mdm.backend.service.configuration.ConfigurationHolder;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.SecurityDataSource;
import com.unidata.mdm.backend.service.security.impl.StandardSecurityDataProvider;
import com.unidata.mdm.backend.service.security.impl.StandardSecurityInterceptionProvider;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowService;
import com.unidata.mdm.conf.Configuration;
import com.unidata.mdm.conf.Exits;
import com.unidata.mdm.conf.Listener;
import com.unidata.mdm.conf.ListenerRef;
import com.unidata.mdm.conf.Notification;
import com.unidata.mdm.conf.Provider;
import com.unidata.mdm.conf.SecurityDataProviderSource;
import com.unidata.mdm.conf.SecurityDataProviders;
import com.unidata.mdm.conf.Sink;
import com.unidata.mdm.conf.SinkProperty;
import com.unidata.mdm.conf.Workflow;
import com.unidata.mdm.conf.WorkflowProcessDefinition;

/**
 * @author Mikhail Mikhailov
 *         Configuration support.
 */
@Service("configurationService")
public class ConfigurationServiceImpl implements ApplicationContextAware, ConfigurationService {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceImpl.class);
    /**
     * Delete element.
     */
    private DeleteImpl delete;
    /**
     * Merge element.
     */
    private MergeImpl merge;
    /**
     * Upsert element.
     */
    private UpsertImpl upsert;
    /**
     * Search element.
     */
    private SearchImpl search;
    /**
     * Join element
     */
    private JoinImpl join;
    /**
     * Split origin from etalon
     */
    private SplitImpl split;
    /**
     * The workflow service.
     */
    @Autowired(required = false)
    private WorkflowService workflowService;
    /**
     * Standard security data source.
     */
    @Autowired
    private StandardSecurityDataProvider standardSecurityDataProvider;
    /**
     * Processes.
     */
    private Map<String, WorkflowProcessDefinitionImpl> processes;
    /**
     * Configured security data sources.
     */
    private Map<String, SecurityDataSource> securityDataSources;

    /**
     * Search cluster name.
     */
    @Value("${" + ConfigurationConstants.SEARCH_CLUSTER_NAME_PROPERTY + "}")
    private String searchCluster;
    /**
     * Search nodes.
     */
    @Value("${" + ConfigurationConstants.SEARCH_NODES_NAME_PROPERTY + "}")
    private String searchNodes;
    /**
     * Default camel context.
     */
    @Autowired(required = false)
    @Qualifier("defaultCamelContext")
    private CamelContext camelContext;

    private List<String> activeRouteIds;
    /**
     * Active sink components.
     */
    private Map<String, SinkComponent> activeSinks;

    private final List<SecurityInterceptionProvider> securityInterceptionProviders = new ArrayList<>();

    /**
     * Unidata property source name.
     */
    public static final String UNIDATA_PROPERTY_SOURCE = "unidata";

    /**
     * Constructor.
     */
    public ConfigurationServiceImpl() {
        super();
    }

    /**
     * Loads user exits.
     */
    @Override
    public void afterContextRefresh() {
        initProperties();
        initConfiguration();
    }

    /**
    *
    */
    @PreDestroy
    private void destroyConfiguration() {
        if(camelContext  == null){
            return;
        }
        if (MapUtils.isNotEmpty(activeSinks)) {
            for (Entry<String, SinkComponent> entry : activeSinks.entrySet()) {
                camelContext.removeComponent(entry.getKey());

                entry.getValue().destroy();
            }

            LOGGER.info("Destroy all active sinks.");
        }
    }

    /**
     * Reads XML configuration. Initializes its blocks.
     */
    private void initConfiguration() {
        try {
            Configuration conf = ConfigurationHolder.getUserExitsConfiguration();
            if (conf == null) {
                LOGGER.info("No configuration defined or I/O error occured.");
                return;
            }

            initUserExits(conf);
            initSecurityProviders(conf);

            if (workflowService != null) {
                initWorkflow(conf);
            }

            if (camelContext != null) {
                initNotificationRoutes(conf);
                initSinks(conf);
            }

            initSecurityInterceptionProvider(conf);
        } catch (Exception exc) {
            LOGGER.warn("User exits configuration cannot be instantiated.", exc);
        }
    }

    /**
     * Initializes work flow bits.
     * @param conf configuration
     */
    private void initWorkflow(Configuration conf) {
        Workflow workflow = conf.getWorkflow();
        if (workflow == null) {
            LOGGER.info("No workflow section defined.");
            return;
        }

        processes = new HashMap<>();
        for (WorkflowProcessDefinition def : workflow.getProcesses()) {
            try {

                WorkflowProcessDefinitionImpl impl = (WorkflowProcessDefinitionImpl) def;

                // 1. Deploy process
                if (StringUtils.isBlank(impl.getPath())) {
                    LOGGER.warn("No BPMN description given for process id '{}'. Skipped.", impl.getId());
                    continue;
                }

                // 2. Initialize support
                if (StringUtils.isBlank(impl.getClazz())) {
                    LOGGER.warn("No support class path given for process id '{}'. Default will be used.", impl.getId());
                    continue;
                }

                Class<?> clazz = ConfigurationService.getApplicationContext().getClassLoader().loadClass(impl.getClazz());
                Object instance = clazz.newInstance();
                if (suitableFor(instance, WorkflowProcessSupport.class)) {
                    impl.setSupport((WorkflowProcessSupport) instance);
                } else {
                    LOGGER.warn("Cannot assign process support to process type '{}' of type '{}' to destination of type '{}'.",
                            impl.getId(),
                            instance == null ? "null" : instance.getClass().getName(),
                            WorkflowProcessSupport.class.getName());
                    continue;
                }

                workflowService.deployProcess(impl.getPath());
                processes.put(impl.getId(), impl);

            } catch (Exception e) {
                LOGGER.warn("Process support class '" + def.getClazz() + "' cannot be instantiated.", e);
            }
        }
    }

    /**
     * Does user exits initialization.
     * @param conf configuration
     */
    private void initUserExits(Configuration conf) {

        Exits exits = conf.getExits();
        if (exits == null) {
            LOGGER.info("User exits section is not defined.");
            return;
        }

        Map<String, Object> listeners = new HashMap<>();
        for (Listener l : exits.getListeners()) {
            try {
                Class<?> clazz
                        = ConfigurationService.getApplicationContext()
                        .getClassLoader()
                        .loadClass(l.getClazz());
                Object instance = clazz.newInstance();
                listeners.put(l.getId(), instance);
            } catch (Exception e) {
                LOGGER.warn("User exit '" + l.getClazz() + "' cannot be instantiated.", e);
            }
        }

        if (MapUtils.isNotEmpty(listeners)) {

            // Delete
            delete = (DeleteImpl) exits.getDelete();
            if(delete != null){
                checkAndAddListeners(delete.getBeforeEtalonDeactivations(), listeners,
                        delete.getBeforeEtalonDeactivationInstances(), DeleteListener.class);
                checkAndAddListeners(delete.getAfterEtalonDeactivations(), listeners,
                        delete.getAfterEtalonDeactivationInstances(), DeleteListener.class);
                checkAndAddListeners(delete.getBeforeRelationDeactivations(), listeners,
                        delete.getBeforeRelationDeactivationInstances(), DeleteRelationListener.class);
                checkAndAddListeners(delete.getAfterRelationDeactivations(), listeners,
                        delete.getAfterRelationDeactivationInstances(), DeleteRelationListener.class);
            }

            // Merge
            merge = (MergeImpl) exits.getMerge();
            if(merge != null){
                checkAndAddListeners(merge.getBeforeMerges(), listeners,
                        merge.getBeforeMergeInstances(), MergeListener.class);
                checkAndAddListeners(merge.getAfterMerges(), listeners,
                        merge.getAfterMergeInstances(), MergeListener.class);
            }

            // Upsert
            upsert = (UpsertImpl) exits.getUpsert();
            if(upsert != null){
                checkAndAddListeners(upsert.getBeforeOriginUpserts(), listeners,
                        upsert.getBeforeOriginUpsertInstances(), UpsertListener.class);
                checkAndAddListeners(upsert.getAfterOriginUpserts(), listeners,
                        upsert.getAfterOriginUpsertInstances(), UpsertListener.class);
                checkAndAddListeners(upsert.getBeforeOriginRelationUpserts(), listeners,
                        upsert.getBeforeOriginRelationUpsertInstances(), UpsertRelationListener.class);
                checkAndAddListeners(upsert.getAfterOriginRelationUpserts(), listeners,
                        upsert.getAfterOriginRelationUpsertInstances(), UpsertRelationListener.class);
                checkAndAddListeners(upsert.getAfterEtalonCompositions(), listeners,
                        upsert.getAfterEtalonCompositionInstances(), UpsertListener.class);
                checkAndAddListeners(upsert.getAfterCompletes(), listeners,
                        upsert.getAfterCompleteInstances(), UpsertListener.class);
            }

            // Search
            search = (SearchImpl) exits.getSearch();
            if(search != null){
                checkAndAddListeners(search.getBeforeSearches(), listeners,
                        search.getBeforeSearchInstances(), SearchListener.class);
                checkAndAddListeners(search.getAfterSearches(), listeners,
                        search.getAfterSearchInstances(), SearchListener.class);
            }

            // Join
            join = (JoinImpl) exits.getJoin();
            if (join != null) {
                checkAndAddListeners(
                        join.getBeforeJoins(), listeners, join.getBeforeJoinInstances(), BeforeJoinListener.class
                );
                checkAndAddListeners(
                        join.getAfterJoins(), listeners, join.getAfterJoinInstances(), AfterJoinListener.class
                );
            }

            // Split
            split = (SplitImpl) exits.getSplit();
            if (split != null) {
                checkAndAddListeners(
                        split.getBeforeSplits(), listeners, split.getBeforeSplitInstances(), BeforeSplitListener.class
                );
                checkAndAddListeners(
                        split.getAfterSplits(), listeners, split.getAfterSplitInstances(), AfterSplitListener.class
                );
            }
        }
    }

    /**
     * Initializes security providers section.
     * @param conf the configuration
     */
    private void initSecurityProviders(Configuration conf) {

        final SecurityDataProviders providers = conf.getSecurityDataProviders();
        if (providers == null) {
            LOGGER.info("Security providers section is not defined.");
            finishSystemDefaultSecurityDataSource(null);
            return;
        }

        final Map<String, Object> providerClasses = new HashMap<>();
        for (final Provider p : providers.getProviders()) {
            try {
                final Class<?> clazz = ConfigurationService.getApplicationContext().getClassLoader().loadClass(p.getClazz());
                final Object instance = clazz.newInstance();
                providerClasses.put(p.getId(), instance);
            } catch (final Exception e) {
                LOGGER.warn("User provider '" + p.getClazz() + "' cannot be instantiated.", e);
            }
        }

        final Map<String, SecurityDataSource> sources = new LinkedHashMap<>();
        final List<SecurityDataProviderSource> providerSources = providers.getSources();
        if (!CollectionUtils.isEmpty(providerSources)) {
            checkAndAddSecurityProviders(providerClasses, providerSources, sources);
        }

        finishSystemDefaultSecurityDataSource(sources);
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

    /**
     * Reload all Camel routes declared for notifications
     * @param conf
     */
    private void initNotificationRoutes(Configuration conf) {

        List<Notification> notifications = conf.getNotifications();
        List<String> addedRouteIds = null;
        if (CollectionUtils.isEmpty(notifications)) {
            LOGGER.debug("No notifications declared in config.");
        } else {

            addedRouteIds = new ArrayList<>();
            for (Notification notification : notifications) {
                LOGGER.debug("Notification configuration [id={}, uri={}, enabled={}, routeRawDefinition={}]",
                    notification.getId(),
                    notification.getUri(),
                    notification.isEnabled(),
                    notification.getRouteRawDefinition());

                try {
                    RoutesDefinition routesDefinition = camelContext.loadRoutesDefinition(
                        new ByteArrayInputStream(notification.getRouteRawDefinition().getBytes("UTF8")));

                    if (routesDefinition != null && !CollectionUtils.isEmpty(routesDefinition.getRoutes())) {
                        LOGGER.debug("Found routes to load: " + routesDefinition.toString());

                        for (RouteDefinition routeDefinition : routesDefinition.getRoutes()) {
                            Route route = camelContext.getRoute(routeDefinition.getId());

                            if (route != null) {
                                camelContext.stopRoute(route.getId());
                                camelContext.removeRoute(route.getId());

                                if (activeRouteIds != null) {
                                    activeRouteIds.remove(route.getId());
                                }
                            }

                            camelContext.addRouteDefinition(routeDefinition);
                            camelContext.startRoute(routeDefinition.getId());

                            addedRouteIds.add(routeDefinition.getId());
                        }
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Failed to load routes for notification [id=" + notification.getId() + ']', e);
                }
            }
        }

        // Remove routes declared before and missed in new configuration.
        if (!CollectionUtils.isEmpty(activeRouteIds)) {
            for (String activeRouteId : activeRouteIds) {
                Route route = camelContext.getRoute(activeRouteId);

                if (route != null) {
                    try {
                        camelContext.stopRoute(route.getId());
                        camelContext.removeRoute(route.getId());
                    }
                    catch (Exception e) {
                        LOGGER.error("Failed to remove route: " + activeRouteId, e);
                    }
                }
            }
        }

        // Now active routes point to added routes only.
        activeRouteIds = addedRouteIds;
    }

    /**
     * Reload sinks.
     * @param conf
     */
    private void initSinks(Configuration conf) {

        List<Sink> sinks = conf.getSinks();
        Map<String, SinkComponent> addedSinks = null;

        if (CollectionUtils.isEmpty(sinks)) {
            LOGGER.debug("No sinks declared in config.");
        } else {
            addedSinks = new LinkedHashMap<>();

            for (Sink sink : sinks) {
                Component component = camelContext.getComponent(sink.getId());

                if (component != null) {
                    camelContext.removeComponent(sink.getId());

                    if (activeSinks != null && activeSinks.containsKey(sink.getId())) {
                        SinkComponent sinkComponent = activeSinks.remove(sink.getId());

                        if (sinkComponent != null) {
                            sinkComponent.destroy();
                        }
                    }
                }

                LOGGER.debug("Sink configuration [id={}, uri={}, class={}]",
                    sink.getId(),
                    sink.getUri(),
                    sink.getClazz());

                final Class<?> clazz;
                try {
                    clazz = ConfigurationService.getApplicationContext().getClassLoader().loadClass(sink.getClazz());

                    if (!SinkComponent.class.isAssignableFrom(clazz) || !Component.class.isAssignableFrom(clazz)) {
                        throw new RuntimeException("Invalid sink class "
                                + "(must implement com.unidata.mdm.backend.common.integration.notification.SinkComponent "
                                + "AND org.apache.camel.Component): " +
                            clazz.getName());
                    }

                    final Object instance = clazz.newInstance();

                    // Set all properties.
                    if (!CollectionUtils.isEmpty(sink.getProperty())) {
                        for (SinkProperty property : sink.getProperty()) {
                            BeanUtils.setProperty(instance, property.getName(), property.getValue());
                        }
                    }

                    SinkComponent sinkComponent = (SinkComponent) instance;

                    sinkComponent.init();

                    camelContext.addComponent(sink.getId(), (Component) sinkComponent);

                    addedSinks.put(sink.getId(), sinkComponent);
                }
                catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                    InvocationTargetException e) {
                    LOGGER.error("Failed to initialize sink: " + sink.getId(), e);
                }
            }
        }

        // Remove sink components declared before and missed in new configuration.
        if (MapUtils.isNotEmpty(activeSinks)) {
            for (Entry<String, SinkComponent> entry : activeSinks.entrySet()) {
                camelContext.removeComponent(entry.getKey());
                entry.getValue().destroy();
            }
        }

        activeSinks = addedSinks;
    }

    private void initSecurityInterceptionProvider(Configuration conf) {
        final List<SecurityInterceptionProvider> result = conf.getSecurityInterceptionProviders().stream()
                .map(securityInterceptionProviderInfo ->
                        initSecurityInterceptionProvider(securityInterceptionProviderInfo.getClazz())
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(result)) {
            securityInterceptionProviders.add(
                    initSecurityInterceptionProvider(StandardSecurityInterceptionProvider.class.getName())
            );
        }
        else {
            securityInterceptionProviders.addAll(result);
        }
    }

    private SecurityInterceptionProvider initSecurityInterceptionProvider(final String clazz) {
        try {
            final Class<?> aClass = ConfigurationService.getApplicationContext()
                            .getClassLoader().loadClass(clazz);
            return (SecurityInterceptionProvider) ConfigurationService.getApplicationContext()
                    .getAutowireCapableBeanFactory()
                    .autowire(aClass, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, true);
        } catch (Exception e) {
            LOGGER.error("Can't create instance of SecurityInterceptionProvider: " + securityInterceptionProviders, e);
            return null;
        }
    }

    /**
     * Reads runtime properties.
     */
    private void initProperties() {

        Properties bProps = null;
        try {
            bProps = ConfigurationHolder.getBackendProperties();
            if (bProps != null) {
                ConfigurableEnvironment cEnv = (ConfigurableEnvironment) ConfigurationService.getApplicationContext().getEnvironment();
                cEnv.getPropertySources().addFirst(new PropertiesPropertySource(ConfigurationServiceImpl.UNIDATA_PROPERTY_SOURCE, bProps));
            }
        } catch (Exception exc) {
            LOGGER.warn("Unidata system property source cannot be instantiated.", exc);
        }
    }

    /**
     * Checks and adds listeners of a certain type.
     *
     * @param refs        references from configuration
     * @param instances   instances
     * @param destination destination map
     * @param type        type to check
     */
    @SuppressWarnings("unchecked")
    private <T> void checkAndAddListeners(List<ListenerRef> refs, Map<String, Object> instances, Map<String, T> destination, Class<T> type) {
        for (ListenerRef ref : refs) {
            Listener l = (Listener) ref.getListener();
            Object instance = instances.get(l.getId());
            if (suitableFor(instance, type)) {
                destination.put(ref.getEntity(), (T) instance);
            } else {
                LOGGER.warn("Cannot assign user exit handler for entity type '{}' of type '{}' to destination of type '{}'.",
                        ref.getEntity(),
                        instance == null ? "null" : instance.getClass().getName(),
                        type.getName());
            }
        }
    }

    /**
     * Checks and adds user security providers.
     *
     * @param instances     Provider instances
     * @param destProviders destination security providers map
     */
    private void checkAndAddSecurityProviders(final Map<String, Object> instances,
                                                     final List<SecurityDataProviderSource> confSecProviders,
                                                     final Map<String, SecurityDataSource> destProviders) {

        for (final SecurityDataProviderSource ref : confSecProviders) {

            AuthenticationProvider authenticationProvider = null;
            if (ref.getAuthenticationProvider() != null) {
                Object instance = instances.get(((Provider) ref.getAuthenticationProvider()).getId());
                if (suitableFor(instance, AuthenticationProvider.class)) {
                    authenticationProvider = (AuthenticationProvider) instance;
                }
            }

            AuthorizationProvider authorizationProvider = null;
            if (ref.getAuthorizationProvider() != null) {
                Object instance = instances.get(((Provider) ref.getAuthorizationProvider()).getId());
                if (suitableFor(instance, AuthorizationProvider.class)) {
                    authorizationProvider = (AuthorizationProvider) instance;
                }
            }

            ProfileProvider profileProvider = null;
            if (ref.getProfileProvider() != null) {
                Object instance = instances.get(((Provider) ref.getProfileProvider()).getId());
                if (suitableFor(instance, ProfileProvider.class)) {
                    profileProvider = (ProfileProvider) instance;
                }
            }

            if (authenticationProvider != null
             || authorizationProvider != null
             || profileProvider != null) {

                final SecurityDataSource extSecurityProvider
                    = new SecurityDataSource(ref.getName(), ref.getDescription(),
                        authenticationProvider, authorizationProvider, profileProvider);

                destProviders.put(ref.getName(), extSecurityProvider);
            } else {
                LOGGER.warn("Cannot create user security provider '{}'", ref.getName());
            }
        }
    }

    /**
     * Checks if the user exit instance is suitable for use as a certain listener type.
     *
     * @param obj               the instance
     * @param requiredInterface the interface
     * @return true, if so, false otherwise
     */
    private boolean suitableFor(Object obj, Class<?> requiredInterface) {
        return obj != null && requiredInterface.isInstance(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteImpl getDelete() {
        return delete;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public MergeImpl getMerge() {
        return merge;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertImpl getUpsert() {
        return upsert;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchImpl getSearch() {
        return search;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JoinImpl getJoin() {
        return join;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SplitImpl getSplit() {
        return split;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkflowProcessDefinition> getDefinedProcessTypes() {

        if (processes != null) {
            return new ArrayList<>(processes.values());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowProcessSupport getProcessSupportByProcessDefinitionId(String definitionId) {

        if (processes != null) {
            WorkflowProcessDefinitionImpl impl = processes.get(definitionId);
            return impl != null ? impl.getSupport() : null;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT_HOLDER.set(applicationContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Bean
    @Profile({"!" + StandaloneConfiguration.STANDALONE_PROFILE_NAME})
    public Client getSearchClient() {
        if (StringUtils.isBlank(searchCluster)) {
            throw new IllegalArgumentException("Elasticsearch has to be configured. Property [" + ConfigurationConstants.SEARCH_CLUSTER_NAME_PROPERTY + "] is undefined");
        }

        if (StringUtils.isBlank(searchNodes)) {
            throw new IllegalArgumentException("Elasticsearch has to be configured. Property [" + ConfigurationConstants.SEARCH_NODES_NAME_PROPERTY + "] is undefined");
        }

        return SearchUtils.initializeSearchClient(searchCluster, searchNodes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, SecurityDataSource> getSecurityDataSources() {
        return securityDataSources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SecurityDataSource getSystemSecurityDataSource() {
        return securityDataSources.get(SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SecurityInterceptionProvider> getSecurityInterceptionProviders() {
        return securityInterceptionProviders;
    }
}
