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

package org.unidata.mdm.core.service.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.service.ConfigurationService;

/**
 * @author Alexander Malyshev
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public String getSystemStringProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }

    @Override
    public String getSystemStringPropertyWithDefault(String key, String defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    @Override
    public Boolean getSystemBooleanProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key, Boolean.class);
    }

    @Override
    public Boolean getSystemBooleanPropertyWithDefault(String key, Boolean defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, Boolean.class, defaultValue);
    }

    @Override
    public Integer getSystemIntProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key, Integer.class);
    }

    @Override
    public Integer getSystemIntPropertyWithDefault(String key, Integer defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, Integer.class, defaultValue);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
