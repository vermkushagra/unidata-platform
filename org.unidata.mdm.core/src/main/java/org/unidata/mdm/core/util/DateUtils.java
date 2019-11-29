package org.unidata.mdm.core.util;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.util.ConvertUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public final class DateUtils {
    private DateUtils() { }

    public static final DateTimeFormatter DEFAULT_FORMATTER_NO_OFFSET
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

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
            throw new PlatformFailureException("Incorrect date format found, unable to parse date string!",
                    CoreExceptionIds.EX_META_VALIDITY_PERIODS_CANNOT_PARSE_DATE, dateAsString);
        }
        return result;
    }
}
