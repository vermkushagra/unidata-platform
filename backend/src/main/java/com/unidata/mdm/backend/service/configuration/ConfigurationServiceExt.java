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

package com.unidata.mdm.backend.service.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.elasticsearch.client.Client;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import com.unidata.mdm.backend.common.configuration.application.RuntimePropertiesService;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessSupport;
import com.unidata.mdm.backend.common.security.SecurityInterceptionProvider;
import com.unidata.mdm.backend.common.service.ConfigurationService;
import com.unidata.mdm.backend.conf.impl.DeleteImpl;
import com.unidata.mdm.backend.conf.impl.JoinImpl;
import com.unidata.mdm.backend.conf.impl.MergeImpl;
import com.unidata.mdm.backend.conf.impl.ModelImpl;
import com.unidata.mdm.backend.conf.impl.SearchImpl;
import com.unidata.mdm.backend.conf.impl.SplitImpl;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.service.security.SecurityDataSource;
import com.unidata.mdm.conf.WorkflowProcessDefinition;

public interface ConfigurationServiceExt extends ConfigurationService, AfterContextRefresh {
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
     * Gets the model UE configuration.
     * @return the model
     */
    ModelImpl getModel();
    /**
     * Get runtime configuration service
     * @return
     */
    RuntimePropertiesService getRuntimePropertiesService();

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
     * Use deprecate user exits or not
     * @return true, or false.
     */
    boolean useDeprecateUserExits();
    /**
     * Get listeners for use
     * @param entityName entity name
     * @param map map for check
     * @return listeners to use
     */
    <T> Collection<T> getListeners(String entityName, MultiValuedMap<String, T> map);
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