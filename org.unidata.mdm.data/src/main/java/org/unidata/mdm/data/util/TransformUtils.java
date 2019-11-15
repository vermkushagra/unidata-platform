package org.unidata.mdm.data.util;

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
import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.util.AttributeUtils;
import org.unidata.mdm.data.type.exchange.ExchangeField;
import org.unidata.mdm.data.type.exchange.db.DbExchangeField;
import org.unidata.mdm.system.util.ConvertUtils;

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
    public static Class<?> getFieldClass(ExchangeField ef, AttributeModelElement attr) {

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
    public static Class<?> getFieldClass(AttributeModelElement holder) {

        if (holder == null) {
            return null;
        }

        if (holder.isComplex()) {
            throw new IllegalArgumentException("Complex attribute for class field selected.");
        } else if (holder.isCode()) {

            switch (holder.getValueType()) {
            case INTEGER:
                return Long.class;
            case STRING:
            default:
                return String.class;
            }

        } else if (holder.isSimple()) {

            // Simple DT with a value type
            switch (holder.getValueType()) {
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

            switch (holder.getValueType()) {
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
    public static Object getFieldValue(Attribute attribute, ExchangeField ef, AttributeModelElement attr) {

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
    public static Object getFieldValue(Object initial, ExchangeField ef, AttributeModelElement holder) {

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
                String separator = StringUtils.defaultString(holder != null ? holder.getExchangeSeparator() : null, "|");
                return AttributeUtils.joinArrayValues((Object[]) initial, separator);
            }

            // No further modifications
            return initial;
        }

        // Class can not be detrmined
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot determine field/conversion type for ["
                    + (holder != null ? holder.getContainer().getName() + "." : "")
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
     * Date object to TimeMillis.
     *
     * @param o
     *            object
     * @return Long timeMillis
     */
    public static Long toTimeMillis(Object o) {

        if (o == null) {
            return null;
        } else if (o instanceof LocalDate) {
            return ConvertUtils.localDate2Date((LocalDate) o).getTime();
        } else if (o instanceof LocalDateTime) {
            return ConvertUtils.localDateTime2Date((LocalDateTime) o).getTime();
        } else if (o instanceof LocalTime) {
            return ConvertUtils.localTime2Date((LocalTime) o).getTime();
        } else if (o instanceof Calendar) {
            return ((Calendar) o).getTimeInMillis();
        } else if (o instanceof Date) {
            return ((Date) o).getTime();
        }

        LOGGER.warn("Cannot convert value of type [{}] to TimeMillis.", o.getClass().getName());
        return null;
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    public static java.sql.Date toSQLDate(Object o) {

        Long timeMillis = toTimeMillis(o);
        if (timeMillis != null) {
            return new java.sql.Date(timeMillis);
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
    public static Time toSQLTime(Object o) {

        if (o == null) {
            return null;
        } else if (o instanceof LocalDate) {
            return new Time(ConvertUtils.localDate2Date((LocalDate) o).getTime());
        } else if (o instanceof LocalDateTime) {
            return new Time(ConvertUtils.localDateTime2Date((LocalDateTime) o).getTime());
        } else if (o instanceof LocalTime) {
            return new Time(ConvertUtils.localTime2Date((LocalTime) o).getTime());
        } else if (o instanceof Calendar) {
            return new Time(((Calendar) o).getTimeInMillis());
        } else if (o instanceof Date) {
            return new Time(((Date) o).getTime());
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
    public static Timestamp toSQLTimestamp(Object o) {

        if (o == null) {
            return null;
        } else if (o instanceof LocalDate) {
            return new Timestamp(ConvertUtils.localDate2Date((LocalDate) o).getTime());
        } else if (o instanceof LocalDateTime) {
            return new Timestamp(ConvertUtils.localDateTime2Date((LocalDateTime) o).getTime());
        } else if (o instanceof LocalTime) {
            return new Timestamp(ConvertUtils.localTime2Date((LocalTime) o).getTime());
        } else if (o instanceof Calendar) {
            return new Timestamp(((Calendar) o).getTimeInMillis());
        } else if (o instanceof Date) {
            return new Timestamp(((Date) o).getTime());
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
