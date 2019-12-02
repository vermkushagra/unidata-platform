package org.unidata.mdm.meta.dto;

import org.unidata.mdm.core.type.measurement.MeasurementValue;

/**
 * @author Mikhail Mikhailov on Dec 3, 2019
 */
public class GetModelMeasurementValueDTO {
    /**
     * MMV.
     */
    private MeasurementValue measurementValue;

    public GetModelMeasurementValueDTO() {
        super();
    }

    public GetModelMeasurementValueDTO(MeasurementValue value) {
        this();
        this.measurementValue = value;
    }

    /**
     * @return the measurementValue
     */
    public MeasurementValue getMeasurementValue() {
        return measurementValue;
    }

    /**
     * @param measurementValue the measurementValue to set
     */
    public void setMeasurementValue(MeasurementValue measurementValue) {
        this.measurementValue = measurementValue;
    }
}
