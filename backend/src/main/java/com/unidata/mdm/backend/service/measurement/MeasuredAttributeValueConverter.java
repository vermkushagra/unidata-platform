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

package com.unidata.mdm.backend.service.measurement;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.tuple.Pair.of;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;

@Component
public class MeasuredAttributeValueConverter {

    /**
     * Measurement service
     */
    @Autowired
    private MetaMeasurementService measurementService;
    /**
     * Measurement conversion service
     */
    @Autowired
    private MeasurementConversionService measurementConversionService;

    /**
     * All measured attributes will be modified,
     * Initial value will be used for calculating base value (value presented in base measurement unit)
     *
     * @param dataRecord - record
     */
    public void enrichMeasuredAttributesByBase(@Nonnull DataRecord dataRecord) {
        dataRecord.getAllAttributesRecursive()
                  .stream()
                  .filter(attr -> attr.getAttributeType() == Attribute.AttributeType.SIMPLE)
                  .filter(attr -> ((AbstractSimpleAttribute<?>) attr).getDataType() == SimpleAttribute.DataType.MEASURED)
                  .map(attr -> (MeasuredSimpleAttributeImpl) attr)
                  .filter(attr -> nonNull(attr.getValue()) && nonNull(attr.getValueId()) && nonNull(attr.getInitialUnitId()))
                  .map(attr -> of(attr, measurementService.getUnitById(attr.getValueId(), attr.getInitialUnitId())))
                  .filter(pair -> pair.getValue() != null)
                  .forEach(pair -> pair.getKey().setValue(getConvertedValue(pair.getKey(), pair.getValue())));
    }

    private Double getConvertedValue(@Nonnull MeasuredSimpleAttributeImpl input,
            @Nullable MeasurementUnit measurementUnit) {
        return measurementConversionService.convert(input.getInitialValue(), measurementUnit);
    }
}
