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

import java.util.Objects;

import org.unidata.mdm.core.type.data.CharacterLargeValue;

/**
 * @author Mikhail Mikhailov
 *  CLOB simple attribute.
 */
public class ClobSimpleAttributeImpl extends AbstractSimpleAttribute<CharacterLargeValue> {

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected ClobSimpleAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public ClobSimpleAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name
     * @param value
     */
    public ClobSimpleAttributeImpl(String name, CharacterLargeValue value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return DataType.CLOB;
    }

    /**
     * Fluent part for compatibility.
     * @param value the value
     * @return self
     */
    public ClobSimpleAttributeImpl withValue(CharacterLargeValue value) {
        setValue(value);
        return this;
    }

    /**
     * @return hash code
     */
    @Override public int hashCode() {
        CharacterLargeValue cv = getValue();
        return Objects.hash(DataType.CLOB,
                cv != null ? cv.getFileName() : null,
                cv != null ? cv.getSize() : null,
                cv != null ? cv.getMimeType() : null,
                cv != null ? cv.getId() : null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V narrow(NarrowType type) {
        if (type == NarrowType.ES) {
            return getValue() == null ? null : (V) getValue().getFileName();
        }
        return super.narrow(type);
    }
}
