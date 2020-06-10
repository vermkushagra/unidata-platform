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
