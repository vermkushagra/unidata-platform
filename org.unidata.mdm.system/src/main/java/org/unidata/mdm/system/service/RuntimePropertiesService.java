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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.unidata.mdm.system.type.configuration.ApplicationConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationProperty;
import org.unidata.mdm.system.type.configuration.ConfigurationUpdatesConsumer;

public interface RuntimePropertiesService {

    void addConfigurationProperties(Collection<ApplicationConfigurationProperty> configurationProperties);

    void subscribeToConfigurationUpdates(ConfigurationUpdatesConsumer configurationUpdatesConsumer);

    <T extends Serializable> Collection<ConfigurationProperty<T>> availableProperties();

    <T extends Serializable> Collection<ConfigurationProperty<T>> getPropertiesByGroup(String groupName);

    <T extends Serializable> Optional<ConfigurationProperty<T>> property(String name);

    Collection<String> updatePropertiesValues(Map<String, String> properties);
}
