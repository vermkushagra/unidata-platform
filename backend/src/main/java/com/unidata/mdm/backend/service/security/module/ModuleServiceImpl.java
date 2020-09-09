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

package com.unidata.mdm.backend.service.security.module;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.common.module.Module;
import com.unidata.mdm.backend.common.module.ModuleConfiguration;
import com.unidata.mdm.backend.common.module.ModuleFeature;
import com.unidata.mdm.backend.service.security.LicenseServiceExt;
import com.unidata.mdm.backend.service.security.ModuleServiceExt;

/**
 * @author Mikhail Mikhailov
 * Simple implementation of the module service.
 */
@Service
public class ModuleServiceImpl implements ModuleServiceExt, ApplicationContextAware {
    /**
     * Everything is rather simple here.
     */
    private final Map<Module, ModuleConfiguration> modules = new EnumMap<>(Module.class);
    /**
     * License service instance.
     */
    @Autowired
    private LicenseServiceExt licenseService;
    /**
     * App context.
     */
    private ApplicationContext applicationContext;
    /**
     * Constructor.
     */
    public ModuleServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterContextRefresh() {

        Map<String, ModuleConfiguration> beans = applicationContext.getBeansOfType(ModuleConfiguration.class);
        if (MapUtils.isNotEmpty(beans)) {
            for (Entry<String, ModuleConfiguration> entry : beans.entrySet()) {
                ModuleConfiguration conf = entry.getValue();
                modules.put(conf.getModuleId(), conf);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ModuleConfiguration> getActiveConfigurations() {
        return modules.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public ModuleConfiguration getConfiguration(Module module) {
        return modules.get(module);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive(Module module) {
        return getConfiguration(module) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ModuleFeature> getAllFeatures(Module module) {

        ModuleConfiguration configuration = modules.get(module);
        if (Objects.nonNull(configuration)) {
            return configuration.getFeatures();
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ModuleFeature> getActiveFeatures(Module module) {

        ModuleConfiguration configuration = modules.get(module);
        if (Objects.nonNull(configuration)) {
            return configuration.getFeatures(licenseService.getEditionType(), licenseService.getOperationMode());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupported(ModuleFeature feature) {

        ModuleConfiguration configuration = modules.get(feature.getModuleId());
        if (Objects.nonNull(configuration)) {
            return feature.isEditionSupported(licenseService.getEditionType())
                && feature.isOperationModeSupported(licenseService.getOperationMode());
        }

        return false;
    }

}
