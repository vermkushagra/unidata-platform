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

public class ConfigurationPropertyRO {
    private String name;
    private String displayName;
    private String group;
    private String groupCode;
    private String type;
    private Object value;
    private ConfigurationPropertyMetaPO meta;

    public ConfigurationPropertyRO() {
    }

    public ConfigurationPropertyRO(
            final String name,
            final String displayName,
            final String groupCode,
            final String group,
            final String type,
            final Object value,
            final ConfigurationPropertyMetaPO meta
    ) {
        this.name = name;
        this.displayName = displayName;
        this.groupCode = groupCode;
        this.group = group;
        this.type = type;
        this.value = value;
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ConfigurationPropertyMetaPO getMeta() {
        return meta;
    }

    public void setMeta(ConfigurationPropertyMetaPO meta) {
        this.meta = meta;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
