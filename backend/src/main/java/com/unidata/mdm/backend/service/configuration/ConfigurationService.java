package com.unidata.mdm.backend.service.configuration;

import java.util.List;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessSupport;
import com.unidata.mdm.backend.common.security.SecurityInterceptionProvider;
import com.unidata.mdm.backend.conf.impl.DeleteImpl;
import com.unidata.mdm.backend.conf.impl.JoinImpl;
import com.unidata.mdm.backend.conf.impl.MergeImpl;
import com.unidata.mdm.backend.conf.impl.SearchImpl;
import com.unidata.mdm.backend.conf.impl.SplitImpl;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.service.security.SecurityDataSource;
import com.unidata.mdm.conf.WorkflowProcessDefinition;

public interface ConfigurationService extends AfterContextRefresh {
    /**
     * Context holder.
     */
    static final ApplicationContextHolder APPLICATION_CONTEXT_HOLDER = new ApplicationContextHolder();
    /**
     * Gets the delete UE configuration.
     * @return the delete
     */
    DeleteImpl getDelete();
    /**
     * Gets the merge UE configuration.
     * @return the merge
     */
    MergeImpl getMerge();
    /**
     * Gets the upsert UE configuration.
     * @return the upsert
     */
    UpsertImpl getUpsert();
    /**
     * Gets the search UE configuration.
     * @return the search
     */
    SearchImpl getSearch();
    /**
     * Gets the join UE configuration.
     * @return the join
     */
    JoinImpl getJoin();
    /**
     * Gets the delete UE configuration.
     * @return the split
     */
    SplitImpl getSplit();
    /**
     * Returns list of defined process types.
     * @return list of types
     */
    List<WorkflowProcessDefinition> getDefinedProcessTypes();
    /**
     * Gets process support for a process type.
     * @param definitionId process definition id
     * @return support or null
     */
    WorkflowProcessSupport getProcessSupportByProcessDefinitionId(String definitionId);
    /**
     * Instantiates a search client singleton.
     *
     * @return ES client instance
     */
    Client getSearchClient();
    /**
     * Gets all registered security data sources.
     * @return map
     */
    Map<String, SecurityDataSource> getSecurityDataSources();
    /**
     * Gets the system default security data source.
     * @return security data source
     */
    SecurityDataSource getSystemSecurityDataSource();
    /**
     * Gets security interceptor providers.
     * @return list
     */
    List<SecurityInterceptionProvider> getSecurityInterceptionProviders();
    /**
     * Gets a property value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    static String getSystemStringProperty(String key) {
        return APPLICATION_CONTEXT_HOLDER.get().getEnvironment().getProperty(key);
    }

    /**
     * Gets a property value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    static String getSystemStringPropertyWithDefault(String key, String defaultValue) {
        return APPLICATION_CONTEXT_HOLDER.get().getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * Gets a property value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    static Boolean getSystemBooleanProperty(String key) {
        return APPLICATION_CONTEXT_HOLDER.get().getEnvironment().getProperty(key, Boolean.class);
    }

    /**
     * Gets a property value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    static Boolean getSystemBooleanPropertyWithDefault(String key, Boolean defaultValue) {
        return APPLICATION_CONTEXT_HOLDER.get().getEnvironment().getProperty(key, Boolean.class, defaultValue);
    }

    /**
     * Gets a property Int value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    static Integer getSystemIntProperty(String key) {
        return APPLICATION_CONTEXT_HOLDER.get().getEnvironment().getProperty(key, Integer.class);
    }

    /**
     * Gets a property  Int value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    static Integer getSystemIntPropertyWithDefault(String key, Integer defaultValue) {
        return APPLICATION_CONTEXT_HOLDER.get().getEnvironment().getProperty(key, Integer.class, defaultValue);
    }
    /**
     * @return the applicationContext
     */
    static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT_HOLDER.get();
    }

    /**
     * Gets the system - wide message source.
     * @return message source or null
     */
    static MessageSource getMessageSource() {
        return APPLICATION_CONTEXT_HOLDER.get();
    }
}