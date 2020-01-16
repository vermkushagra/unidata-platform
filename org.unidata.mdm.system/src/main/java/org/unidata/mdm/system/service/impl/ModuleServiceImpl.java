package org.unidata.mdm.system.service.impl;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.dao.ModuleDao;
import org.unidata.mdm.system.dto.ModuleInfo;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.module.SystemModule;
import org.unidata.mdm.system.module.annotation.ModuleRef;
import org.unidata.mdm.system.service.ModuleService;
import org.unidata.mdm.system.service.RuntimePropertiesService;
import org.unidata.mdm.system.service.impl.module.ModularContextBuilder;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesConsumer;
import org.unidata.mdm.system.type.module.Dependency;
import org.unidata.mdm.system.type.module.Module;
import org.unidata.mdm.system.type.support.IdentityHashSet;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * @author Alexander Malyshev
 * Simple implementation of the module service.
 */
@Service
public class ModuleServiceImpl implements ModuleService {

    private static final Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

    private static final String MODULE_CLASS_ATTRIBUTE = "Unidata-Module-Class";

    /**
     * Needed for inclusion of parent post processor classes.
     */
    private static final Class<?>[] INITIAL_BEAN_FACTORY_POST_PROCESSORS = {
        PropertySourcesPlaceholderConfigurer.class,
        CustomEditorConfigurer.class
    };

    private final AtomicBoolean init = new AtomicBoolean(false);

    private final RuntimePropertiesService runtimePropertiesService;

    private final ModuleDao moduleDao;

    private final List<ModuleInfo> modulesInfo = new ArrayList<>();

    private final Map<String, Module> startedModules = new HashMap<>();

    private final Map<String, Module> readyModules = new HashMap<>();

    private final Set<String> readyFailedModules = new HashSet<>();

    private final Map<String, AbstractApplicationContext> modulesContexts = new HashMap<>();
    // Only singletones are actually accepted
    private final Set<BeanPostProcessor> beanPostProcessors = new IdentityHashSet<>();
    // Only singletones are actually accepted
    private final Set<BeanFactoryPostProcessor> beanFactoryPostProcessors = new IdentityHashSet<>();

    private ApplicationContext currentContext;

    public ModuleServiceImpl(
            final RuntimePropertiesService runtimePropertiesService,
            final ModuleDao moduleDao) {
        this.runtimePropertiesService = runtimePropertiesService;
        this.moduleDao = moduleDao;
    }

    @Override
    public void init() {
        Objects.requireNonNull(currentContext, "Current application context must be set");
        final Map<String, ModuleInfo> installedModules = moduleDao.fetchModulesInfo().stream()
                        .collect(Collectors.toMap(mi -> mi.getModule().getId(), Function.identity()));

        // 1. Prepare post-processors
        for (Class<?> ppc : INITIAL_BEAN_FACTORY_POST_PROCESSORS) {
            try {
                BeanFactoryPostProcessor bfpp = (BeanFactoryPostProcessor) currentContext.getBean(ppc);
                beanFactoryPostProcessors.add(bfpp);
            } catch (BeansException e) {
                // Nothing
            }
        }

        beanPostProcessors.add(this);

        // 2. Load stuff
        logger.info("Starting loading modules...");
        try {
            final Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
            final Set<String> modulesClasses = new HashSet<>();
            while (resources.hasMoreElements()) {
                final Manifest manifest = new Manifest(resources.nextElement().openStream());
                final Attributes attributes = manifest.getMainAttributes();
                final String moduleClass = attributes.getValue(MODULE_CLASS_ATTRIBUTE);
                if (moduleClass != null) {
                    modulesClasses.add(moduleClass);
                }
            }
            final Collection<ModuleInfo> loadedModulesInfo = loadModules(modulesClasses, installedModules);
            moduleDao.saveModulesInfo(loadedModulesInfo);
            this.modulesInfo.addAll(loadedModulesInfo);
            initConfigurationPropertiesSystem();
            callReady();
            logger.info("Modules were loaded");
        } catch (IOException e) {
            logger.error("Error while loading modules", e);
            throw new PlatformFailureException("Can't load modules", e, null);
        }
    }

    private void callReady() {
        startedModules.values().forEach(this::callReady);
    }

    private void callReady(Module module) {
        final String moduleId = module.getId();
        if (readyModules.containsKey(moduleId) || readyFailedModules.contains(moduleId)) {
            return;
        }
        if (module.getDependencies() != null) {
            module.getDependencies()
                    .forEach(d -> callReady(startedModules.get(d.getModuleId())));
            if (module.getDependencies().stream().anyMatch(d -> readyFailedModules.contains(d.getModuleId()))) {
                readyFailedModules.add(moduleId);
                return;
            }
        }
        try {
            module.ready();
            readyModules.put(moduleId, module);
        }
        catch (Exception e) {
            logger.error("Error happened while call ready() on {}", moduleId, e);
            readyFailedModules.add(moduleId);
        }
    }

