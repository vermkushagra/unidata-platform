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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.stereotype.Service;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;

@Service
public class MeasurementConversionServiceImpl
        implements MeasurementConversionService {

    private static final String VALUE = "value";
    private static final String UNIT = "unit";
    private static final String VAR = "var ";
    private static final String FUNCTION = "= function (value) {return ";
    private static final String END_OF_FUNCTION = ";};";
    private static final String UNDEFINED = "= undefined;";
    private static final Double TEST_VALUE = 10d;
    private static final ScriptEngine NASHORN = new ScriptEngineManager().getEngineByName("nashorn");

    @Override
    public void registerMeasurementUnit(@Nonnull MeasurementUnit measurementUnit) {
        String jsFunction = generateJsFunction(measurementUnit);
        try {
            NASHORN.eval(jsFunction);
            //test function
            convert(TEST_VALUE, measurementUnit);
        } catch (ScriptException e) {
            throw new BusinessException("Conversion function is incorrect: " + jsFunction, e,
                    ExceptionId.EX_MEASUREMENT_CONVERSION_FAILED);
        }
    }

    @Override
    public void removeMeasurementUnit(@Nonnull MeasurementUnit measurementUnit) {
        String jsFunction = generateUndefinedJsFunction(measurementUnit);
        try {
            NASHORN.eval(jsFunction);
        } catch (ScriptException e) {
            throw new BusinessException("Conversion function is incorrect: " + jsFunction, e,
                    ExceptionId.EX_MEASUREMENT_CONVERSION_FAILED);
        }
    }

    @Nonnull
    private String generateJsFunction(@Nonnull MeasurementUnit measurementUnit) {
        //var name = function (value) {return value*100;};
        return VAR + getFunctionName(measurementUnit) + FUNCTION + measurementUnit.getConvectionFunction()
                + END_OF_FUNCTION;
    }

    @Nonnull
    private String generateUndefinedJsFunction(@Nonnull MeasurementUnit measurementUnit) {
        // var name = undefined;
        return VAR + getFunctionName(measurementUnit) + UNDEFINED;
    }

    @Override
    @Nonnull
    public Double convert(@Nonnull Double input, @Nullable MeasurementUnit toUnit) {
        if (toUnit == null) {
            return input;
        }
        Invocable invocable = (Invocable) NASHORN;
        try {
            return (Double) invocable.invokeFunction(getFunctionName(toUnit), input);
        } catch (ScriptException | NoSuchMethodException se) {
            throw new BusinessException("Conversion function is incorrect ", se,
                    ExceptionId.EX_MEASUREMENT_CONVERSION_FAILED);
        }
    }

    @Nonnull
    private String getFunctionName(@Nonnull MeasurementUnit unit) {
        return VALUE + unit.getValueId() + UNIT + unit.getId();
    }

}
