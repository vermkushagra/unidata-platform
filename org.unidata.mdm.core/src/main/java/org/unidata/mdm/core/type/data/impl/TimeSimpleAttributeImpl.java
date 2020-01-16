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

package org.unidata.mdm.core.type.data.impl;

import java.time.LocalTime;

import org.unidata.mdm.system.util.ConvertUtils;

/**
 * @author Mikhail Mikhailov
 * Time value.
 */
public class TimeSimpleAttributeImpl extends AbstractSimpleAttribute<LocalTime> {

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected TimeSimpleAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public TimeSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name the name
     * @param value the value
     */
    public TimeSimpleAttributeImpl(String name, LocalTime value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.TIME;
    }

    /**
     * Fluent part for compatibility.
     * @param value the value
     * @return self
     */
    public TimeSimpleAttributeImpl withValue(LocalTime value) {
        setValue(value);
        return this;
    }
    // TODO Move this code and deps for this OUT OF COMMON.
    @Override
    @SuppressWarnings("unchecked")
    public <V> V narrow(NarrowType type) {
        if (type == NarrowType.ES) {
            return (V) ConvertUtils.localTime2Date(getValue());
        }
        return super.narrow(type);
    }
}
