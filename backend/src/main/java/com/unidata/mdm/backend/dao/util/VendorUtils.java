package com.unidata.mdm.backend.dao.util;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.WillClose;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.PGConnection;
import org.postgresql.copy.PGCopyOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.dao.util.pg.PgUtils;
import com.unidata.mdm.backend.dao.util.pg.VendorDataType;

/**
 * @author mikhail
 * DB vendor utils. Postgres centric so far.
 */
public class VendorUtils {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VendorUtils.class);
    /**
     * Positive infinity. Taken from PGStatement.
     */
    private static final long DATE_POSITIVE_INFINITY = 9223372036825200000L;
    /**
     * Negative infinity. Taken from PGStatement.
     */
    private static final long DATE_NEGATIVE_INFINITY = -9223372036832400000L;
    /**
     * Positive smaller infinity. Taken from PGStatement.
     */
    @SuppressWarnings("unused")
    private static final long DATE_POSITIVE_SMALLER_INFINITY = 185543533774800000L;
    /**
     * Negative smaller infinity. Taken from PGStatement.
     */
    @SuppressWarnings("unused")
    private static final long DATE_NEGATIVE_SMALLER_INFINITY = -185543533774800000L;
    /**
     * Constructor.
     */
    private VendorUtils() {
        super();
    }

    /**
     * Constructs text array from input.
     * @param elements the elements
     * @return array string
     */
    public static String textArray(Collection<? extends CharSequence> elements) {
        return "array[" + (CollectionUtils.isEmpty(elements)
                ? ""
                : String.join(",",
                        elements.stream()
                            .map(CharSequence::toString)
                            .map(str -> StringUtils.wrapIfMissing(str, "'"))
                            .collect(Collectors.toList())
                        )) + "]::text[]";
    }

    /**
     * Colaesce from date.
     * @param ts the time stamp
     * @return timestamp
     */
    public static Timestamp coalesceFrom(Date ts) {
        return ts != null ? new Timestamp(ts.getTime()) : new Timestamp(DATE_NEGATIVE_INFINITY);
    }
    /**
     * Colaesce to date.
     * @param ts the time stamp
     * @return timestamp
     */
    public static Timestamp coalesceTo(Date ts) {
        return ts != null ? new Timestamp(ts.getTime()) : new Timestamp(DATE_POSITIVE_INFINITY);
    }
    /**
     * Colaesce a date.
     * @param ts the time stamp
     * @return timestamp
     */
    public static Timestamp coalesce(Date ts) {
        return ts != null ? new Timestamp(ts.getTime()) : new Timestamp(System.currentTimeMillis());
    }
    /**
     * Appends row to bulk set.
     * @param stream the stream
     * @param types value types
     * @param values values
     */
    public static void bulkAppend(CopyDataOutputStream stream, VendorDataType[] types, Object[] values) {

        try {
            stream.writeShort(types.length);
            for (int i = 0; i < types.length; i++) {
                PgUtils.bulkAppend(stream, types[i], values[i]);
            }
        } catch(Exception e) {
            final String message = "Append to bulk set failed.";
            LOGGER.error(message, e);
            throw new DataProcessingException(message, e, ExceptionId.EX_DATA_APPEND_BATCH_SET_FAILED);
        }
    }
    /**
     * Starts a new bulk portion.
     * @param out the driver stream.
     * @return new data stream
     */
    public static CopyDataOutputStream bulkStart(Connection connection, String prolog) {

        PGConnection pgConnection = null;
        try {
            pgConnection = connection.unwrap(PGConnection.class);
        } catch (SQLException e) {
            final String message = "Cannot unwrap pooling connection to PGConnection.";
            LOGGER.error(message, e);
            throw new SystemRuntimeException(message, e, ExceptionId.EX_SYSTEM_CONNECTION_UNWRAP);
        }

        CopyDataOutputStream buffer = null;
        try {

            buffer = new CopyDataOutputStream(new CopyBufferedOutputStream(new PGCopyOutputStream(pgConnection, prolog, 8192)));

            // 11 bytes required header
            buffer.writeBytes("PGCOPY\n\377\r\n\0");
            // 32 bit integer indicating no OID
            buffer.writeInt(0);
            // 32 bit header extension area length
            buffer.writeInt(0);

        } catch(Exception e) {
            final String message = "Init bulk set failed.";
            LOGGER.error(message, e);
            throw new DataProcessingException(message, e, ExceptionId.EX_DATA_INIT_BATCH_SET_FAILED);
        }

        return buffer;
    }
    /**
     * Ends a bulk set.
     * @param buffer the buffer to finish.
     */
    public static void bulkFinish(final CopyDataOutputStream buffer) {

        try {
            buffer.writeShort(-1);
            buffer.flush();
        } catch(Exception e) {
            final String message = "Finish bulk set failed.";
            LOGGER.error(message, e);
            throw new DataProcessingException(message, e, ExceptionId.EX_DATA_FINISH_BATCH_SET_FAILED);
        }
    }
    /**
     * @author Mikhail Mikhailov
     * Want to close the underlaying stream upon finish.
     */
    public static class CopyDataOutputStream extends DataOutputStream {
        /**
         * {@inheritDoc}
         */
        public CopyDataOutputStream(@WillClose OutputStream out) {
            super(out);
        }
        /**
         * Gets the PG copy stream.
         * @return stream
         */
        public OutputStream getWriterStream() {
            return this.out;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
            try {
                this.out.close();
            } catch (Exception e) {
                final String message = "Cannot close the underlaying PG COPY output stream.";
                LOGGER.error(message, e);
                throw new DataProcessingException(message, e, ExceptionId.EX_DATA_COPY_STREAM_CLOSE_FAILED);
            }
        }
    }
    /**
     * @author Mikhail Mikhailov
     * Needs BOS to write signature.
     */
    private static class CopyBufferedOutputStream extends BufferedOutputStream {
        /**
         * Constructor.
         * @param out
         */
        public CopyBufferedOutputStream(@WillClose OutputStream out) {
            super(out, 8192);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws IOException {
            super.out.close();
        }
    }
}
