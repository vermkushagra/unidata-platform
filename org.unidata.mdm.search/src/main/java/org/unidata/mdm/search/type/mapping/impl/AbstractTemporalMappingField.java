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

package org.unidata.mdm.search.type.mapping.impl;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 */
public abstract class AbstractTemporalMappingField<X extends AbstractTemporalMappingField<X>> extends AbstractValueMappingField<X> {
    /**
     * Accepted format.
     */
    private String format;
    /**
     * Constructor.
     * @param name
     */
    public AbstractTemporalMappingField(String name) {
        super(name);
    }
    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }
    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public X withFormat(String format) {
        setFormat(format);
        return self();
    }
}
