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

package org.unidata.mdm.system.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

/**
 * Root configuration beans class.
 * @author Mikhail Mikhailov on Nov 5, 2019
 */
public abstract class AbstractConfiguration implements ApplicationContextAware {
    /**
     * Configured contexts.
     */
    protected static final Map<ConfigurationId, ApplicationContext>
        CONFIGURED_CONTEXT_MAP = new IdentityHashMap<>();
    /**
     * Constructor.
     */
    public AbstractConfiguration() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        CONFIGURED_CONTEXT_MAP.put(getId(), applicationContext);
    }
    /**
     * Gets the underlying context
     * @return context or null
     */
    public ApplicationContext getConfiguredApplicationContext() {
        return CONFIGURED_CONTEXT_MAP.get(getId());
    }
    /**
     * Gets a named bean/component.
     *
     * @param <T> the bean type
     * @param name the bean name
     * @return bean or null
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getBeanByName(String name) {
        ApplicationContext context = CONFIGURED_CONTEXT_MAP.get(getId());
        if (Objects.nonNull(context)) {
            return (T) context.getBean(name);
        }

        return null;
    }

    /**
     * Gets named beans/components.
     *
     * @param <T> the bean type
     * @param names the bean names
     * @return bean or empty collection
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> Collection<T> getBeansByNames(String... names) {

        ApplicationContext context = CONFIGURED_CONTEXT_MAP.get(getId());
        if (Objects.nonNull(context) && ArrayUtils.isNotEmpty(names)) {

            List<T> result = new ArrayList<>(names.length);
            for (int i = 0; i < names.length; i++) {

                if (Objects.isNull(names[i])) {
                    continue;
                }

                T val = (T) context.getBean(names[i]);
                result.add(val);
            }

            return result;
        }

        return Collections.emptyList();
    }
    /**
     * Gets a bean.
     *
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
    @Nullable
    public <T> T getBeanByClass(Class<T> beanClass) {
        ApplicationContext context = CONFIGURED_CONTEXT_MAP.get(getId());
        if (Objects.nonNull(context)) {
            return context.getBean(beanClass);
        }

        return null;
    }
    /**
     * Gets beans of type.
     *
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
    public <T> Map<String, T> getBeansOfType(Class<T> beanClass) {
        ApplicationContext context = CONFIGURED_CONTEXT_MAP.get(getId());
        if (Objects.nonNull(context)) {
            return context.getBeansOfType(beanClass);
        }

        return Collections.emptyMap();
    }
    /**
     * Gets all properties, string with specified prefix.
     * @param prefix the prefix to filter properties by
     * @param strip the flag, telling to strip prefix in the result, if true or letting the name unchanged, if false
     * @return properties
     */
    public Properties getAllPropertiesWithPrefix(String prefix, boolean strip) {

        final Properties result = new Properties();
        ConfigurableEnvironment ce = (ConfigurableEnvironment) getConfiguredApplicationContext().getEnvironment();

        ce.getPropertySources().forEach(ps -> {
            if (ps instanceof CompositePropertySource) {
    			CompositePropertySource cps = (CompositePropertySource) ps;
    			processPropertySources(prefix, strip, cps.getPropertySources(), result, ce);
    		} else if (ps instanceof EnumerablePropertySource) {
    			processEnumerablePropertySource(prefix, strip, (EnumerablePropertySource<?>) ps, result, ce);
    		}
    	});

    	return result;
    }

    private void processPropertySources(String prefix, boolean strip, Collection<PropertySource<?>> sources, Properties result, ConfigurableEnvironment ce) {
    	sources.forEach(ps -> {
    		if (ps instanceof CompositePropertySource) {
    			CompositePropertySource cps = (CompositePropertySource) ps;
    			processPropertySources(prefix, strip, cps.getPropertySources(), result, ce);
    		} else if (ps instanceof EnumerablePropertySource) {
    			processEnumerablePropertySource(prefix, strip, (EnumerablePropertySource<?>) ps, result, ce);
    		}
    	});
    }

    private void processEnumerablePropertySource(String prefix, boolean strip, EnumerablePropertySource<?> eps, Properties result, ConfigurableEnvironment ce) {

    	for (String propName : eps.getPropertyNames()) {

    		if (!propName.startsWith(prefix)) {
    			continue;
    		}

    		// Resolve it once again via environment properly,
    		// so that place holders are also processed
    		result.setProperty(strip ? StringUtils.substringAfter(propName, prefix) : propName, ce.getProperty(propName));
    	}
    }

    /**
     * Gets the id.
     * @return id
     */
    protected abstract ConfigurationId getId();
    /**
     * Just a pointer for configured context map.
     * @author Mikhail Mikhailov on Nov 9, 2019
     */
    @FunctionalInterface
    protected interface ConfigurationId {
        /**
         * @return the name
         */
        String getName();
    }
}
