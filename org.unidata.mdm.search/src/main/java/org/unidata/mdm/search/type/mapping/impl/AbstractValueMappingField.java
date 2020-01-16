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
 * @author Mikhail Mikhailov on Oct 9, 2019
 */
public abstract class AbstractValueMappingField<X extends AbstractValueMappingField<X>> extends AbstractMappingField<X> {
    /**
     * Index as doc value.
     */
    private boolean docValue;
    /**
     * The 'null' value.
     */
    private Object defaultValue;
    /**
     * Constructor.
     * @param name
     */
    public AbstractValueMappingField(String name) {
        super(name);
    }
    /**
     * @return the docValue
     */
    public boolean isDocValue() {
        return docValue;
    }
    /**
     * @param docValue the docValue to set
     */
    public void setDocValue(boolean docValue) {
        this.docValue = docValue;
    }
    /**
     * @return the defaultValue
     */
    public Object getDefaultValue() {
        return defaultValue;
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    /**
     * Sets field's storage to doc value.
     * @param docValue the flag
     * @return self
     */
    public X withDocValue(boolean docValue) {
        setDocValue(docValue);
        return self();
    }
    /**
     * @param defaultValue the defaultValue to set
     */
    public X withDefaultValue(Object defaultValue) {
        setDefaultValue(defaultValue);
        return self();
    }
}
