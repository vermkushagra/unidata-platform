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
 * Single cleanse function configuration.
 */
public class SingleCleanseFunctionConfiguration extends CleanseFunctionConfiguration {
    /**
     * Java class.
     */
    private String javaClass;
    /**
     * Constructor.
     */
    public SingleCleanseFunctionConfiguration() {
        super();
    }
    /**
     * @return the javaClass
     */
    public String getJavaClass() {
        return javaClass;
    }
    /**
     * @param javaClass the javaClass to set
     */
    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CleanseFunctionType getType() {
        return CleanseFunctionType.SINGLE;
    }
    /**
     * Fluent setter.
     * @param functionName the function name
     * @return self
     */
    public SingleCleanseFunctionConfiguration withFunctionName(String functionName) {
        super.setFunctionName(functionName);
        return this;
    }
    /**
     * Fluent setter.
     * @param description the description
     * @return self
     */
    public SingleCleanseFunctionConfiguration withDescription(String description) {
        super.setDescription(description);
        return this;
    }
    /**
     * Fluent setter.
     * @param javaClass the javaClass
     * @return self
     */
    public SingleCleanseFunctionConfiguration withJavaClass(String javaClass) {
        setJavaClass(javaClass);
        return this;
    }
}
