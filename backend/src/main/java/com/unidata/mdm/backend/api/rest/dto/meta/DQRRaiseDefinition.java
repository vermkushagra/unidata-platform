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

package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 11.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQRRaiseDefinition {
    protected PhaseDefinition phase;
    protected String functionRaiseErrorPort;

    protected String messagePort;

    protected String messageText;

    protected String severityPort;

    protected String severityValue;

    protected String categoryPort;

    protected String categoryText;

    protected String pathsPort;

    public String getFunctionRaiseErrorPort() {
        return functionRaiseErrorPort;
    }

    public void setFunctionRaiseErrorPort(String functionRaiseErrorPort) {
        this.functionRaiseErrorPort = functionRaiseErrorPort;
    }

    public String getMessagePort() {
        return messagePort;
    }

    public void setMessagePort(String messagePort) {
        this.messagePort = messagePort;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSeverityPort() {
        return severityPort;
    }

    public void setSeverityPort(String severityPort) {
        this.severityPort = severityPort;
    }

    public String getSeverityValue() {
        return severityValue;
    }

    public void setSeverityValue(String severityValue) {
        this.severityValue = severityValue;
    }

    public String getCategoryPort() {
        return categoryPort;
    }

    public void setCategoryPort(String categoryPort) {
        this.categoryPort = categoryPort;
    }

    public String getCategoryText() {
        return categoryText;
    }

    public void setCategoryText(String categoryText) {
        this.categoryText = categoryText;
    }

    public PhaseDefinition getPhase() {
        return phase;
    }

    public void setPhase(PhaseDefinition phase) {
        this.phase = phase;
    }

    /**
     * @return the pathsPort
     */
    public String getPathsPort() {
        return pathsPort;
    }

    /**
     * @param pathsPort the pathsPort to set
     */
    public void setPathsPort(String pathsPort) {
        this.pathsPort = pathsPort;
    }
}
