package com.unidata.mdm.backend.api.rest.dto.measurement;

public class MeasurementUnitDto {
    private String id;
    private String shortName;
    private String name;
    private String valueId;
    private boolean isBase = false;
    private String convectionFunction;

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

    public String getConvectionFunction() {
        return convectionFunction;
    }

    public void setConvectionFunction(String convectionFunction) {
        this.convectionFunction = convectionFunction;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isBase() {
        return isBase;
    }

    public void setBase(boolean base) {
        isBase = base;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }
}
