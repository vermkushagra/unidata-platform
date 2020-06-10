package com.unidata.mdm.backend.common.service;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.module.Module;
import com.unidata.mdm.backend.common.module.ModuleConfiguration;
import com.unidata.mdm.backend.common.module.ModuleFeature;

/**
 * @author Mikhail Mikhailov
 * Basic functionality around business modules.
 */
public interface ModuleService {
    /**
     * Gets active configurations.
     * @return all active configurations
     */
    Collection<ModuleConfiguration> getActiveConfigurations();
    /**
     * Gets configuration of a module or null, if the module is inactive.
     * @param module the module constant
     * @return configuration or null
     */
    ModuleConfiguration getConfiguration(Module module);
    /**
     * Gets all features of a module.
     * @param module the module id
     * @return list of features
     */
    List<ModuleFeature> getAllFeatures(Module module);
    /**
     * Gets features of a module, which are active in current configuration.
     * @param module the module id
     * @return list of active features
     */
    List<ModuleFeature> getActiveFeatures(Module module);
    /**
     * Checks, if the module is active.
     * @param module the module constant
     * @return true if active, false otherwise
     */
    boolean isActive(Module module);
    /**
     * Checks, whether a particular feature is supported by its module in current configuration.
     * @param feature the feature
     * @return true, if supported, false otherwise
     */
    boolean isSupported(@Nonnull ModuleFeature feature);
}
