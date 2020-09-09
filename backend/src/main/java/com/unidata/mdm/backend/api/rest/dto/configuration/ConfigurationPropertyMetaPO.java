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

package com.unidata.mdm.backend.api.rest.dto.configuration;

import java.util.Collection;

public class ConfigurationPropertyMetaPO {
    private Collection<ConfigurationPropertyAvailableValuePO> availableValues;
    private boolean required;
    private boolean readonly;

    public ConfigurationPropertyMetaPO() {
    }

    public ConfigurationPropertyMetaPO(
            final Collection<ConfigurationPropertyAvailableValuePO> availableValues,
            final boolean required,
            final boolean readonly
    ) {
        this.availableValues = availableValues;
        this.required = required;
        this.readonly = readonly;
    }

    public Collection<ConfigurationPropertyAvailableValuePO> getAvailableValues() {
        return availableValues;
    }

    public void setAvailableValues(Collection<ConfigurationPropertyAvailableValuePO> availableValues) {
        this.availableValues = availableValues;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}
