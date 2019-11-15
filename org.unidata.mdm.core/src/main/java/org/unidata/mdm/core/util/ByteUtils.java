package org.unidata.mdm.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Mikhail Mikhailov
 * Stuff taken from java.nio.Bits.
 */
public final class ByteUtils {
    /**
     * Constructor.
     */
    private ByteUtils() {
        super();
    }

    /**
     * Append int to buffer.
     * @param buf the buffer to append to
     * @param data the data
     * @param offset the offset in buffer
     */
    public static void append(byte[] buf, int data, int offset) {
        buf[offset] = (byte) (data >> 24);
        buf[offset + 1] = (byte) (data >> 16);
        buf[offset + 2] = (byte) (data >> 8);
        buf[offset + 3] = (byte) (data);
    }

    /**
     * Pack to byte buffer.
     * @param values integers to pack
     * @return byte array
     */
    public static byte[] pack(int... values) {

        byte[] result = new byte[4 * values.length];
        for (int i = 0; i < values.length; i++) {
            append(result, values[i], i * 4);
        }

        return result;
    }

    /**
     * Pack to byte buffer.
     * @param values integers to pack
     * @return byte array
     */
    public static byte[] packLocalDate(LocalDate date) {

        // Year + 2 octets for month and day
        byte[] result = new byte[6];
        append(result, date.getYear(), 0);

        result[4] = (byte) date.getMonthValue();
        result[5] = (byte) date.getDayOfMonth();

        return result;
    }
    /**
     * Pack to byte buffer.
     * @param values integers to pack
     * @return byte array
     */
    public static byte[] packLocalTime(LocalTime time) {

        // 3 octets for hours, minutes, seconds + nano of seconds
        byte[] result = new byte[7];
        append(result, time.getNano(), 0);

        result[4] = (byte) time.getHour();
        result[5] = (byte) time.getMinute();
        result[6] = (byte) time.getSecond();

        return result;
    }
    /**
     * Pack to byte buffer.
     * @param values integers to pack
     * @return byte array
     */
    public static byte[] packLocalDateTime(LocalDateTime timestamp) {

        // Year + nano of seconds + 2 octets for month and day + 3 octets for hours, minutes, seconds
        byte[] result = new byte[13];
        append(result, timestamp.getYear(), 0);
        append(result, timestamp.getNano(), 4);

        result[8] = (byte) timestamp.getMonthValue();
        result[9] = (byte) timestamp.getDayOfMonth();
        result[10] = (byte) timestamp.getHour();
        result[11] = (byte) timestamp.getMinute();
        result[12] = (byte) timestamp.getSecond();

        return result;
    }
    /**
     * Extract integer from byte array.
     * @param buf the buffer
     * @param offset the offset
     * @return integer
     */
    public static int extract(byte[] buf, int offset) {
        // Provoke AIOOB, in case of wrong pointer arithmetic.
        return ((buf[offset] << 24) | ((buf[offset + 1]  & 0xff) << 16) | ((buf[offset + 2]  & 0xff) << 8) | (buf[offset + 3]  & 0xff));
    }

    /**
     * Unpack a numer of integers from byte array.
     * @param buf the buffer
     * @param count count of packed integers
     * @return integer array
     */
    public static int[] unpack(byte[] buf, int count) {

        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            result[i] = extract(buf, i * 4);
        }

        return result;
    }

    /**
     * Unpack a local date from byte array.
     * @param buf the buffer
     * @return integer array
     */
    public static LocalDate unpackLocalDate(byte[] buf) {

        int year = extract(buf, 0);
        int month = (buf[4] & 0xff);
        int day = (buf[5] & 0xff);

        return LocalDate.of(year, month, day);
    }
    /**
     * Unpack a local date from byte array.
     * @param buf the buffer
     * @return integer array
     */
    public static LocalTime unpackLocalTime(byte[] buf) {

        int nanos = extract(buf, 0);
        int hour = (buf[4] & 0xff);
        int minute = (buf[5] & 0xff);
        int second = (buf[6] & 0xff);

        return LocalTime.of(hour, minute, second, nanos);
    }
    /**
     * Unpack a local date from byte array.
     * @param buf the buffer
     * @return integer array
     */
    public static LocalDateTime unpackLocalDateTime(byte[] buf) {

        int year = extract(buf, 0);
        int nanos = extract(buf, 4);
        int month = (buf[8] & 0xff);
        int day = (buf[9] & 0xff);
        int hour = (buf[10] & 0xff);
        int minute = (buf[11] & 0xff);
        int second = (buf[12] & 0xff);

        return LocalDateTime.of(year, month, day, hour, minute, second, nanos);
    }
}
