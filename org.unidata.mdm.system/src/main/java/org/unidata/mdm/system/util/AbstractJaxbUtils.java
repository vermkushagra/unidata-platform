package org.unidata.mdm.system.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.exception.SystemExceptionIds;

/**
 * @author Mikhail Mikhailov on Oct 6, 2019
 * JAXB boilerplate routines.
 */
public abstract class AbstractJaxbUtils {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJaxbUtils.class);
    /**
     * Datatype factory, which is not guaranteed to be thread safe
     * (implementation specific).
     */
    private static ThreadLocal<DatatypeFactory> DATATYPE_FACTORY = ThreadLocal.withInitial(() -> {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            final String message = "DatatypeFactory initialization failure. [{}]";
            LOGGER.error(message, e);
            throw new PlatformFailureException(message, SystemExceptionIds.EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE, e);
        }
    });
    /**
     * Constructor.
     */
    protected AbstractJaxbUtils() {
        super();
    }
    /**
     * Gets a data type factory.
     *
     * @return data type factory
     */
    public static DatatypeFactory getDatatypeFactory() {
        return DATATYPE_FACTORY.get();
    }
    /**
     * Creates an {@link XMLGregorianCalendar} instance from string value. The
     * string must be in internal format already, which is ISO 8601 (transformer
     * applied).
     *
     * @param value the value
     * @return calendar
     */
    public static XMLGregorianCalendar ISO8601StringToXMGregorianCalendar(String value) {
        return getDatatypeFactory().newXMLGregorianCalendar(value);
    }

    /**
     * Creates an {@link XMLGregorianCalendar} instance from a {@link Date}
     * value.
     *
     * @param value the value
     * @return calendar
     */
    public static XMLGregorianCalendar dateToXMGregorianCalendar(Object value) {
        XMLGregorianCalendar result = null;
        if (value != null && value instanceof Date) {
            //FIXME: Set server default timezone
            Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
            calendar.setTime((Date) value);
            //calendar.setTimeZone();
            result = calendarToXMGregorianCalendar(calendar);
        }

        return result;
    }

    /**
     * Creates an {@link XMLGregorianCalendar} instance from a {@link Calendar}
     * value.
     *
     * @param value the value
     * @return calendar
     */
    public static XMLGregorianCalendar calendarToXMGregorianCalendar(Object value) {
        XMLGregorianCalendar result = null;
        if (value instanceof GregorianCalendar) {
            result = getDatatypeFactory().newXMLGregorianCalendar((GregorianCalendar) value);
        }
        return result;
    }

    /**
     * Creates an {@link XMLGregorianCalendar} instance from a {@link Date}
     * value.
     *
     * @param value the value
     * @return calendar
     */
    public static XMLGregorianCalendar localDateValueToXMGregorianCalendar(Object value) {
        XMLGregorianCalendar result = null;
        if (value instanceof Date) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime((Date) value);
            result = getDatatypeFactory()
                    .newXMLGregorianCalendarDate(
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            DatatypeConstants.FIELD_UNDEFINED);
        }

        return result;
    }

    /**
     * Creates an {@link XMLGregorianCalendar} instance from a {@link Date}
     * value.
     *
     * @param value the value
     * @return calendar
     */
    public static XMLGregorianCalendar localTimeValueToXMGregorianCalendar(Object value) {
        XMLGregorianCalendar result = null;
        if (value instanceof Date) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime((Date) value);
            result = getDatatypeFactory()
                    .newXMLGregorianCalendarTime(
                            calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND),
                            DatatypeConstants.FIELD_UNDEFINED);
        }

        return result;
    }

    /**
     * Creates an {@link XMLGregorianCalendar} instance from a {@link Date}
     * value.
     *
     * @param value the value
     * @return calendar
     */
    public static XMLGregorianCalendar localTimestampValueToXMGregorianCalendar(Object value) {
        XMLGregorianCalendar result = null;
        if (value instanceof Date) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime((Date) value);
            result = getDatatypeFactory()
                    .newXMLGregorianCalendar(
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND),
                            DatatypeConstants.FIELD_UNDEFINED);
        }

        return result;
    }

    /**
     * Gets {@link Calendar} from {@link XMLGregorianCalendar}.
     *
     * @param xmlCalendar XML calendar
     * @return util calendar
     */
    public static Calendar xmlGregorianCalendarToCalendar(XMLGregorianCalendar xmlCalendar) {
        Calendar result = null;
        if (xmlCalendar != null) {
            result = xmlCalendar.toGregorianCalendar(TimeZone.getDefault(), null, null);
        }
        return result;
    }

    /**
     * Gets {@link Date} from {@link XMLGregorianCalendar}.
     *
     * @param xmlCalendar XML calendar
     * @return util date
     */
    public static Date xmlGregorianCalendarToDate(XMLGregorianCalendar xmlCalendar) {
        Calendar calendar = xmlGregorianCalendarToCalendar(xmlCalendar);
        return calendar != null ? calendar.getTime() : null;
    }
}
