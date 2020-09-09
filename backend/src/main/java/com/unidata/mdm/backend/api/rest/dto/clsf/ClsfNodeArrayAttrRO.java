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

package com.unidata.mdm.backend.api.rest.dto.clsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;
import com.unidata.mdm.backend.api.rest.util.serializer.ClassifierArrayAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.ClassifierArrayAttributeSerializer;
import org.apache.commons.collections4.CollectionUtils;

/**
 * The Class ClassifierAttributeRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ClassifierArrayAttributeDeserializer.class)
@JsonSerialize(using = ClassifierArrayAttributeSerializer.class)
public class ClsfNodeArrayAttrRO extends ClsfNodeAttrRO {

    /**
     * Array data type.
     */
    private ArrayDataType arrayDataType;

    /** Default or final value of attribute for classifier. */
    private final List<Object> values = new ArrayList<>();

    /**
     * Gets the array data type.
     *
     * @return the array data type
     */
    public ArrayDataType getArrayDataType() {
        return arrayDataType;
    }

    /**
     * Sets the array data type.
     *
     * @param arrayDataType
     *            the new array data type
     */
    public void setArrayDataType(ArrayDataType arrayDataType) {
        this.arrayDataType = arrayDataType;
    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    public List<Object> getValues() {
        return values;
    }

    /**
     * Sets the value.
     *
     * @param values
     *            the new values
     */
    public void setValues(Collection<String> values) {
        this.values.clear();
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        this.values.addAll(
                values.stream().map(stringToObjectConverter()).collect(Collectors.toList())
        );
    }

    /**
     * Sets the values.
     *
     * @param values
     *            the new value
     */
    public void setValuesObj(final List<Object> values) {
        this.values.clear();
        if (CollectionUtils.isNotEmpty(values)) {
            this.values.addAll(values);
        }
    }

    @Override
    protected Function<String, Object> dataTypeConverter() {
        if (getArrayDataType() != null) {
            switch (getArrayDataType()) {
                case DATE:
                case TIME:
                case TIMESTAMP:
                    return dateConverter(getArrayDataType());
                case INTEGER:
                    return integerConverter;
                case NUMBER:
                    return numberConverter;
                case STRING:
                    return stringConverter;
                default:
                    throw new RuntimeException("Unsupported data type: " + getArrayDataType().name());
            }
        }
        return stringConverter;
    }
}
