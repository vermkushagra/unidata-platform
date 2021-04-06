package com.unidata.mdm.backend.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Mikhail Mikhailov
 * Various conversions.
 */
public class ConvertUtils {

    /**
     * Start of epoch as local date.
     */
    private static final LocalDate START_OF_EPOCH = LocalDate.of(1970, 1, 1);

    /**
     * Constructor.
     */
    private ConvertUtils() {
        super();
    }
    /**
     * Date 2 LocalDate.
     * @param d the date
     * @return local date
     */
    public static LocalDate date2LocalDate(Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    /**
     * Date 2 LocalTime.
     * @param d the date
     * @return local time
     */
    public static LocalTime date2LocalTime(Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }
    /**
     * Date 2 LocalDateTime.
     * @param d the date
     * @return local date time
     */
    public static LocalDateTime date2LocalDateTime(Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    /**
     * LocalDate 2 Date.
     * @param d the local date
     * @return date
     */
    public static Date localDate2Date(LocalDate d) {
        return d == null ? null : Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    /**
     * LocalTime 2 Date.
     * @param d the local time
     * @return date
     */
    public static Date localTime2Date(LocalTime d) {
        return d == null ? null : Date.from(d.atDate(START_OF_EPOCH).atZone(ZoneId.systemDefault()).toInstant());
    }
    /**
     * LocalDateTime 2 Date.
     * @param d the local date time
     * @return date
     */
    public static Date localDateTime2Date(LocalDateTime d) {
        return d == null ? null : Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     *
     * @param z the zoned date time
     * @return date
     */
    public static Date zonedDateTime2Date(ZonedDateTime z){
        return z == null ? null : Date.from(z.toInstant());
    }
}
