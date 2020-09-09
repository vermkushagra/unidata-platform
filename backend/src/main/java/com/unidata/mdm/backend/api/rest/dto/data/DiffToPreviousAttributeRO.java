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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiffToPreviousAttributeRO {

    private String path;

    private String action;

    private SimpleAttributeRO oldSimpleValue;

    private ComplexAttributeRO oldComplexValue;

    private CodeAttributeRO oldCodeValue;

    private ArrayAttributeRO oldArrayValue;
    /**
     * Constructor.
     */
    public DiffToPreviousAttributeRO() {
        super();
    }
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }
    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }
    /**
     * @return the oldSimpleValue
     */
    public SimpleAttributeRO getOldSimpleValue() {
        return oldSimpleValue;
    }
    /**
     * @param oldSimpleValue the oldSimpleValue to set
     */
    public void setOldSimpleValue(SimpleAttributeRO oldSimpleValue) {
        this.oldSimpleValue = oldSimpleValue;
    }
    /**
     * @return the oldComplexValue
     */
    public ComplexAttributeRO getOldComplexValue() {
        return oldComplexValue;
    }
    /**
     * @param oldComplexValue the oldComplexValue to set
     */
    public void setOldComplexValue(ComplexAttributeRO oldComplexValue) {
        this.oldComplexValue = oldComplexValue;
    }
    /**
     * @return the oldCodeValue
     */
    public CodeAttributeRO getOldCodeValue() {
        return oldCodeValue;
    }
    /**
     * @param oldCodeValue the oldCodeValue to set
     */
    public void setOldCodeValue(CodeAttributeRO oldCodeValue) {
        this.oldCodeValue = oldCodeValue;
    }
    /**
     * @return the oldArrayValue
     */
    public ArrayAttributeRO getOldArrayValue() {
        return oldArrayValue;
    }
    /**
     * @param oldArrayValue the oldArrayValue to set
     */
    public void setOldArrayValue(ArrayAttributeRO oldArrayValue) {
        this.oldArrayValue = oldArrayValue;
    }

}
