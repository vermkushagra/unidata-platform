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

package com.unidata.mdm.backend.exchange.util;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.service.data.util.AttributeUtils;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 * Various transform/exchange related operations.
 */
public class TransformUtils {
    /**
     * The Constant log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformUtils.class);

    /**
     * Constructor.
     */
    private TransformUtils() {
        super();
    }

    /**
     * Gets default field class.
     *
     * @param holder
     *            the holder
     * @return class or null
     */
    public static Class<?> getFieldClass(ExchangeField ef, AttributeInfoHolder attr) {

        Class<?> typeClazz;
        if (ef instanceof DbExchangeField) {
            typeClazz = ((DbExchangeField) ef).getTypeClazz();
            if (typeClazz != null) {
                return typeClazz;
            }
        }

        return getFieldClass(attr);
    }

    /**
     * Gets default field class.
     *
     * @param holder
     *            the holder
     * @return class or null
     */
    public static Class<?> getFieldClass(AttributeInfoHolder holder) {

        if (holder == null) {
            return null;
        }

        if (holder.isComplex()) {
            throw new IllegalArgumentException("Complex attribute for class field selected.");
        } else if (holder.isCode()) {

            CodeAttributeDef ca = holder.narrow();
            switch (ca.getSimpleDataType()) {
            case INTEGER:
                return Long.class;
            case STRING:
            default:
                return String.class;
            }

        } else if (holder.isSimple()) {

            SimpleAttributeDef sa = holder.narrow();
            SimpleDataType type = holder.isLookupLink()
                ? sa.getLookupEntityCodeAttributeType()
                : (holder.isEnumValue() || holder.isLinkTemplate())
                    ? SimpleDataType.STRING
                    : sa.getSimpleDataType();

            // Simple DT with a value type
            switch (type) {
                case BOOLEAN:
                    return Boolean.class;
                case STRING:
                    return String.class;
                case DATE:
                    return java.sql.Date.class;
                case TIME:
                    return Time.class;
                case TIMESTAMP:
                    return Timestamp.class;
                case INTEGER:
                    return Long.class;
                case NUMBER:
                    return Double.class;
                case MEASURED:
                    return Double.class;

                default:
                    break;
            }
        } else if (holder.isArray()) {

            ArrayAttributeDef aa = holder.narrow();
            ArrayValueType type = holder.isLookupLink()
                ? aa.getLookupEntityCodeAttributeType()
                : aa.getArrayValueType();

            switch (type) {
            case STRING:
                return String.class;
            case DATE:
                return java.sql.Date.class;
            case TIME:
                return Time.class;
            case TIMESTAMP:
                return Timestamp.class;
            case INTEGER:
                return Long.class;
            case NUMBER:
                return Double.class;
            default:
                break;
            }

        }

        return null;
    }

    /**
     * Gets field simple value.
     *
     * @param fields
     *            - result set.
     * @param alias
     *            - name of table column.
     * @param clazz
     *            - class of a field.
     * @return field value
     * @throws SQLException
     *             the SQL exception
     */
    public static Object getFieldValue(Attribute attribute, ExchangeField ef, AttributeInfoHolder attr) {

        if(attribute == null){
            return null;
        }

        return getFieldValue(extractFieldValue(attribute), ef, attr);
    }

    /**
     * Gets field simple value.
     *
     * @param fields
     *            - result set.
     * @param alias
     *            - name of table column.
     * @param clazz
     *            - class of a field.
     * @return field value
     * @throws SQLException
     *             the SQL exception
     */
    public static Object getFieldValue(Object initial, ExchangeField ef, AttributeInfoHolder holder) {

        if (initial == null) {
            return null;
        }

        Class<?> clazz = getFieldClass(ef, holder);

        // Direct value string
        if (ef.getValue() != null && clazz == null) {
            clazz = String.class;
        }

        // Array
        if (initial instanceof Object[]) {

            // Join with separator
            // TODO NOT FINISHED!!! ARRAYS NOT FUNCTIONING!!!
            if (clazz != Array.class) {
                ArrayAttributeDef aad = holder != null ? holder.narrow() : null;
                String separator = StringUtils.defaultString(aad != null ? aad.getExchangeSeparator() : null, "|");
                return AttributeUtils.joinArrayValues((Object[]) initial, separator);
            }

            // No further modifications
            return initial;
        }

        // Class can not be detrmined
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot determine field/conversion type for ["
                    + (holder != null ? holder.getEntity().getName() + "." : "")
                    + ef.getName() + "] field.");
        }

        if (clazz == Integer.class) {
            return toInt(initial);
        } else if (clazz == Long.class) {
            return toLong(initial);
        } else if (clazz == String.class) {
            return toString(initial);
        } else if (clazz == java.sql.Date.class) {
            return toSQLDate(initial);
        } else if (clazz == Timestamp.class) {
            return toSQLTimestamp(initial);
        } else if (clazz == Time.class) {
            return toSQLTime(initial);
        } else if (clazz == Float.class) {
            return toFloat(initial);
        } else if (clazz == Double.class) {
            return toDouble(initial);
        } else if (clazz == Boolean.class) {
            return toBoolean(initial);
        }

