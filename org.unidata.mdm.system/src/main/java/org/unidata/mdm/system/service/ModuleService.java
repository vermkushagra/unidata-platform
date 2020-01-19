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

package org.unidata.mdm.system.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.unidata.mdm.system.dto.ModuleInfo;
import org.unidata.mdm.system.type.module.Module;

/**
 * @author Alexander Malyshev
 * Basic functionality around business modules.
 */
public interface ModuleService extends BeanPostProcessor, ModularPostProcessingRegistrar {

    void setCurrentContext(ApplicationContext applicationContext);

    void init();

    Collection<ModuleInfo> modulesInfo();

    Optional<Module> findModuleById(String moduleId);
    /**
     * Gets a collection of registered modules.
     * @return collection
     */
    Collection<Module> getModules();
}
