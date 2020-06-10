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
