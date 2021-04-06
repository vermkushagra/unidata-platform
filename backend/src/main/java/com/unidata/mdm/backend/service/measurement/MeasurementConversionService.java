package com.unidata.mdm.backend.service.measurement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;

/**
 * Measured values conversion service
 */
public interface MeasurementConversionService {

    /**
     * @param measurementUnit - register conversion function of unit
     */
    void registerMeasurementUnit(@Nonnull MeasurementUnit measurementUnit);

    /**
     * @param measurementUnit - already registered conversion function of unit
     */
    void removeMeasurementUnit(@Nonnull MeasurementUnit measurementUnit);

    /**
     * @param input  - input
     * @param toUnit - to unit
     * @return converted result , or the same if toUnit is null
     */
    @Nonnull
    Double convert(@Nonnull Double input, @Nullable MeasurementUnit toUnit);
}
