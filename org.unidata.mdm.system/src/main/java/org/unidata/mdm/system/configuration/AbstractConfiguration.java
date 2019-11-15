package org.unidata.mdm.system.configuration;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
    public <T> T getBeanByName(String name) {
        ApplicationContext context = CONFIGURED_CONTEXT_MAP.get(getId());
        if (Objects.nonNull(context)) {
            return (T) context.getBean(name);
        }

        return null;
    }
    /**
     * Gets a bean.
     *
     * @param <T>
     * @param beanClass the bean class
     * @return bean
     */
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
