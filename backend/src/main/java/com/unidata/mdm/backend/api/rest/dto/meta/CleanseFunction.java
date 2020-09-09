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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 20.05.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CleanseFunction extends CleanseFunctionTreeElement {
    protected String javaClass;
    protected CleanseFunctionType type;

    private List<DQRRuleExecutionContext> supportedExecutionContexts;

    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public CleanseFunctionType getType() {
        return type;
    }

    public void setType(CleanseFunctionType type) {
        this.type = type;
    }
    /**
     * @return the supportedExecutionContexts
     */
    public List<DQRRuleExecutionContext> getSupportedExecutionContexts() {
        return supportedExecutionContexts;
    }

    /**
     * @param supportedExecutionContexts the supportedExecutionContexts to set
     */
    public void setSupportedExecutionContexts(List<DQRRuleExecutionContext> supportedExecutionContexts) {
        this.supportedExecutionContexts = supportedExecutionContexts;
    }
}
