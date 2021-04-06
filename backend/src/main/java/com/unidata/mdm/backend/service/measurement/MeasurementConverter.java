package com.unidata.mdm.backend.service.measurement;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.po.measurement.MeasurementUnitPO;
import com.unidata.mdm.backend.po.measurement.MeasurementValuePO;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;

public class MeasurementConverter {

    @Nonnull
    public static MeasurementValuePO convert(@Nonnull MeasurementValue measurementValue) {
        MeasurementValuePO measurementValuePO = new MeasurementValuePO();
        measurementValuePO.setShortName(measurementValue.getShortName());
        measurementValuePO.setId(measurementValue.getId());
        measurementValuePO.setName(measurementValue.getName());
        measurementValuePO.setMeasurementUnits(measurementValue.getMeasurementUnits()
                .stream()
                .map(MeasurementConverter::convert)
                .collect(Collectors.toList()));
        return measurementValuePO;
    }

    @Nonnull
    public static MeasurementUnitPO convert(@Nonnull MeasurementUnit measurementUnit) {
        MeasurementUnitPO measurementUnitPO = new MeasurementUnitPO();
        measurementUnitPO.setName(measurementUnit.getName());
        measurementUnitPO.setId(measurementUnit.getId());
        measurementUnitPO.setShortName(measurementUnit.getShortName());
        measurementUnitPO.setBase(measurementUnit.isBase());
        measurementUnitPO.setConvectionFunction(measurementUnit.getConvectionFunction());
        measurementUnitPO.setValueId(measurementUnit.getValueId());
        measurementUnitPO.setOrder(measurementUnit.getOrder());
        return measurementUnitPO;
    }

    @Nonnull
    public static MeasurementValue convert(@Nonnull MeasurementValuePO measurementValue) {
        MeasurementValue measurementValuePO = new MeasurementValue();
        measurementValuePO.setShortName(measurementValue.getShortName());
        measurementValuePO.setId(measurementValue.getId());
        measurementValuePO.setName(measurementValue.getName());
        measurementValuePO.setMeasurementUnits(measurementValue.getMeasurementUnits()
                .stream()
                .map(MeasurementConverter::convert)
                .collect(Collectors.toMap(MeasurementUnit::getId, (v) -> v)));
        MeasurementUnit baseUnit = measurementValuePO.getMeasurementUnits().stream().filter(MeasurementUnit::isBase).findAny().orElse(null);
        measurementValuePO.setBaseUnitId(baseUnit == null ? null : baseUnit.getId());
        return measurementValuePO;
    }

    @Nonnull
    public static MeasurementUnit convert(@Nonnull MeasurementUnitPO measurementUnit) {
        MeasurementUnit measurementUnitPO = new MeasurementUnit();
        measurementUnitPO.setName(measurementUnit.getName());
        measurementUnitPO.setId(measurementUnit.getId());
        measurementUnitPO.setShortName(measurementUnit.getShortName());
        measurementUnitPO.setBase(measurementUnit.isBase());
        measurementUnitPO.setConvectionFunction(measurementUnit.getConvectionFunction());
        measurementUnitPO.setValueId(measurementUnit.getValueId());
        measurementUnitPO.setOrder(measurementUnit.getOrder());
        return measurementUnitPO;
    }
}
