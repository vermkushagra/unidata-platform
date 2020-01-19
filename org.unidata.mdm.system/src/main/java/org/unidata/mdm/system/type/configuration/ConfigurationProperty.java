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

package org.unidata.mdm.system.type.configuration;

import java.io.Serializable;

public class ConfigurationProperty<T extends Serializable> {

    private final ApplicationConfigurationProperty property;

    private final T value;

    public ConfigurationProperty(
            final ApplicationConfigurationProperty property,
            final T value
    ) {
        this.property = property;
        this.value = value;
    }

    public ApplicationConfigurationProperty getProperty() {
        return property;
    }

    public T getValue() {
        return value;
    }
}
