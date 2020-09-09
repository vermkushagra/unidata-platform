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

package com.unidata.mdm.backend.service.model;

import javax.annotation.Nonnull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Object responsible for collecting all necessary information about things which should be available for searching.
 */
public class ModelSearchObject {
    /**
     * Name of model element
     */
    private final String entityName;
    /**
     * Display name of model element
     */
    private final String displayName;
    /**
     * Collection things which should be available for searching
     */
    private final Multimap<String, String> searchElements = HashMultimap.create();

    public ModelSearchObject(String entityName, String displayName) {
        this.entityName = entityName;
        this.displayName = displayName;
        addSearchElement(SearchElementType.entityName, entityName);
        addSearchElement(SearchElementType.entityDisplayName, displayName);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Multimap<String, String> getSearchElements() {
        return searchElements;
    }

    public boolean addSearchElement(@Nonnull SearchElementType searchElementType, @Nonnull String value) {
        return searchElements.put(searchElementType.name(), value);
    }

    public enum SearchElementType {
        type,
        attributeName,
        attributeDisplayName,
        entityName,
        entityDisplayName,
        entityDescription,
        dashboardVisible,
        relationFromName,
        relationFromDisplayName,
        relationFromAttributesNames,
        relationFromAttributesDisplayNames,
        dqName,
        dqDescription,
        group,
        groupDisplayName,
        classifiers;
    }
}
