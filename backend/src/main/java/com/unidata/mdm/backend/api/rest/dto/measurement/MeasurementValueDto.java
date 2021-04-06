package com.unidata.mdm.backend.api.rest.dto.measurement;

import java.util.Collection;

public class MeasurementValueDto {
    private String id;
    //in future store this names in separated table.
    private String name;
    private String shortName;
    private Collection<MeasurementUnitDto> measurementUnits;

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

    public Collection<MeasurementUnitDto> getMeasurementUnits() {
        return measurementUnits;
    }

    public void setMeasurementUnits(Collection<MeasurementUnitDto> measurementUnitDtos) {
        this.measurementUnits = measurementUnitDtos;
    }
}