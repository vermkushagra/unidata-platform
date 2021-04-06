package com.unidata.mdm.backend.api.rest.converter;

import javax.annotation.Nonnull;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.measurement.MeasurementUnitDto;
import com.unidata.mdm.backend.api.rest.dto.measurement.MeasurementValueDto;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;

public class MeasurementValueConverter {

    private static final Comparator<MeasurementUnit> MEASUREMENT_UNIT_COMPARATOR = (o1, o2) -> Integer.compare(o1.getOrder(),o2.getOrder());

    @Nonnull
    public static MeasurementValueDto convert(@Nonnull MeasurementValue measurementValue) {
        MeasurementValueDto measurementValueDto = new MeasurementValueDto();
        measurementValueDto.setShortName(measurementValue.getShortName());
        measurementValueDto.setId(measurementValue.getId());
        measurementValueDto.setName(measurementValue.getName());
        measurementValueDto.setMeasurementUnits(measurementValue.getMeasurementUnits()
                .stream()
                .sequential()
                .sorted(MEASUREMENT_UNIT_COMPARATOR)
                .map(MeasurementValueConverter::convert)
                .collect(Collectors.toList()));
        return measurementValueDto;
    }

    @Nonnull
    public static MeasurementUnitDto convert(@Nonnull MeasurementUnit measurementUnit) {
        MeasurementUnitDto measurementUnitDto = new MeasurementUnitDto();
        measurementUnitDto.setName(measurementUnit.getName());
        measurementUnitDto.setId(measurementUnit.getId());
        measurementUnitDto.setShortName(measurementUnit.getShortName());
        measurementUnitDto.setBase(measurementUnit.isBase());
        measurementUnitDto.setConvectionFunction(measurementUnit.getConvectionFunction());
        measurementUnitDto.setValueId(measurementUnit.getValueId());
        return measurementUnitDto;
    }

}
