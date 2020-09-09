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

package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.meta.DQApplicableDefinition;

/**
 * The Class DQErrorRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQErrorRO {

    /** The severity. */
    private String severity;

    /** The category. */
    private String category;

    /** The message. */
    private String message;

    /** The rule name. */
    private String ruleName;

    private DQApplicableDefinition phase;
    /**
     * Attribute local paths.
     */
    private List<String> paths;
    /**
     * Gets the severity.
     *
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the severity.
     *
     * @param severity
     *            the severity to set
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Gets the category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     *
     * @param category
     *            the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the rule name.
     *
     * @return the ruleName
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Sets the rule name.
     *
     * @param ruleName
     *            the ruleName to set
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * @return the phase
     */
    public DQApplicableDefinition getPhase() {
        return phase;
    }

    /**
     * @param phase
     *            the phase to set
     */
    public void setPhase(DQApplicableDefinition phase) {
        this.phase = phase;
    }

    /**
     * @return the paths
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * @param paths the paths to set
     */
    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
