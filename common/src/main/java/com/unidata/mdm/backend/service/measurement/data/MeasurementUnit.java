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

package com.unidata.mdm.backend.service.measurement.data;

import java.io.Serializable;

/**
 * Measurement unit it is a data structure which describe values like: grams, meters, volts etc
 */
public class MeasurementUnit implements Serializable{
    /**
     * User defined unique id of unit
     */
    private String id;
    /**
     * User defined short display name
     */
    private String shortName;
    /**
     * User defined display name
     */
    private String name;
    /**
     * Id of related measurement value
     */
    private String valueId;
    /**
     * Flag which show it is unit base, or not. (Can be only one base unit in value)
     */
    private boolean isBase = false;
    /**
     * Conversion function to base unti, for base unit it equals (value).
     * Example: value*1000
     */
    private String convectionFunction;
    /**
     * Order in measurement value
     */
    private int order;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public boolean isBase() {
        return isBase;
    }

    public void setBase(boolean base) {
        isBase = base;
    }

    public String getConvectionFunction() {
        return convectionFunction;
    }

    public void setConvectionFunction(String convectionFunction) {
        this.convectionFunction = convectionFunction;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "MeasurementUnit{" +
                "id='" + id + '\'' +
                ", shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", valueId='" + valueId + '\'' +
                ", isBase=" + isBase +
                ", convectionFunction='" + convectionFunction + '\'' +
                '}';
    }
}
