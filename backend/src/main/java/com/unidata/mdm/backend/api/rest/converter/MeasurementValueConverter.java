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
