package org.unidata.mdm.system.service.impl.module;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.unidata.mdm.system.configuration.SystemConfiguration;
import org.unidata.mdm.system.type.module.Module;

/**
 * Parent context +.
 * @author Mikhail Mikhailov on Oct 25, 2019
 */
public class ModularContextBuilder {
    /**
     * Include parent post processor classes.
     */
    private static final Class<?> INCLUDE_POST_PROCESSORS[] = {
        PropertyPlaceholderConfigurer.class,
        PropertySourcesPlaceholderConfigurer.class,
        CustomEditorConfigurer.class
    };
    /**
     * Exclude parent post processor classes.
     */
    private static final Class<?> EXCLUDE_POST_PROCESSORS[] = {
        BeanFactoryAware.class
    };
    /**
     * The parent context to use.
     */
    private AbstractApplicationContext parent;
    /**
     * Custom class loader, if needed.
     */
    private ClassLoader customClassLoader;
    /**
     * The module, being currentlu initialized.
     */
    private Module module;
    /**
     * Non-default post processors to run on children contexts.
     */
    private Collection<BeanPostProcessor> postProcessors;
    /**
     * Builder method.
     * @param parent the parent (system) context
     * @return builder
     */
    public static ModularContextBuilder builder(AbstractApplicationContext parent) {
        Objects.requireNonNull(parent, "Parent context must not be null!");
        return new ModularContextBuilder(parent);
    }
    /**
     * Constructor.
     */
    private ModularContextBuilder(AbstractApplicationContext parent) {
        super();
        this.parent = parent;
    }
    /**
     * Sets a custom classloader.
     * @param customClassLoader the class loader to set
     * @return self
     */
    public ModularContextBuilder customClassLoader(ClassLoader customClassLoader) {
        this.customClassLoader = customClassLoader;
        return this;
    }
    /**
     * Sets this module state.
     * @param module the module being initialized
     * @return self
     */
    public ModularContextBuilder module(Module module) {
        this.module = module;
        return this;
    }
    /**
     * Sets non-default post processors to run on children contexts.
     * @param postProcessors the post processors
     * @return self
     */
    public ModularContextBuilder postProcessors(Collection<BeanPostProcessor> postProcessors) {
        this.postProcessors = postProcessors;
        return this;
    }
    /**
     * Builds a child context.
     * @return a new context
     */
    public AnnotationConfigApplicationContext build() {

        // 0. Validate state
        Objects.requireNonNull(this.module, "Module must not be null");

        ModularAnnotationConfigApplicationContext result
            = new ModularAnnotationConfigApplicationContext(customClassLoader, new ModularListableBeanFactory(this));

        // 2. Set parent and subcontext ID
        result.setParent(parent);
        result.setId(module.getId());

        // 3. Scan this module package and its subpackages
        result.scan(module.getId());

        // 4. This module doesn't use spring
        if (result.getBeanDefinitionCount() == 0) {
            return null;
        }

        // 5. Refresh
        result.refresh();

        // 6. Add resource basenames to parent MS, if those are defined
        if (ArrayUtils.isNotEmpty(module.getResourceBundleBasenames())) {
            SystemConfiguration configuration = parent.getBean(SystemConfiguration.class);
            ResourceBundleMessageSource rbms = configuration.getSystemMessageSource();
            String[] basenames = Arrays.stream(module.getResourceBundleBasenames())
                .map(v -> "classpath:" + v)
                .toArray(sz -> new String[sz]);
            rbms.addBasenames(basenames);
        }

        // 7. And return
        return result;
    }
    /**
     * The bean factory.
     * @author Mikhail Mikhailov on Oct 25, 2019
     */
    private class ModularListableBeanFactory extends DefaultListableBeanFactory {
        /**
         * PPs from parent and siblings.
         */
        private transient Collection<BeanPostProcessor> postProcessors;
        /**
         * Constructor.
         * @param parentBeanFactory
         */
        public ModularListableBeanFactory(ModularContextBuilder builder) {
            super();
            postProcessors = builder.postProcessors;
        }

        public void modularPostProcess() {
            getBeanPostProcessors().addAll(postProcessors);
        }
    }
    /**
     * Our own context.
     * @author Mikhail Mikhailov on Oct 25, 2019
     */
    private class ModularAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext {
        /**
         * Constructor.
         * @param beanFactory
         */
        public ModularAnnotationConfigApplicationContext(ClassLoader customClassLoader, DefaultListableBeanFactory beanFactory) {
            super(beanFactory);
            if (Objects.nonNull(customClassLoader)) {
                setClassLoader(customClassLoader);
            }
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
            super.postProcessBeanFactory(beanFactory);
            ModularListableBeanFactory modularFactory = (ModularListableBeanFactory) beanFactory;
            modularFactory.modularPostProcess();
        }
    }
}
