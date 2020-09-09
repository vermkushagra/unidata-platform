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

package com.unidata.mdm.backend.po.matching;

import java.util.Collection;
/**
 * Matchig rule PO.
 */
public class MatchingRulePO {
    /**
     * Id field.
     */
    public static final String FIELD_ID = "id";
    /**
     * Name field.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Settings field.
     */
    public static final String FIELD_SETTINGS = "settings";
    /**
     * Entity name field.
     */
    public static final String FIELD_ENTITY_NAME = "entity_name";
    /**
     * Active field.
     */
    public static final String FIELD_ACTIVE = "active";
    /**
     * Description field.
     */
    public static final String FIELD_DESCRIPTION = "description";
    /**
     * Storage field.
     */
    public static final String FIELD_STORAGE_FKEY = "storage_fkey";
    /**
     * Preprocessing flag field.
     */
    public static final String FIELD_WITH_PREPROCESSING = "preprocessing";
    /**
     * Auto merge flag field
     */
    public static final String FIELD_AUTO_MERGE = "auto_merge";
    /**
     * Id.
     */
    private Integer id;
    /**
     * Name.
     */
    private String name;
    /**
     * Settings.
     */
    private String settings;
    /**
     * Entity name.
     */
    private String entityName;
    /**
     * Description.
     */
    private String description;
    /**
     * Active or not.
     */
    private boolean active;
    /**
     * 'Preprocessing enabled' flag.
     */
    private boolean withPreprocessing;
    /**
     * 'Automerge enabled' flag.
     */
    private boolean autoMerge;
    /**
     * Matching algorithms.
     */
    private Collection<MatchingAlgorithmPO> matchingAlgorithms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Collection<MatchingAlgorithmPO> getMatchingAlgorithms() {
        return matchingAlgorithms;
    }

    public void setMatchingAlgorithms(Collection<MatchingAlgorithmPO> matchingAlgorithms) {
        this.matchingAlgorithms = matchingAlgorithms;
    }

    public boolean isWithPreprocessing() {
        return withPreprocessing;
    }

    public void setWithPreprocessing(boolean withPreprocessing) {
        this.withPreprocessing = withPreprocessing;
    }

    public boolean isAutoMerge() {
        return autoMerge;
    }

    public void setAutoMerge(boolean autoMerge) {
        this.autoMerge = autoMerge;
    }
}
