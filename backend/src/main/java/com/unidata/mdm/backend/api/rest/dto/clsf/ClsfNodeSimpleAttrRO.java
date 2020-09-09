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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.util.serializer.ClassifierSimpleAttributeDeserializer;
import com.unidata.mdm.backend.api.rest.util.serializer.ClassifierSimpleAttributeSerializer;

import java.util.function.Function;

/**
 * The Class ClassifierAttributeRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ClassifierSimpleAttributeDeserializer.class)
@JsonSerialize(using = ClassifierSimpleAttributeSerializer.class)
public class ClsfNodeSimpleAttrRO extends ClsfNodeAttrRO {

    /**
     * data type.
     */
    private SimpleDataType simpleDataType;

    /** Default or final value of attribute for classifier. */
    private Object value;

    /**
     * Enum data type.
     */
    private String enumDataType;

    /**
     * Gets the simple data type.
     *
     * @return the simple data type
     */
    public SimpleDataType getSimpleDataType() {
        return simpleDataType;
    }

    /**
     * Sets the simple data type.
     *
     * @param simpleDataType
     *            the new simple data type
     */
    public void setSimpleDataType(SimpleDataType simpleDataType) {
        this.simpleDataType = simpleDataType;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the new value
     */
    public void setValue(String value) {
        this.value = stringToObjectConverter().apply(value);
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the new value
     */
    public void setValueObj(Object value) {
        this.value = value;
    }

    public String getEnumDataType() {
        return enumDataType;
    }

    public void setEnumDataType(String enumDataType) {
        this.enumDataType = enumDataType;
    }

    @Override
    protected Function<String, Object> dataTypeConverter() {
        if (getSimpleDataType() != null) {
            switch (getSimpleDataType()) {
                case BOOLEAN:
                    return booleanConverter;
                case DATE:
                case TIME:
                case TIMESTAMP:
                    return dateConverter(getSimpleDataType());
                case INTEGER:
                    return integerConverter;
                case NUMBER:
                    return numberConverter;
                case STRING:
                    return stringConverter;
                default:
                    throw new RuntimeException("Unsupported data type: " + getSimpleDataType().name());
            }
        }
        return stringConverter;
    }
}
