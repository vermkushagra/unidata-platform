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

package com.unidata.mdm.backend.dao.util.pg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Mikhail Mikhailov
 * Low level COPY stuff.
 */
public class PgUtils {
    /**
     * Constructor.
     */
    private PgUtils() {
        super();
    }
    /**
     * Vendor specific bulk append.
     * @param stream the stream to append to
     * @param type the vendor - specific type
     * @param obj the object
     * @throws IOException
     */
    public static void bulkAppend(DataOutputStream stream, VendorDataType type, Object obj) throws IOException {

        if (Objects.isNull(obj)) {
            stream.writeInt(-1);
            return;
        }

        switch (type) {
        case BOOLEAN:
            bulkAppendBoolean(stream, (Boolean) obj);
            break;
        case BYTEA:
            bulkAppendBytea(stream, (byte[]) obj);
            break;
        case CHAR:
        case TEXT:
            bulkAppendChars(stream, (String) obj);
            break;
        case DATE:
        case TIMESTAMP:
            bulkAppendDate(stream, (Date) obj);
            break;
        case DOUBLE:
            bulkAppendDouble(stream, (Double) obj);
            break;
        case REAL:
            bulkAppendFloat(stream, (Float) obj);
            break;
        case INET4:
            bulkAppendInet4Address(stream, (Inet4Address) obj);
            break;
        case INET6:
            bulkAppendInet6Address(stream, (Inet6Address) obj);
            break;
        case INT2:
            bulkAppendShort(stream, (Short) obj);
            break;
        case INT4:
            bulkAppendInteger(stream, (Integer) obj);
            break;
        case INT8:
            bulkAppendLong(stream, (Long) obj);
            break;
        case JSONB:
            bulkAppendJsonb(stream, (String) obj);
            break;
        // case MAC_ADDRESS:
        //    break;
        // case MONEY:
        //    break;
        // case UNKNOWN:
        //    break;
        // case CASH:
        //    break;
        // case CIDR:
        //    break;
        case UUID:
            bulkAppendUUID(stream, (UUID) obj);
            break;
        default:
            break;
        }
    }
    /**
     * Writes boolean value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendBoolean(DataOutputStream stream, Boolean value) throws IOException {
        stream.writeInt(1);
        if (value) {
            stream.writeByte(1);
        } else {
            stream.writeByte(0);
        }
    }
    /**
     * Writes byte array value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendBytea(DataOutputStream stream, byte[] value) throws IOException {
        stream.writeInt(value.length);
        stream.write(value);
    }
    /**
     * Writes character value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendChars(DataOutputStream stream, String value) throws IOException {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        stream.writeInt(bytes.length);
        stream.write(bytes);
    }
    /**
     * Writes JSONB value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendJsonb(DataOutputStream stream, String value) throws IOException {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        stream.writeInt(bytes.length + 1);
        stream.writeByte(1); // JSONB protocol version
        stream.write(bytes);
    }
    /**
     * Writes short range integer value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendShort(DataOutputStream stream, Short value) throws IOException {
        stream.writeInt(2);
        stream.writeShort(value);
    }
    /**
     * Writes int value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendInteger(DataOutputStream stream, Integer value) throws IOException {
        stream.writeInt(4);
        stream.writeInt(value);
    }
    /**
     * Writes long value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendLong(DataOutputStream stream, Long value) throws IOException {
        stream.writeInt(8);
        stream.writeLong(value);
    }
    /**
     * Writes FP4 value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendFloat(DataOutputStream stream, Float value) throws IOException {
        stream.writeInt(4);
        stream.writeFloat(value);
    }
    /**
     * Writes FP8 value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendDouble(DataOutputStream stream, Double value) throws IOException {
        stream.writeInt(8);
        stream.writeDouble(value);
    }
    /**
     * Writes INET4 address value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendInet4Address(DataOutputStream stream, Inet4Address value) throws IOException {
        stream.writeInt(8);
        stream.writeByte(2); // INET4
        stream.writeByte(32); // number of bits in the mask
        stream.writeByte(0); // Is CIDR or not? 0 - no

        byte[] inet4AddressBytes = value.getAddress();

        stream.writeByte(inet4AddressBytes.length);
        stream.write(inet4AddressBytes);
    }
    /**
     * Writes INET4 address value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendInet6Address(DataOutputStream stream, Inet6Address value) throws IOException {
        stream.writeInt(20);
        stream.writeByte(3); // INET6
        stream.writeByte(128); // number of bits in the mask
        stream.writeByte(0); // Is CIDR or not? 0 - no

        byte[] inet6AddressBytes = value.getAddress();

        stream.writeByte(inet6AddressBytes.length);
        stream.write(inet6AddressBytes);
    }
    /**
     * Writes date value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendDate(DataOutputStream stream, Date value) throws IOException {

        long millis = value.getTime();

        // adjust time zone offset
        // millis += theTime.getZone().getOffset(millis);

        // pg time 0 is 2000-01-01 00:00:00
        long secs = TimeUnit.MILLISECONDS.toSeconds(millis);

        // java epoc to postgres epoc
        secs -= 946684800L;

        // Julian/Greagorian calendar cutoff point
        if (secs < -13165977600L) { // October 15, 1582 -> October 4, 1582
            secs -= 86400 * 10;
            if (secs < -15773356800L) { // 1500-03-01 -> 1500-02-28
                int years = (int) ((secs + 15773356800L) / -3155823050L);
                years++;
                years -= years / 4;
                secs += years * 86400;
            }
        }

        stream.writeInt(8);
        stream.writeLong(TimeUnit.SECONDS.toMicros(secs));
    }
    /**
     * Writes UUID value.
     * @param stream the stream
     * @param value the value to write
     * @throws IOException
     */
    private static void bulkAppendUUID(DataOutputStream stream, UUID value) throws IOException {

        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(value.getMostSignificantBits());
        bb.putLong(value.getLeastSignificantBits());

        stream.writeInt(16);
        stream.writeInt(bb.getInt(0));
        stream.writeShort(bb.getShort(4));
        stream.writeShort(bb.getShort(6));
        stream.write(Arrays.copyOfRange(bb.array(), 8, 16));
    }
}
