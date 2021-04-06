package com.unidata.mdm.backend.service.data.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.data.export.xlsx.XLSXProcessor;

public class AttributeUtils {
    /**
     * The AttributeUtils log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributeUtils.class);

    private static final String TIME = "Время";

    private static final String DATE = "Дата";

    private static final String DATE_TIME = "Дата и время";

    /**
     * Array of available for system formaters
     */
    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_TIME,
            XLSXProcessor.EXCEL_DATE_FORMAT,
            XLSXProcessor.EXCEL_DATE_TIME_FORMAT,
            XLSXProcessor.EXCEL_TIME_FORMAT
    };

    /**
     * Sets the value to the simple attribute.
     *
     * @param valueAttr data simple attribute
     * @param value     the value to set
     */
    public static void processCodeAttributeValue(@Nullable CodeAttribute<?> valueAttr, @Nullable Object value) {

        if (Objects.isNull(valueAttr)) {
            return;
        }

        // Overwrite or set the value
        switch (valueAttr.getDataType()) {
        case INTEGER:
            valueAttr.castValue(toLong(value));
            return;
        case STRING:
            valueAttr.castValue(toString(value));
            return;
        default:
            return;
        }
    }

    /**
     * Sets the value to the simple attribute.
     *
     * @param valueAttr data simple attribute
     * @param value     the value to set
     */
    public static void processSimpleAttributeValue(@Nullable SimpleAttribute<?> valueAttr, @Nullable Object value) {

        if (Objects.isNull(valueAttr)) {
            return;
        }

        // Overwrite or set the value
        try{
            switch (valueAttr.getDataType()) {
            case BOOLEAN:
                valueAttr.castValue(toBoolean(value));
                return;
            case INTEGER:
                valueAttr.castValue(toLong(value));
                return;
            case NUMBER:
            case MEASURED:
                valueAttr.castValue(toDouble(value));
                return;
            case STRING:
            case ENUM:
                valueAttr.castValue(toString(value));
                return;
            case DATE:
                valueAttr.castValue(toDate(value));
                return;
            case TIME:
                valueAttr.castValue(toTime(value));
                return;
            case TIMESTAMP:
                valueAttr.castValue(toTimestamp(value));
                return;
            case BLOB:
            case CLOB:
                // TODO
                return;
            default:
                return;
            }
        } catch (Exception e){
            String message = "Unable to instantiate an array variable.Value[" + String.valueOf(value) + "]. AttrName ["
                    + valueAttr.getName() + "]. Type [" + valueAttr.getDataType() + "]";
            LOGGER.error(message, e);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_IMPORT_IMPOSSIBLE_CONVERT_TO_TYPE,
                    String.valueOf(value), valueAttr.getName(), valueAttr.getDataType());
        }
    }

    /**
     * Splits array values using specific separator.
     * @param value the value
     * @param exchangeSeparator the separator
     * @return array or null
     */
    public static String[] splitArrayValues(Object value, String exchangeSeparator) {

        if (Objects.isNull(value)) {
            return null;
        }

        return StringUtils.split(value.toString(), StringUtils.isBlank(exchangeSeparator) ? "|" : StringUtils.trim(exchangeSeparator));
    }

    /**
     * Joins array values using specific separator.
     * @param values the values
     * @param exchangeSeparator the separator
     * @return array or null
     */
    public static String joinArrayValues(Object[] values, String exchangeSeparator) {

        if (Objects.isNull(values)) {
            return null;
        }

        return StringUtils.join(Arrays.stream(values)
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(StringUtils::trim)
                    .filter(StringUtils::isNotBlank).toArray(),
                StringUtils.isBlank(exchangeSeparator) ? "|" : StringUtils.trim(exchangeSeparator));
    }
    /**
     * Sets the value to the simple attribute.
     *
     * @param valueAttr data simple attribute
     * @param value     the value to set
     */
    public static void processArrayAttributeValue(@Nullable ArrayAttribute<?> valueAttr, @Nullable Object value, String exchangeSeparator) {

        if (Objects.isNull(valueAttr)) {
            return;
        }

        List<?> result = null;
        String[] values = splitArrayValues(value, exchangeSeparator);
        if (Objects.nonNull(values)) {

            try {
                switch (valueAttr.getDataType()) {
                case INTEGER:
                    result = Arrays.stream(values).map(AttributeUtils::toLong).filter(Objects::nonNull).collect(Collectors.toList());
                    break;
                case NUMBER:
                    result = Arrays.stream(values).map(AttributeUtils::toDouble).filter(Objects::nonNull).collect(Collectors.toList());
                    break;
                case STRING:
                    result = Arrays.stream(values).map(AttributeUtils::toString).filter(Objects::nonNull).collect(Collectors.toList());
                    break;
                case DATE:
                    result = Arrays.stream(values).map(AttributeUtils::toDate).filter(Objects::nonNull).collect(Collectors.toList());
                    break;
                case TIME:
                    result = Arrays.stream(values).map(AttributeUtils::toTime).filter(Objects::nonNull).collect(Collectors.toList());
                    break;
                case TIMESTAMP:
                    result = Arrays.stream(values).map(AttributeUtils::toTimestamp).filter(Objects::nonNull).collect(Collectors.toList());
                    break;
                default:
                    break;
                }
            } catch (Exception e) {
                String message =
                        "Unable to instantiate an array variable.Value[" + String.valueOf(value) + "]. AttrName ["
                                + valueAttr.getName() + "]. Array type [" + valueAttr.getDataType() + "]";
                LOGGER.error(message, e);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_IMPORT_IMPOSSIBLE_CONVERT_TO_TYPE,
                        String.valueOf(value), valueAttr.getName(), "Массив " + valueAttr.getDataType());
            }
        }

        valueAttr.castValue(result);
    }

    /**
     * Object to boolean, if required.
     *
     * @param o object
     * @return the boolean
     */
    private static Boolean toBoolean(Object o) {
        return o == null ?
                Boolean.FALSE :
                Boolean.class.isAssignableFrom(o.getClass()) ? (Boolean) o : Boolean.valueOf(o.toString());
    }

    /**
     * Object to long, if required.
     *
     * @param o object
     * @return the long
     */
    private static Long toLong(Object o) {
        return o == null ?
                null :
                Number.class.isAssignableFrom(o.getClass()) ? ((Number) o).longValue() : Long.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     *
     * @param o object
     * @return the double
     */
    private static Double toDouble(Object o) {
        return o == null ?
                null :
                Number.class.isAssignableFrom(o.getClass()) ? ((Number) o).doubleValue() : Double.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     *
     * @param o object
     * @return the string
     */
    private static String toString(Object o) {
        return o == null ? null : String.class.isAssignableFrom(o.getClass()) ? (String) o : o.toString();
    }

    /**
     * Object to boolean, if required.
     *
     * @param o object
     * @return the XML gregorian calendar
     */
    private static LocalDate toDate(Object o) {
        if (o == null) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return new Date(((Date) o).getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            TemporalAccessor result = parseStringToDate(o.toString());
            if (result != null) {
                return result.query(LocalDate::from);
            } else {
                String message = "Cannot convert value [" + o.toString() + "] to LocalDate";
                LOGGER.error(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_IMPORT_INCORRECT_STRING_TIME_FORMAT,
                        TIME, o.toString());
            }
        } else {
            String message = "Cannot convert value of type [" + o.getClass().getName() + "] to LocalDate";
            LOGGER.error(message);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_IMPORT_UNSUPPORTED_TIME_FORMAT, TIME,
                    o.getClass().getName(), o.toString());
        }
    }

    /**
     * Object to boolean, if required.
     *
     * @param o object
     * @return the XML gregorian calendar
     */
    private static LocalTime toTime(Object o) {
        if (o == null) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return new Date(((Date) o).getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            TemporalAccessor result = parseStringToDate(o.toString());
            if (result != null) {
                return result.query(LocalTime::from);
            } else {
                String message = "Cannot convert value [" + o.toString() + "] to LocalTime";
                LOGGER.error(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_IMPORT_INCORRECT_STRING_TIME_FORMAT,
                        DATE, o.toString());
            }
        } else {
            String message = "Cannot convert value of type [" + o.getClass().getName() + "] to LocalTime";
            LOGGER.error(message);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_IMPORT_UNSUPPORTED_TIME_FORMAT, DATE,
                    o.getClass().getName(), o.toString());
        }
    }

    /**
     * Object to boolean, if required.
     *
     * @param o object
     * @return the XML gregorian calendar
     */
    private static LocalDateTime toTimestamp(Object o) {
        if (o == null) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return new Date(((Date) o).getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            TemporalAccessor result = parseStringToDate(o.toString());
            if (result != null) {
                return result.query(LocalDateTime::from);
            } else {
                String message = "Cannot convert value [" + o.toString() + "] to LocalDateTime";
                LOGGER.error(message);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_IMPORT_INCORRECT_STRING_TIME_FORMAT,
                        DATE_TIME, o.toString());
            }
        } else {
            String message = "Cannot convert value of type [" + o.getClass().getName() + "] to LocalDateTime";
            LOGGER.error(message);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_IMPORT_UNSUPPORTED_TIME_FORMAT, DATE_TIME,
                    o.getClass().getName(), o.toString());
        }
    }

    private static TemporalAccessor parseStringToDate(String date) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return formatter.parse(date);
            } catch (Exception e) {
                LOGGER.trace("Cannot format string [{}] to date using [{}]", date, formatter.toString());
            }
        }
        LOGGER.trace("Cannot format string [{}] to date ", date);
        return null;
    }
}
