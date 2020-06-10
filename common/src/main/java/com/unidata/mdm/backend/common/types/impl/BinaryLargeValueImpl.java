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

package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.types.BinaryLargeValue;

/**
 * @author Mikhail Mikhailov
 * Binary large value.
 */
public class BinaryLargeValueImpl extends AbstractLargeValue implements BinaryLargeValue {
    /**
     * Constructor.
     */
    public BinaryLargeValueImpl() {
        super();
    }

    /**
     * Sets data value.
     * @param value the value
     * @return self
     */
    public BinaryLargeValueImpl withData(byte[] value) {
        setData(value);
        return this;
    }

    /**
     * Sets id.
     * @param value the id
     * @return self
     */
    public BinaryLargeValueImpl withId(String value) {
        setId(value);
        return this;
    }

    /**
     * Sets file name.
     * @param value the file name
     * @return self
     */
    public BinaryLargeValueImpl withFileName(String value) {
        setFileName(value);
        return this;
    }

    /**
     * Seats mime type.
     * @param value the mime type
     * @return self
     */
    public BinaryLargeValueImpl withMimeType(String value) {
        setMimeType(value);
        return this;
    }

    /**
     * Sets size.
     * @param value the size
     * @return self
     */
    public BinaryLargeValueImpl withSize(long value) {
        setSize(value);
        return this;
    }

}