        // TODO handle array
        LOGGER.warn("Not supported type class conversion requested for: {}", clazz.getName());
        return null;
    }
    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the boolean
     */
    public static Boolean toBoolean(Object o) {
        return o == null
                ? Boolean.FALSE
                : Boolean.class.isAssignableFrom(o.getClass())
                ? (Boolean) o
                : Boolean.valueOf(o.toString());
    }

    /**
     * Object to long, if required.
     *
     * @param o
     *            object
     * @return the long
     */
    public static Long toLong(Object o) {
        return o == null
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                    ? ((Number) o).longValue()
                    : o instanceof Boolean
                        ? ((Boolean) o).booleanValue() ? 1L : 0L
                        : Long.valueOf(o.toString());
    }

    /**
     * Object to int, if required.
     *
     * @param o
     *            object
     * @return the long
     */
    public static Integer toInt(Object o) {
        return o == null
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                    ? ((Number) o).intValue()
                    : o instanceof Boolean
                        ? ((Boolean) o).booleanValue() ? 1 : 0
                        : Integer.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the double
     */
    public static Float toFloat(Object o) {
        return o == null
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                ? ((Number) o).floatValue()
                : Float.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the double
     */
    public static Double toDouble(Object o) {
        return o == null
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                ? ((Number) o).doubleValue()
                : Double.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the string
     */
    public static String toString(Object o) {
        return o == null
                ? null
                : String.class.isAssignableFrom(o.getClass())
                ? (String) o
                : o.toString();
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    public static java.sql.Date toSQLDate(Object o) {

        if (o == null) {
            return null;
        } else if (o instanceof LocalDate) {
            return new java.sql.Date(ConvertUtils.localDate2Date((LocalDate) o).getTime());
        } else if (o instanceof LocalDateTime) {
            return new java.sql.Date(ConvertUtils.localDateTime2Date((LocalDateTime) o).getTime());
        } else if (o instanceof LocalTime) {
            return new java.sql.Date(ConvertUtils.localTime2Date((LocalTime) o).getTime());
        } else if (o instanceof Calendar) {
            return new java.sql.Date(((Calendar) o).getTimeInMillis());
        } else if (o instanceof Date) {
            return new java.sql.Date(((Date) o).getTime());
        }

        LOGGER.warn("Cannot convert value of type [{}] to SQL Date.", o.getClass().getName());
        return null;
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    public static java.sql.Time toSQLTime(Object o) {

        if (o == null) {
            return null;
        } else if (o instanceof LocalDate) {
            return new java.sql.Time(ConvertUtils.localDate2Date((LocalDate) o).getTime());
        } else if (o instanceof LocalDateTime) {
            return new java.sql.Time(ConvertUtils.localDateTime2Date((LocalDateTime) o).getTime());
        } else if (o instanceof LocalTime) {
            return new java.sql.Time(ConvertUtils.localTime2Date((LocalTime) o).getTime());
        } else if (o instanceof Calendar) {
            return new java.sql.Time(((Calendar) o).getTimeInMillis());
        } else if (o instanceof Date) {
            return new java.sql.Time(((Date) o).getTime());
        }

        LOGGER.warn("Cannot convert value of type [{}] to SQL Time.", o.getClass().getName());
        return null;
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    public static java.sql.Timestamp toSQLTimestamp(Object o) {

        if (o == null) {
            return null;
        } else if (o instanceof LocalDate) {
            return new java.sql.Timestamp(ConvertUtils.localDate2Date((LocalDate) o).getTime());
        } else if (o instanceof LocalDateTime) {
            return new java.sql.Timestamp(ConvertUtils.localDateTime2Date((LocalDateTime) o).getTime());
        } else if (o instanceof LocalTime) {
            return new java.sql.Timestamp(ConvertUtils.localTime2Date((LocalTime) o).getTime());
        } else if (o instanceof Calendar) {
            return new java.sql.Timestamp(((Calendar) o).getTimeInMillis());
        } else if (o instanceof Date) {
            return new java.sql.Timestamp(((Date) o).getTime());
        }

        LOGGER.warn("Cannot convert value of type [{}] to SQL Timestamp.", o.getClass().getName());
        return null;
    }

    /**
     * Extracts value.
     * @param attribute the attribute
     * @return value
     */
    public static Object extractFieldValue(Attribute attribute) {

        switch (attribute.getAttributeType()) {
        case SIMPLE:
            SimpleAttribute<?> sa = attribute.narrow();
            return sa.getValue();
        case CODE:
            CodeAttribute<?> ca = attribute.narrow();
            return ca.getValue();
        case ARRAY:
            ArrayAttribute<?> aa = attribute.narrow();
            return aa.toArray();
        default:
            break;
        }

        return null;
    }
}
