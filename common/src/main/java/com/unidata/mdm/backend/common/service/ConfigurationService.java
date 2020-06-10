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

/**
 * @author Mikhail Mikhailov
 * Configuration service basic interface.
 */
public interface ConfigurationService {
    /**
     * Gets a string property value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    String getSystemStringProperty(String key);
    /**
     * Gets a string property value from the environment falling back to default, if not found.
     *
     * @param key the key
     * @param defaultValue fall back
     * @return value or null
     */
    String getSystemStringPropertyWithDefault(String key, String defaultValue);
    /**
     * Gets a boolean property value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    Boolean getSystemBooleanProperty(String key);
    /**
     * Gets a boolean property value from the environment falling back to default, if not found.
     *
     * @param key the key
     * @param defaultValue fall back value
     * @return value or null
     */
    Boolean getSystemBooleanPropertyWithDefault(String key, Boolean defaultValue);
    /**
     * Gets an int property from the environment falling back to default, if not found.
     *
     * @param key the key
     * @return value or null
     */
    Integer getSystemIntProperty(String key);
    /**
     * Gets an int property  Int value from the environment falling back to default, if not found.
     *
     * @param key the key
     * @param defaultValue fall back value
     * @return value or null
     */
    Integer getSystemIntPropertyWithDefault(String key, Integer defaultValue);
}
