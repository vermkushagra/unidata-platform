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

package com.unidata.mdm.backend.service.matching.algorithms;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.util.ByteUtils;
import com.unidata.mdm.backend.util.CryptUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class AbstractStrictAlgorithm extends AbstractAlgorithm {

    /**
     * Constructor.
     */
    public AbstractStrictAlgorithm() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExact() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object construct(Attribute attr) {

        if (Objects.nonNull(attr) && !attr.isEmpty()) {
            switch (attr.getAttributeType()) {
            case SIMPLE:
                return encodeSimpleAttribute((SimpleAttribute<?>) attr);
            case ARRAY:
                return encodeArrayAttribute((ArrayAttribute<?>) attr);
            default:
                break;
            }
        }

        return "empty";
    }

    /**
     * Encodes attribute value as string.
     * Attribute types BLOB, CLOB and LINK are not supported.
     *
     * @param attr simple attribute
     * @return strig
     */
    private String encodeSimpleAttribute(SimpleAttribute<?> attr) {

        Object val = attr.getValue();
        if (Objects.nonNull(val)) {

            switch (attr.getDataType()) {
            case BOOLEAN:
                return CryptUtils.toMurmurString((Boolean) val ? 1L : 0L);
            case DATE:
                return CryptUtils.toMurmurString(ByteUtils.packLocalDate((LocalDate) val));
            case TIME:
                return CryptUtils.toMurmurString(ByteUtils.packLocalTime((LocalTime) val));
            case TIMESTAMP:
                return CryptUtils.toMurmurString(ByteUtils.packLocalDateTime((LocalDateTime) val));
            case ENUM:
            case STRING:
                return CryptUtils.toMurmurString((String) val);
            case INTEGER:
                return CryptUtils.toMurmurString((Long) val);
            case NUMBER:
            case MEASURED:
                return CryptUtils.toMurmurString(Double.doubleToLongBits((Double) val));
            default:
                break;
            }
        }

        return null;
    }

    // TODO implement
    private String encodeArrayAttribute(ArrayAttribute<?> attr) {

        Object[] vals = attr.toArray();
        if (Objects.nonNull(vals)) {
            /*
            switch (attr.getDataType()) {
            case DATE:
                return CryptUtils.toMurmurString(ByteUtils.packLocalDate((LocalDate[]) vals));
            case TIME:
                return CryptUtils.toMurmurString(ByteUtils.packLocalTime((LocalTime) val));
            case TIMESTAMP:
                return CryptUtils.toMurmurString(ByteUtils.packLocalDateTime((LocalDateTime) val));
            case STRING:
                return CryptUtils.toMurmurString((String) val);
            case INTEGER:
                return CryptUtils.toMurmurString((Long) val);
            case NUMBER:
                return CryptUtils.toMurmurString(Double.doubleToLongBits((Double) val));
            default:
                break;
            }
            */
        }

        return null;
    }
}
