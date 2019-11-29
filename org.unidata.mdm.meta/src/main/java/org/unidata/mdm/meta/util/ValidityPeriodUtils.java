package org.unidata.mdm.meta.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.unidata.mdm.core.util.CoreServiceUtils;
import org.unidata.mdm.core.util.DateUtils;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.DateGranularityMode;
import org.unidata.mdm.meta.PeriodBoundaryDef;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.util.ConvertUtils;

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

    /**
     * Globally defined validity period start.
     */
    private static Date globalValidityPeriodStart;
    /**
     * Globally defined validity period end.
     */
    private static Date globalValidityPeriodEnd;

    /**
     * Globally defined DateGranularityMode.
     */
    private static DateGranularityMode globalDateGranularityMode;
    /**
     * From comparator.
     */
    private static final Comparator<Date> FROM_COMPARATOR =
        (o1, o2) -> {

            if (o1 == null)
                return 1;

            if (o2 == null)
                return -1;

            return o2.compareTo(o1);
        };
    /**
     * To comparator.
     */
    private static final Comparator<Date> TO_COMPARATOR =
        (o1, o2) -> {

            if (o1 == null)
                return 1;

            if (o2 == null)
                return -1;

            return o1.compareTo(o2);
        };
    /**
     * Max period id.
     */
    public static final long TIMELINE_MAX_PERIOD_ID = 9223372036825200000L;

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

            globalValidityPeriodStart = extractDateBoundary("unidata.validity.period.start");
            globalValidityPeriodEnd = extractDateBoundary("unidata.validity.period.end");

            try {

                String modeAsString = CoreServiceUtils.configurationService()
                        .getSystemStringProperty("unidata.validity.period.mode");

                globalDateGranularityMode = StringUtils.isBlank(modeAsString)
                        ? DateGranularityMode.DATE
                        : DateGranularityMode.fromValue(modeAsString);

            }catch (Exception e){
                globalDateGranularityMode = DateGranularityMode.DATE;
                LOGGER.warn("GlobalDateGranularityMode initialization failed, used default(GlobalDateGranularityMode.DATA), exception caught.", e);
            }

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

        String validityPeriodBoundaryAsString = CoreServiceUtils.configurationService()
                .getSystemStringProperty(propertyName);

        try {
            LocalDateTime ldt = StringUtils.isBlank(validityPeriodBoundaryAsString)
                    ? null
                    : LocalDateTime.parse(validityPeriodBoundaryAsString, DateUtils.DEFAULT_FORMATTER_NO_OFFSET);
            if (ldt != null) {
                return ConvertUtils.localDateTime2Date(ldt);
            }

        } catch (DateTimeParseException e) {
            LOGGER.warn("Cannot parse validity period boundary {}.", propertyName, e);
        }

        return null;
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
						.withStart(MetaJaxbUtils.dateToXMGregorianCalendar(globalValidityPeriodStart))
						.withEnd(MetaJaxbUtils.dateToXMGregorianCalendar(globalValidityPeriodEnd)));

			} else {
				if (el.getValidityPeriod().getStart() == null) {
					el.getValidityPeriod().setStart(MetaJaxbUtils.dateToXMGregorianCalendar(globalValidityPeriodStart));
				}
				if (el.getValidityPeriod().getEnd() == null) {
					el.getValidityPeriod().setEnd(MetaJaxbUtils.dateToXMGregorianCalendar(globalValidityPeriodEnd));
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
    public static Date mostTo(Date d1, Date d2) {

        // 1. End of the timeline, positive infinity
        if (d1 == null || d2 == null) {
            return null;
        }
        return d1.after(d2) ? d1 : d2;
    }

    public static boolean isSamePoint(Date a, Date b) {

        if (a == null && b == null) {
            return true;
        } else if (a != null && b != null) {
            return a.getTime() == b.getTime();
        }

        return false;
    }

    public static Date normalizeFrom(Date from) {

        if (Objects.isNull(from)) {
            return null;
        }

        ZonedDateTime adj;
        if (from instanceof java.sql.Date) {
            LocalDate ld = ((java.sql.Date) from).toLocalDate();
            adj = ZonedDateTime.of(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth(),
                0, 0, 0, 0, ZoneId.systemDefault());
        } else if (from instanceof java.sql.Timestamp) {
            LocalDateTime ldt = ((java.sql.Timestamp) from).toLocalDateTime();
            adj = ZonedDateTime.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                    0, 0, 0, 0, ZoneId.systemDefault());
        } else {
            // This is probably wrong
            ZonedDateTime i = from.toInstant().atZone(ZoneId.systemDefault());
            adj = ZonedDateTime.of(i.getYear(), i.getMonthValue(), i.getDayOfMonth(),
                    0, 0, 0, 0, ZoneId.systemDefault());
        }

        return ConvertUtils.zonedDateTime2Date(adj);
    }

    public static Date normalizeTo(Date to) {

        if (Objects.isNull(to)) {
            return null;
        }

        ZonedDateTime adj;
        if (to instanceof java.sql.Date) {
            LocalDate ld = ((java.sql.Date) to).toLocalDate();
            adj = ZonedDateTime.of(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth(),
                    23, 59, 59, (int) TimeUnit.MILLISECONDS.toNanos(999), ZoneId.systemDefault());
        } else if (to instanceof java.sql.Timestamp) {
            LocalDateTime ldt = ((java.sql.Timestamp) to).toLocalDateTime();
            adj = ZonedDateTime.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                    23, 59, 59, (int) TimeUnit.MILLISECONDS.toNanos(999), ZoneId.systemDefault());
        } else {
            // This is probably wrong
            ZonedDateTime i = to.toInstant().atZone(ZoneId.systemDefault());
            adj = ZonedDateTime.of(i.getYear(), i.getMonthValue(), i.getDayOfMonth(),
                    23, 59, 59, (int) TimeUnit.MILLISECONDS.toNanos(999), ZoneId.systemDefault());
        }

        return ConvertUtils.zonedDateTime2Date(adj);
    }


    public static DateGranularityMode getGlobalDateGranularityMode() {
        return globalDateGranularityMode;
    }
}