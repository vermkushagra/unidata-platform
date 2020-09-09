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

package com.unidata.mdm.backend.service.classifier.cache;

import java.io.Serializable;

/**
 * @author Mikhail Mikhailov
 * Custom property with {@link Serializable} mark.
 */
public class CachedClassifierCustomProperty implements Serializable {
    /**
     * The name.
     */
    private String name;
    /**
     * The value.
     */
    private String value;
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -2220372297989773885L;
    /**
     * Constructor.
     */
    public CachedClassifierCustomProperty() {
        super();
    }
    /**
     * Constructor.
     * @param name the name
     * @param value the value
     */
    public CachedClassifierCustomProperty(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
