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

package com.unidata.mdm.backend.util.interceptor;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedWriter;
import org.apache.cxf.io.DelegatingInputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Aleksandr Magdenko
 */
public class UnitdataLoggingInInterceptor extends AbstractLoggingInterceptor {
    private static final Logger LOG = LogUtils.getLogger(UnitdataLoggingInInterceptor.class);

    private static final String BINARY_CONTENT_PAYLOAD = "Binary data! Mime-type: %s, length: %d bytes";

    private static final List<String> BINARY_CONTENT_MEDIA_TYPES;

    private static final String PASSWORD_STR_REGEXP = "\"password\":\\s*\"([^=\"]*)\"";
    private static final Pattern PASSWORD_PAYLOAD_PATTERN = Pattern.compile(PASSWORD_STR_REGEXP);

    private static final String USER_NAME_STR_REGEXP = "\"userName\":\\s*\"([^=\"]*)\"";
    private static final Pattern USER_NAME_PAYLOAD_PATTERN = Pattern.compile(USER_NAME_STR_REGEXP);

    static {
        BINARY_CONTENT_MEDIA_TYPES = new ArrayList<>();
        BINARY_CONTENT_MEDIA_TYPES.add("application/zip");
        BINARY_CONTENT_MEDIA_TYPES.add("application/gzip");
        BINARY_CONTENT_MEDIA_TYPES.add("application/x-zip-compressed");
        BINARY_CONTENT_MEDIA_TYPES.add("application/pdf");
        BINARY_CONTENT_MEDIA_TYPES.add("application/msword");
        BINARY_CONTENT_MEDIA_TYPES.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        BINARY_CONTENT_MEDIA_TYPES.add("application/vnd.ms-excel");
        BINARY_CONTENT_MEDIA_TYPES.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public UnitdataLoggingInInterceptor() {
        super(Phase.RECEIVE);
    }

    public UnitdataLoggingInInterceptor(String phase) {
        super(phase);
    }

    public UnitdataLoggingInInterceptor(String id, String phase) {
        super(id, phase);
    }

    public UnitdataLoggingInInterceptor(int lim) {
        this();
        limit = lim;
    }
    public UnitdataLoggingInInterceptor(String id, int lim) {
        this(id, Phase.RECEIVE);
        limit = lim;
    }

    public UnitdataLoggingInInterceptor(PrintWriter w) {
        this();
        this.writer = w;
    }
    public UnitdataLoggingInInterceptor(String id, PrintWriter w) {
        this(id, Phase.RECEIVE);
        this.writer = w;
    }

    public void handleMessage(Message message) throws Fault {
        Logger logger = getMessageLogger(message);
        if (writer != null || logger.isLoggable(Level.INFO)) {
            logging(logger, message);
        }
    }

    private Logger getMessageLogger(Message message) {
        Endpoint ep = message.getExchange().getEndpoint();
        if (ep == null || ep.getEndpointInfo() == null) {
            return getLogger();
        }
        EndpointInfo endpoint = ep.getEndpointInfo();
        if (endpoint.getService() == null) {
            return getLogger();
        }
        Logger logger = endpoint.getProperty("MessageLogger", Logger.class);
        if (logger == null) {
            String serviceName = endpoint.getService().getName().getLocalPart();
            InterfaceInfo iface = endpoint.getService().getInterface();
            String portName = endpoint.getName().getLocalPart();
            String portTypeName = iface.getName().getLocalPart();
            String logName = "org.apache.cxf.services." + serviceName + "."
                + portName + "." + portTypeName;
            logger = LogUtils.getL7dLogger(this.getClass(), null, logName);
            endpoint.setProperty("MessageLogger", logger);
        }
        return logger;
    }

    protected void logging(Logger logger, Message message) throws Fault {
        if (message.containsKey(LoggingMessage.ID_KEY)) {
            return;
        }
        String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }
        message.put(LoggingMessage.ID_KEY, id);
        final LoggingMessage buffer
            = new LoggingMessage("Inbound Message\n----------------------------", id);

        if (!Boolean.TRUE.equals(message.get(Message.DECOUPLED_CHANNEL_MESSAGE))) {
            // avoid logging the default responseCode 200 for the decoupled responses
            Integer responseCode = (Integer)message.get(Message.RESPONSE_CODE);
            if (responseCode != null) {
                buffer.getResponseCode().append(responseCode);
            }
        }

        String encoding = (String)message.get(Message.ENCODING);

        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }
        String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }
        String ct = (String)message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);

        if (headers != null) {
            buffer.getHeader().append(headers);
        }
        String uri = (String)message.get(Message.REQUEST_URL);
        if (uri == null) {
            String address = (String)message.get(Message.ENDPOINT_ADDRESS);
            uri = (String)message.get(Message.REQUEST_URI);
            if (uri != null && uri.startsWith("/")) {
                if (address != null && !address.startsWith(uri)) {
                    uri = address + uri;
                }
            } else {
                uri = address;
            }
        }
        if (uri != null) {
            buffer.getAddress().append(uri);
            String query = (String)message.get(Message.QUERY_STRING);
            if (query != null) {
                buffer.getAddress().append("?").append(query);
            }
        }

        InputStream is = message.getContent(InputStream.class);
        if (is != null) {
            logInputStream(message, is, buffer, encoding, ct);
        } else {
            Reader reader = message.getContent(Reader.class);
            if (reader != null) {
                logReader(message, reader, buffer, ct);
            }
        }
        log(logger, formatLoggingMessage(buffer));
    }

    protected void logReader(Message message, Reader reader, LoggingMessage buffer, String ct) {
        try {
            CachedWriter writer = new CachedWriter();
            IOUtils.copyAndCloseInput(reader, writer);
            message.setContent(Reader.class, writer.getReader());

            if (writer.getTempFile() != null) {
                //large thing on disk...
                buffer.getMessage()
                        .append("\nMessage (saved to tmp file):\n")
                        .append("Filename: ")
                        .append(writer.getTempFile().getAbsolutePath())
                        .append("\n");
            }

            if (!isShowBinaryContent() && isBinaryContent(ct)) {
                buffer.getPayload().append(String.format(BINARY_CONTENT_PAYLOAD, ct, writer.size()));
            } else {
                if (writer.size() > limit && limit != -1) {
                    buffer.getMessage().append("(message truncated to ").append(limit).append(" bytes)\n");
                }
                writer.writeCacheTo(buffer.getPayload(), limit);
            }
        } catch (Exception e) {
            throw new Fault(e);
        }
    }
    protected void logInputStream(Message message, InputStream is, LoggingMessage buffer,
                                  String encoding, String ct) {
        CachedOutputStream bos = new CachedOutputStream();
        if (threshold > 0) {
            bos.setThreshold(threshold);
        }
        try {
            // use the appropriate input stream and restore it later
            InputStream bis = is instanceof DelegatingInputStream
                ? ((DelegatingInputStream)is).getInputStream() : is;


            //only copy up to the limit since that's all we need to log
            //we can stream the rest
            IOUtils.copyAtLeast(bis, bos, limit == -1 ? Integer.MAX_VALUE : limit);
            bos.flush();
            bis = new SequenceInputStream(bos.getInputStream(), bis);

            // restore the delegating input stream or the input stream
            if (is instanceof DelegatingInputStream) {
                ((DelegatingInputStream)is).setInputStream(bis);
            } else {
                message.setContent(InputStream.class, bis);
            }

            if (bos.getTempFile() != null) {
                //large thing on disk...
                buffer.getMessage()
                        .append("\nMessage (saved to tmp file):\n")
                        .append("Filename: ")
                        .append(bos.getTempFile().getAbsolutePath()).append("\n");
            }
            if (!isShowBinaryContent() && isBinaryContent(ct)) {
                buffer.getPayload().append(String.format(BINARY_CONTENT_PAYLOAD, ct, bos.size()));
            } else {
                if (bos.size() > limit && limit != -1) {
                    buffer.getMessage().append("(message truncated to ").append(limit).append(" bytes)\n");
                }

                writePayload(buffer.getPayload(), bos, encoding, ct);
            }

            bos.close();
        } catch (Exception e) {
            throw new Fault(e);
        }
    }

    protected String formatLoggingMessage(LoggingMessage loggingMessage) {
        final StringBuilder payload = loggingMessage.getPayload();
        if (payload.length() > 0) {
            final String payloadString = payload.toString();
            final String passwordCleanedPayload = PASSWORD_PAYLOAD_PATTERN.matcher(payloadString)
                    .replaceAll("\"password\": \"ropa}|{ ripo9aM\"");
            final String cleanedPayload = USER_NAME_PAYLOAD_PATTERN.matcher(passwordCleanedPayload)
                    .replaceAll("\"userName\": \"6aT9|\"");
            payload.replace(0, payload.length(), cleanedPayload);
        }
        return loggingMessage.toString();
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public boolean isBinaryContent(String contentType) {
        return contentType != null && (BINARY_CONTENT_MEDIA_TYPES.contains(contentType)
                || super.isBinaryContent(contentType)
                || contentType.contains("multipart/form-data"));
    }
}
