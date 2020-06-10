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

package com.unidata.mdm.backend.common.module;

/**
 * @author Mikhail Mikhailov
 * Description of a module feature.
 */
public class FeatureDescription {
    /**
     * Name of the feature.
     */
    private final String name;
    /**
     * Display name of the feature.
     */
    private final String displayName;
    /**
     * Its description.
     */
    private final String description;
    /**
     * Constructor.
     * @param name the name
     * @param displayName the display name
     * @param description the description
     */
    public FeatureDescription(String name, String displayName, String description) {
        super();
        this.name = name;
        this.displayName = displayName;
        this.description = description;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