    private void initConfigurationPropertiesSystem() {
        runtimePropertiesService.addConfigurationProperties(
                startedModules.values().stream()
                        .map(Module::configurationProperties)
                        .flatMap(Arrays::stream)
                        .collect(Collectors.toList())
        );
        startedModules.keySet().forEach(id -> {
            if (modulesContexts.containsKey(id)) {
                subscribeToConfigurationUpdates(modulesContexts.get(id));
            }
        });
        subscribeToConfigurationUpdates(currentContext);
        modulesContexts.clear();
    }

    private void subscribeToConfigurationUpdates(ApplicationContext applicationContext) {
        applicationContext.getBeansOfType(ConfigurationUpdatesConsumer.class).values()
                .forEach(runtimePropertiesService::subscribeToConfigurationUpdates);
    }

    private Collection<ModuleInfo> loadModules(final Set<String> modulesClasses, final Map<String, ModuleInfo> installedModules) {
        final Map<String, ModuleInfo> modules = loadModulesInfo(modulesClasses);

        modules.values().forEach(moduleInfo ->
            loadModule(moduleInfo, modules, installedModules)
        );
        return modules.values();
    }

    private Map<String, ModuleInfo> loadModulesInfo(Set<String> modulesClasses) {
        return modulesClasses.stream()
                .filter(moduleClass -> moduleClass != null && !moduleClass.isEmpty())
                .map(moduleClass -> {
                    final Module module = loadModuleObject(moduleClass);
                    if (module == null) {
                        return null;
                    }
                    return new ModuleInfo(module);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(moduleInfo -> moduleInfo.getModule().getId(), Function.identity()));
    }

    private void loadModule(
            final ModuleInfo moduleInfo,
            final Map<String, ModuleInfo> modules,
            final Map<String, ModuleInfo> installedModules
    ) {
        final String moduleId = moduleInfo.getModule().getId();
        logger.info("Starting loading module {}", moduleId);
        if (moduleInfo.getModuleStatus() == ModuleInfo.ModuleStatus.LOADED
                || moduleInfo.getModuleStatus() == ModuleInfo.ModuleStatus.FAILED
                || moduleInfo.getModuleStatus() == ModuleInfo.ModuleStatus.INSTALLATION_FAILED
                || moduleInfo.getModuleStatus() == ModuleInfo.ModuleStatus.START_FAILED) {
            logger.info("Module {} was loaded early", moduleId);
            return;
        }
        moduleInfo.setModuleStatus(ModuleInfo.ModuleStatus.LOADING);
        if (!loadModuleDependencies(moduleInfo, modules, installedModules)) {
            return;
        }
        final Module module = moduleInfo.getModule();

        // Load context, if needed.
        if (SystemModule.MODULE_ID.equals(moduleId)) {

            // Save context
            autowireModuleInstance(module, (AbstractApplicationContext) currentContext);
            modulesContexts.put(module.getId(), (AbstractApplicationContext) currentContext);
        } else {
            final AnnotationConfigApplicationContext moduleContext = initModuleSpringConfigs(module);
            if (moduleContext != null) {

                // Save context
                modulesContexts.put(module.getId(), moduleContext);
            }
        }

        if (wasInstalled(moduleId, installedModules, moduleInfo)
                && moduleInfo.getModuleStatus() != ModuleInfo.ModuleStatus.FAILED) {
            try {
                logger.info("Installing module {}", moduleId);
                module.install();
                logger.info("Module {} was installing", moduleId);
            } catch (Exception e) {
                moduleInfo.setModuleStatus(ModuleInfo.ModuleStatus.INSTALLATION_FAILED);
                moduleInfo.setError("Module " + moduleId + " installation failed");
                logger.error("Installation of module {} failed", moduleId, e);
                return;
            }
        }
        try {
            logger.info("Starting module {}", moduleId);
            module.start();
            logger.info("Module {} was started", moduleId);
        }
        catch (Exception e) {
            moduleInfo.setModuleStatus(ModuleInfo.ModuleStatus.START_FAILED);
            moduleInfo.setError("Module starting failed");
            logger.error("Starting of module {} failed", moduleId, e);
            return;
        }
        moduleInfo.setModuleStatus(ModuleInfo.ModuleStatus.LOADED);
        startedModules.put(moduleId, module);
        logger.info("Module {} was loaded", moduleId);
    }

    private boolean wasInstalled(String moduleId, Map<String, ModuleInfo> installedModules, ModuleInfo moduleInfo) {
        final ModuleInfo mi = installedModules.get(moduleId);
        return !installedModules.containsKey(moduleId)
                || !Objects.equals(mi.getModule().getVersion(), moduleInfo.getModule().getVersion())
                || mi.getModuleStatus() == ModuleInfo.ModuleStatus.FAILED
                || mi.getModuleStatus() == ModuleInfo.ModuleStatus.INSTALLATION_FAILED;
    }

    private boolean loadModuleDependencies(
            final ModuleInfo moduleInfo,
            final Map<String, ModuleInfo> modules,
            final Map<String, ModuleInfo> installedModules
    ) {
        final Collection<Dependency> dependencies = moduleInfo.getModule().getDependencies();
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }
        final String moduleId = moduleInfo.getModule().getId();
        for (final Dependency dependency : dependencies) {
            final ModuleInfo dependencyModuleInfo = modules.get(dependency.getModuleId());
            if (dependencyModuleInfo == null) {
                moduleInfo.setModuleStatus(ModuleInfo.ModuleStatus.FAILED);
                moduleInfo.setError("Unknown dependency " + dependency);
                logger.error(
                        "Module {} has unknown dependency {}:{}~{}",
                        moduleId,
                        dependency.getModuleId(),
                        dependency.getVersion(),
                        dependency.getTag()
                );
                return false;
            }
            if (!Objects.equals(dependencyModuleInfo.getModule().getVersion(), dependency.getVersion())) {
                moduleInfo.setModuleStatus(ModuleInfo.ModuleStatus.FAILED);
                moduleInfo.setError("Unknown dependency version " + dependency);
                logger.error("Module {} has unknown dependency version {}", moduleId, dependency);
                return false;
            }
            if (dependencyModuleInfo.getModuleStatus() == ModuleInfo.ModuleStatus.LOADING) {
                moduleInfo.setModuleStatus(ModuleInfo.ModuleStatus.FAILED);
                moduleInfo.setError("Cyclic loading module " + moduleId);
                logger.error("Module {} has cyclic dependencies", moduleId);
                return false;
            }
            if (dependencyModuleInfo.getModuleStatus() == ModuleInfo.ModuleStatus.NOT_LOADED) {
                loadModule(dependencyModuleInfo, modules, installedModules);
            }
            if (dependencyModuleInfo.getModuleStatus() == ModuleInfo.ModuleStatus.FAILED
                    || dependencyModuleInfo.getModuleStatus() == ModuleInfo.ModuleStatus.START_FAILED) {
                moduleInfo.setModuleStatus(ModuleInfo.ModuleStatus.FAILED);
                moduleInfo.setError("Dependency has failed status " + dependency);
                logger.error("Module {} has failed dependency {}", moduleId, dependency.getModuleId());
                return false;
            }
        }
        return true;
    }

