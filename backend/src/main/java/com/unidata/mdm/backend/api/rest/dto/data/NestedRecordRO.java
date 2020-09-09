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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yashin. Created on 02.06.2015.
 */
public class NestedRecordRO {

    /**
     * Simple attributes.
     */
    private List<SimpleAttributeRO> simpleAttributes = new ArrayList<>();
    /**
     * Simple attributes.
     */
    private List<ArrayAttributeRO> arrayAttributes = new ArrayList<>();
    /**
     * Complex attributes.
     */
    private List<ComplexAttributeRO> complexAttributes = new ArrayList<>();
    /**
     * DQ errors list.
     */
    private List<DQErrorRO> dqErrors = new ArrayList<>();
    /**
     * Gets simple attributes.
     * @return list
     */
    public List<SimpleAttributeRO> getSimpleAttributes() {
        return simpleAttributes;
    }
    /**
     * Sets simple attributes.
     * @param simpleAttributes attributes list
     */
    public void setSimpleAttributes(List<SimpleAttributeRO> simpleAttributes) {
        this.simpleAttributes = simpleAttributes;
    }
    /**
     * @return the arrayAttributes
     */
    public List<ArrayAttributeRO> getArrayAttributes() {
        return arrayAttributes;
    }
    /**
     * @param arrayAttributes the arrayAttributes to set
     */
    public void setArrayAttributes(List<ArrayAttributeRO> arrayAttributes) {
        this.arrayAttributes = arrayAttributes;
    }
    /**
     * Gets simple attributes.
     * @return complex attributes list
     */
    public List<ComplexAttributeRO> getComplexAttributes() {
        return complexAttributes;
    }
    /**
     * Sets simple attributes.
     * @param complexAttributes complex attributes list
     */
    public void setComplexAttributes(List<ComplexAttributeRO> complexAttributes) {
        this.complexAttributes = complexAttributes;
    }

    /**
     * @return the errors
     */
    public List<DQErrorRO> getDqErrors() {
        if (dqErrors == null) {
            this.dqErrors = new ArrayList<>();
        }
        return dqErrors;
    }

    /**
     * @param errors
     *            the errors to set
     */
    public void setDqErrors(List<DQErrorRO> errors) {
        this.dqErrors = errors;
    }
}
