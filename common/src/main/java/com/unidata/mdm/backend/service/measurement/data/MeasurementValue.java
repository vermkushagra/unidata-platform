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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * MeasurementValue - it is a values like a length,mass etc;
 */
public class MeasurementValue implements Serializable{
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4851410968298290660L;
    /**
     * User defined unique id of measurement value
     */
    private String id;
    //in future store this names in separated table.
    /**
     * value display name
     */
    private String name;
    /**
     * short display name
     */
    private String shortName;
    /**
     * base unit id
     */
    private String baseUnitId;
    /**
     * Map of all included measurement units
     */
    private Map<String, MeasurementUnit> measurementUnits = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getBaseUnitId() {
        return baseUnitId;
    }

    public void setBaseUnitId(String baseUnitId) {
        this.baseUnitId = baseUnitId;
    }

    @Nullable
    public MeasurementUnit getUnitById(@Nonnull String unitId) {
        return measurementUnits.get(unitId);
    }

    @Nonnull
    public MeasurementUnit getBaseUnit() {
        return measurementUnits.get(baseUnitId);
    }

    @Nonnull
    public Collection<MeasurementUnit> getMeasurementUnits() {
        return measurementUnits.values();
    }

    public void setMeasurementUnits(Map<String, MeasurementUnit> measurementUnits) {
        this.measurementUnits = measurementUnits;
    }

    /**
     * @param unitId - unit id
     * @return true if unit present in value, otherwise false
     */
    public boolean present(@Nonnull String unitId) {
        return measurementUnits.keySet().contains(unitId);
    }

    @Override
    public String toString() {
        return "MeasurementValue{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", baseUnitId=" + baseUnitId +
                ", measurementUnits=" + measurementUnits +
                '}';
    }
}