    private Module loadModuleObject(final String moduleClassName) {
        try {
            final Class<?> moduleClass = getClass().getClassLoader().loadClass(moduleClassName);
            final Constructor<?> constructor = moduleClass.getConstructor();
            return (Module) constructor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.error("Error while creating module {} object", moduleClassName, e);
            return null;
        }
    }

    private AnnotationConfigApplicationContext initModuleSpringConfigs(Module module) {

        // Create context
        AnnotationConfigApplicationContext result = ModularContextBuilder.builder((AbstractApplicationContext) currentContext)
            .customClassLoader(null) // Just for a possible future use
            .module(module)
            .beanPostProcessors(beanPostProcessors)
            .beanFactoryPostProcessors(beanFactoryPostProcessors)
            .build();

        autowireModuleInstance(module, result);
        return result;
    }

    private void autowireModuleInstance(Module module, AbstractApplicationContext context) {
        // Possibly post process module.
        if (Objects.nonNull(context)) {
            // Inject stuff, but don't call any callbacks such as @PostConstruct etc.
            context.getAutowireCapableBeanFactory().autowireBean(module);
        }
    }

    @Override
    public Collection<ModuleInfo> modulesInfo() {
        return modulesInfo;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Module> getModules() {
        return startedModules.values();
    }

    @Override
    public Optional<Module> findModuleById(String moduleId) {
        return Optional.ofNullable(startedModules.get(moduleId));
    }

    @PreDestroy
    public void stop() {
        startedModules.values().forEach(Module::stop);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        try {

            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(bean.getClass(), ModuleRef.class);
            for (Field f : fields) {

                boolean accessible = f.isAccessible();
                f.setAccessible(true);

                Class<?> fieldType = f.getType();
                Module hit = null;
                // 1. We need the id
                if (fieldType == Module.class) {
                    String requestedId = f.getAnnotation(ModuleRef.class).name();
                    hit = startedModules.get(requestedId);
                // 2. Exact type was given. Select one.
                } else {
                    for (Module i : startedModules.values()) {
                        if (i.getClass() == fieldType) {
                            hit = i;
                            break;
                        }
                    }
                }

                f.set(bean, hit);
                f.setAccessible(accessible);
            }
        } catch (IllegalAccessException e) {
            logger.warn("IAE caught while post-processing.", e);
        }

        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        beanFactoryPostProcessors.add(beanFactoryPostProcessor);
    }

    @Override
    public void setCurrentContext(ApplicationContext applicationContext) {
        this.currentContext = applicationContext;
    }
}
