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

import java.util.List;

import org.unidata.mdm.core.type.data.ArrayValue;

/**
 * @author Mikhail Mikhailov
 * Array of long integer numbers.
 */
public class IntegerArrayAttributeImpl extends AbstractArrayAttribute<Long> {
    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected IntegerArrayAttributeImpl() {
        super();
    }
    /**
     * Constructor.
     * @param name
     */
    public IntegerArrayAttributeImpl(String name) {
        super(name);
    }

    /**
     * Constructor.
     * @param name
     * @param value
     */
    public IntegerArrayAttributeImpl(String name, List<ArrayValue<Long>> value) {
        super(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataType getDataType() {
        return ArrayDataType.INTEGER;
    }
}
