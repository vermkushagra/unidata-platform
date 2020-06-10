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

package com.unidata.mdm.backend.common.cleanse;

/**
 * @author Mikhail Mikhailov
 * Cleanse function configuration top level class.
 */
public abstract class CleanseFunctionConfiguration {
    /**
     * Name of the function.
     */
    protected String functionName;
    /**
     * Description.
     */
    protected String description;
    /**
     * Constructor.
     */
    public CleanseFunctionConfiguration() {
        super();
    }
    /**
     * @return the functionName
     */
    public String getFunctionName() {
        return functionName;
    }
    /**
     * @param functionName the functionName to set
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Cleanse function type.
     * @return
     */
    public abstract CleanseFunctionType getType();
}
