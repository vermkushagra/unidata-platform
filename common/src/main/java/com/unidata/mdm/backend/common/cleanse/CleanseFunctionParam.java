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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

/**
 * @author Mikhail Mikhailov
 * Cleanse function param base type.
 */
public abstract class CleanseFunctionParam {
    /**
     * The values.
     */
    protected final List<Attribute> values;
    /**
     * Name of the port.
     */
    private final String portName;
    /**
     * Type of the param.
     */
    private final ParamType paramType;
    /**
     * Type of the param
     * @author Mikhail Mikhailov
     */
    public enum ParamType {
        /**
         * Input param type.
         */
        INPUT,
        /**
         * Output param type.
         */
        OUTPUT
    }
    /**
     * Constructor.
     * @param paramType type of the param
     * @param portName name ofthe port
     * @param values the values to hold
     */
    protected CleanseFunctionParam(ParamType paramType, String portName, List<Attribute> values) {
        super();
        this.paramType = paramType;
        this.portName = portName;
        this.values = values;
    }
    /**
     * @return the portName
     */
    public String getPortName() {
        return portName;
    }
    /**
     * @return the paramType
     */
    public ParamType getParamType() {
        return paramType;
    }
    /**
     * Returns true, if the underlaying collection holds only one element.
     * @return true, if holds a single value, false otherwise
     */
    public boolean isSingleton() {
        return values.size() == 1;
    }
    /**
     * Returns value, if this is the only value, hold by the underlaying collection.
     * Otherwise returns null
     * @return object or null
     */
    @SuppressWarnings("unchecked")
    public<T extends Attribute> T getSingleton() {

        if (CollectionUtils.isNotEmpty(values) && values.size() == 1) {
            return (T) values.iterator().next();
        }

        return null;
    }
    /**
     * Extracts singleton as typed object.
     * @return object or null
     * @throws ClassCastException
     */
    @SuppressWarnings("unchecked")
    public<T> T toSingletonValue() {
        return (T) toSingletonValueObject();
    }
    /**
     * Extracts singleton as object.
     * @return object or null
     */
    public Object toSingletonValueObject() {

        if (isSingleton()) {

            Attribute attribute = getSingleton();
            if (attribute.getAttributeType() == AttributeType.SIMPLE) {
                return ((SimpleAttribute<?>) attribute).castValue();
            } else if (attribute.getAttributeType() == AttributeType.CODE) {
                return ((CodeAttribute<?>) attribute).castValue();
            } else if (attribute.getAttributeType() == AttributeType.ARRAY) {
                return ((ArrayAttribute<?>) attribute).toArray();
            }
        }

        return null;
    }
    /**
     * Gets values as typed objects array.
     * @return array
     * @throws ClassCastException
     */
    @SuppressWarnings("unchecked")
    public<T> T[] toValues() {
        return (T[]) toValuesObjects();
    }
    /**
     * Gets values as objects array.
     * @return array
     */
    public Object[] toValuesObjects() {

        List<Object> objects = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {

            Attribute attribute = values.get(i);
            if (attribute.isEmpty()) {
                continue;
            }

            if (attribute.getAttributeType() == AttributeType.SIMPLE) {
                objects.add(((SimpleAttribute<?>) attribute).getValue());
            } else if (attribute.getAttributeType() == AttributeType.CODE) {
                objects.add(((CodeAttribute<?>) attribute).getValue());
            } else if (attribute.getAttributeType() == AttributeType.ARRAY) {
                objects.addAll(((ArrayAttribute<?>) attribute).toList());
            }
        }

        return objects.toArray();
    }
    /**
     * Tells whether this param holds no value.
     * @return true if so, false otherwise
     */
    public boolean isEmpty() {

        if (CollectionUtils.isEmpty(values)) {
            return true;
        }

        Iterator<Attribute> i = values.iterator();
        while (i.hasNext()) {
            Attribute attribute = i.next();
            if (Objects.nonNull(attribute) && !attribute.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
