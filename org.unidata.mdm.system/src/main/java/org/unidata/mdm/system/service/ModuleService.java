package org.unidata.mdm.system.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.unidata.mdm.system.dto.ModuleInfo;
import org.unidata.mdm.system.type.module.Module;

/**
 * @author Alexander Malyshev
 * Basic functionality around business modules.
 */
public interface ModuleService extends BeanPostProcessor, ModularPostProcessingRegistrar {

    void init();

    Collection<ModuleInfo> modulesInfo();

    Optional<Module> findModuleById(String moduleId);
    /**
     * Gets a collection of registered modules.
     * @return collection
     */
    Collection<Module> getModules();
}
