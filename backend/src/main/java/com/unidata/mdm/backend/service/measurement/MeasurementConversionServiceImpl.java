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
        implements MeasurementConversionService, EntryAddedListener<String, MeasurementValue>,
        EntryRemovedListener<String, MeasurementValue>, EntryEvictedListener<String, MeasurementValue> {

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
            throw new BusinessException("Conversion function is incorrect ",
                    ExceptionId.EX_MEASUREMENT_CONVERSION_FAILED);
        }
    }

    @Override
    public void removeMeasurementUnit(@Nonnull MeasurementUnit measurementUnit) {
        String jsFunction = generateUndefinedJsFunction(measurementUnit);
        try {
            NASHORN.eval(jsFunction);
        } catch (ScriptException e) {
            throw new BusinessException("Conversion function is incorrect ",
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
            throw new BusinessException("Conversion function is incorrect ",
                    ExceptionId.EX_MEASUREMENT_CONVERSION_FAILED);
        }
    }

    @Nonnull
    private String getFunctionName(@Nonnull MeasurementUnit unit) {
        return VALUE + unit.getValueId() + UNIT + unit.getId();
    }

    @Override
    public void entryAdded(EntryEvent<String, MeasurementValue> event) {
        event.getValue().getMeasurementUnits().forEach(this::registerMeasurementUnit);
    }

    @Override
    public void entryRemoved(EntryEvent<String, MeasurementValue> event) {
        event.getOldValue().getMeasurementUnits().forEach(this::removeMeasurementUnit);
    }

    @Override
    public void entryEvicted(EntryEvent<String, MeasurementValue> event) {
        event.getValue().getMeasurementUnits().forEach(this::removeMeasurementUnit);
    }
}
