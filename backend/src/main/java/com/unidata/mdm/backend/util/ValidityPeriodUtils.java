/**
 *
 */
package com.unidata.mdm.backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.PeriodBoundaryDef;

/**
 * @author Mikhail Mikhailov
 * Validity period utils.
 */
public class ValidityPeriodUtils {

    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidityPeriodUtils.class);
    /**
     * Time stamp pattern (INPUT_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";)
     */
    private static final FastDateFormat DEFAULT_TIMESTAMP_NO_OFFSET
        = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final DateTimeFormatter DEFAULT_FORMATTER_NO_OFFSET
        = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Globally defined validity period start.
     */
    private static Date globalValidityPeriodStart;
    /**
     * Globally defined validity period end.
     */
    private static Date globalValidityPeriodEnd;
    /**
     * From comparator.
     */
    private static final Comparator<Date> FROM_COMPARATOR =
        (o1, o2) ->
            o1 == null
                ? 1
                : o2 == null
                    ? -1
                    : o2.compareTo(o1);
    /**
     * To comparator.
     */
    private static final Comparator<Date> TO_COMPARATOR =
        (Date o1, Date o2) ->
            o1 == null
                ? 1
                : o2 == null
                    ? -1
                    : o1.compareTo(o2);

    /**
     * Constructor.
     */
    private ValidityPeriodUtils() {
        super();
    }

    /**
     * Convenient init method.
     */
    public static void init(ApplicationContext ac) {
        try {

            globalValidityPeriodStart = extractDateBoundary(ConfigurationConstants.VALIDITY_PERIOD_START_PROPERTY);
            globalValidityPeriodEnd = extractDateBoundary(ConfigurationConstants.VALIDITY_PERIOD_END_PROPERTY);

        } catch (Exception exc) {
            LOGGER.warn("Initialization failed, exception caught.", exc);
        }
    }

    /**
     * Reads boundary date.
     * @param propertyName the property name
     * @return date or null
     */
    private static Date extractDateBoundary(String propertyName) {

        String validityPeriodBoundaryAsString = ConfigurationService.getSystemStringProperty(propertyName);
        try {
            LocalDateTime ldt = StringUtils.isBlank(validityPeriodBoundaryAsString)
                    ? null
                    : LocalDateTime.parse(validityPeriodBoundaryAsString, DEFAULT_FORMATTER_NO_OFFSET);
            if (ldt != null) {
                return ConvertUtils.localDateTime2Date(ldt);
            }

        } catch (DateTimeParseException e) {
            LOGGER.warn("Cannot parse validity period boundary {}.", propertyName, e);
        }

        return null;
    }


	/**
	 * Parses string representation of date according to date format from
	 * {@see DEFAULT_TIMESTAMP_NO_OFFSET}.
	 *
	 * @param dateAsString
	 *            string representation of date.
	 * @return parsed date.
	 */
	public static Date parse(String dateAsString) {

		Date result = null;
		try {
			LocalDateTime ldt = StringUtils.isBlank(dateAsString)
                    ? null
                    : LocalDateTime.parse(dateAsString, DEFAULT_FORMATTER_NO_OFFSET);
            if (ldt != null) {
                result = ConvertUtils.localDateTime2Date(ldt);
            }
		} catch (DateTimeParseException e) {
			throw new SystemRuntimeException("Incorrect date format found, unable to parse date string!",
					ExceptionId.EX_DATA_CANNOT_PARSE_DATE, dateAsString);
		}
		return result;
	}
	/**
	 * Converts date to string representation
	 * {@see DEFAULT_TIMESTAMP_NO_OFFSET}.
	 *
	 * @param date
	 *            date to convert.
	 * @return date as string.
	 */
	public static String asString(Date date) {
		return date != null ? ValidityPeriodUtils.DEFAULT_TIMESTAMP_NO_OFFSET.format(date) : null;
	}
    /**
     * @return the globalValidityPeriodStart
     */
    public static Date getGlobalValidityPeriodStart() {
        return globalValidityPeriodStart;
    }

    /**
     * @return the globalValidityPeriodEnd
     */
    public static Date getGlobalValidityPeriodEnd() {
        return globalValidityPeriodEnd;
    }

    /**
	 * Add default validity periods to entities and lookup entities if they are
	 * not specified.
	 *
	 * @param ctx
	 *            update model request context.
	 */
	public static void adjustTimeIntervals(UpdateModelRequestContext ctx) {
		adjustDefaultTimeIntervals(ctx.getEntityUpdate());
		adjustDefaultTimeIntervals(ctx.getLookupEntityUpdate());
	}

	/**
	 * Add default validity periods to entities if they are not specified.
	 *
	 * @param entities
	 *            list with entities.
	 */
	public static void adjustDefaultTimeIntervals(List<? extends AbstractEntityDef> entities) {

	    if (CollectionUtils.isEmpty(entities)) {
			return;
		}

		entities.stream().forEach(el -> {
			if (el.getValidityPeriod() == null) {
				el.setValidityPeriod(new PeriodBoundaryDef()
						.withStart(JaxbUtils.dateToXMGregorianCalendar(globalValidityPeriodStart))
						.withEnd(JaxbUtils.dateToXMGregorianCalendar(globalValidityPeriodEnd)));

			} else {
				if (el.getValidityPeriod().getStart() == null) {
					el.getValidityPeriod().setStart(JaxbUtils.dateToXMGregorianCalendar(globalValidityPeriodStart));
				}
				if (el.getValidityPeriod().getEnd() == null) {
					el.getValidityPeriod().setEnd(JaxbUtils.dateToXMGregorianCalendar(globalValidityPeriodEnd));
				}
			}
		});
	}

    /**
     * Returns least from date (or null for negative infinity).
     * @param fromDates the from dates
     * @return date
     */
    public static Date leastFrom(List<Date> fromDates) {

        if (fromDates != null && !fromDates.isEmpty()) {
            fromDates.sort(FROM_COMPARATOR);
            return fromDates.get(fromDates.size() - 1);
        }

        return null;
    }

    /**
     * Returns most to date (or null for positive infinity).
     * @param toDates the to dates
     * @return date
     */
    public static Date mostTo(List<Date> toDates) {

        if (toDates != null && !toDates.isEmpty()) {
            toDates.sort(TO_COMPARATOR);
            return toDates.get(toDates.size() - 1);
        }

        return null;
    }
    /**
     * The earliest date on the timeline.
     * @param d1 date one
     * @param d2 date two
     * @return the earliest date on the timeline
     */
    public static Date leastFrom(Date d1, Date d2) {

        // 1. Begin of the timeline, negative infinity
        if (d1 == null || d2 == null) {
            return null;
        }

        return d1.before(d2) ? d1 : d2;
    }
    /**
     * The latest date on the timeline.
     * @param d1 date one
     * @param d2 date two
     * @return the latest date on the timeline
     */
    public static Date mostTo(@Nonnull Date d1, @Nonnull Date d2) {

        // 1. End of the timeline, positive infinity
        if (d1 == null || d2 == null) {
            return null;
        }
        return d1.after(d2) ? d1 : d2;
    }
}