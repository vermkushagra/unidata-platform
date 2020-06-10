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

package com.unidata.mdm.backend.common.cleanse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractArrayAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.ComplexAttributeImpl;

/**
 * @author Mikhail Mikhailov
 * Output param type.
 */
public class CleanseFunctionOutputParam extends CleanseFunctionParam {
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, Collection<DataRecord> value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(ComplexAttributeImpl.ofUnattended(portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, Attribute value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(value));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam ofIntegers(String portName, List<Long> value) {
        return new CleanseFunctionOutputParam(portName,
                Collections.singletonList(AbstractArrayAttribute.of(ArrayDataType.INTEGER, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam ofNumbers(String portName, List<Double> value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractArrayAttribute.of(ArrayDataType.NUMBER, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam ofStrings(String portName, List<String> value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractArrayAttribute.of(ArrayDataType.STRING, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam ofTimestamps(String portName, List<LocalDateTime> value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractArrayAttribute.of(ArrayDataType.TIMESTAMP, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam ofTimes(String portName, List<LocalTime> value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractArrayAttribute.of(ArrayDataType.TIME, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam ofDates(String portName, List<LocalDate> value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractArrayAttribute.of(ArrayDataType.DATE, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, Boolean value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.BOOLEAN, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, Long value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.INTEGER, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, Double value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.NUMBER, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, String value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.STRING, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, LocalDateTime value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.TIMESTAMP, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, LocalTime value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.TIME, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, LocalDate value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.DATE, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, BinaryLargeValue value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.BLOB, portName, value)));
    }
    /**
     * Creates output value param.
     * @param portName
     * @param value
     * @return param
     */
    public static CleanseFunctionOutputParam of(String portName, CharacterLargeValue value) {
        return new CleanseFunctionOutputParam(portName, Collections.singletonList(AbstractSimpleAttribute.of(DataType.CLOB, portName, value)));
    }
    /**
     * Constructor.
     */
    private CleanseFunctionOutputParam(String portName, List<Attribute> values) {
        super(ParamType.OUTPUT, portName, values);
    }

}
