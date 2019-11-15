package org.unidata.mdm.meta.po;

import java.util.ArrayList;
import java.util.Collection;

public class MeasurementValuePO {
    private String id;
    //in future store this names in separated table.
    private String name;
    private String shortName;
    private Collection<MeasurementUnitPO> measurementUnits = new ArrayList<>();

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

    public Collection<MeasurementUnitPO> getMeasurementUnits() {
        return measurementUnits;
    }

    public void setMeasurementUnits(Collection<MeasurementUnitPO> measurementUnits) {
        this.measurementUnits = measurementUnits;
    }
}
