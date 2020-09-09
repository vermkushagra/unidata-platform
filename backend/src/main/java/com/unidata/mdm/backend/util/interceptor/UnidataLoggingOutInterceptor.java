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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;

/**
 * @author Aleksandr Magdenko
 */
@NoJSR250Annotations
public class UnidataLoggingOutInterceptor extends AbstractLoggingInterceptor {
    private static final Logger LOG = LogUtils.getLogger(UnidataLoggingOutInterceptor.class);
    private static final String LOG_SETUP = UnidataLoggingOutInterceptor.class.getName() + ".log-setup";

    private static final String BINARY_CONTENT_PAYLOAD = "Binary data! Mime-type: %s, length: %d bytes";

    private static final List<String> BINARY_CONTENT_MEDIA_TYPES;
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

    public UnidataLoggingOutInterceptor(String phase) {
        super(phase);
        addBefore(StaxOutInterceptor.class.getName());
    }

    public UnidataLoggingOutInterceptor() {
        this(Phase.PRE_STREAM);
    }

    public UnidataLoggingOutInterceptor(int lim) {
        this();
        limit = lim;
    }

    public UnidataLoggingOutInterceptor(PrintWriter w) {
        this();
        this.writer = w;
    }

    public void handleMessage(Message message) throws Fault {
        final OutputStream os = message.getContent(OutputStream.class);
        final Writer iowriter = message.getContent(Writer.class);
        if (os == null && iowriter == null) {
            return;
        }
        Logger logger = getMessageLogger(message);
        if (logger.isLoggable(Level.INFO) || writer != null) {
            // Write the output while caching it for the log message
            boolean hasLogged = message.containsKey(LOG_SETUP);
            if (!hasLogged) {
                message.put(LOG_SETUP, Boolean.TRUE);
                if (os != null) {
                    final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
                    if (threshold > 0) {
                        newOut.setThreshold(threshold);
                    }
                    if (limit > 0) {
                        newOut.setCacheLimit(limit);
                    }
                    message.setContent(OutputStream.class, newOut);
                    newOut.registerCallback(new LoggingCallback(logger, message, os));
                } else {
                    message.setContent(Writer.class, new LogWriter(logger, message, iowriter));
                }
            }
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

    private LoggingMessage setupBuffer(Message message) {
        String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }
        final LoggingMessage buffer
            = new LoggingMessage("Outbound Message\n---------------------------",
                                 id);

        Integer responseCode = (Integer)message.get(Message.RESPONSE_CODE);
        if (responseCode != null) {
            buffer.getResponseCode().append(responseCode);
        }

        String encoding = (String)message.get(Message.ENCODING);
        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }
        String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }
        String address = (String)message.get(Message.ENDPOINT_ADDRESS);
        if (address != null) {
            buffer.getAddress().append(address);
            String uri = (String)message.get(Message.REQUEST_URI);
            if (uri != null && !address.startsWith(uri)) {
                if (!address.endsWith("/") && !uri.startsWith("/")) {
                    buffer.getAddress().append("/");
                }
                buffer.getAddress().append(uri);
            }
        }
        String ct = (String)message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            buffer.getHeader().append(headers);
        }
        return buffer;
    }

    private class LogWriter extends FilterWriter {
        StringWriter out2;
        int count;
        Logger logger; //NOPMD
        Message message;
        final int lim;

        public LogWriter(Logger logger, Message message, Writer writer) {
            super(writer);
            this.logger = logger;
            this.message = message;
            if (!(writer instanceof StringWriter)) {
                out2 = new StringWriter();
            }
            lim = limit == -1 ? Integer.MAX_VALUE : limit;
        }
        public void write(int c) throws IOException {
            super.write(c);
            if (out2 != null && count < lim) {
                out2.write(c);
            }
            count++;
        }
        public void write(char[] cbuf, int off, int len) throws IOException {
            super.write(cbuf, off, len);
            if (out2 != null && count < lim) {
                out2.write(cbuf, off, len);
            }
            count += len;
        }
        public void write(String str, int off, int len) throws IOException {
            super.write(str, off, len);
            if (out2 != null && count < lim) {
                out2.write(str, off, len);
            }
            count += len;
        }
        public void close() throws IOException {
            LoggingMessage buffer = setupBuffer(message);
            if (count >= lim) {
                buffer.getMessage().append("(message truncated to " + lim + " bytes)\n");
            }
            StringWriter w2 = out2;
            if (w2 == null) {
                w2 = (StringWriter)out;
            }
            String ct = (String)message.get(Message.CONTENT_TYPE);
            try {
                writePayload(buffer.getPayload(), w2, ct);
            } catch (Exception ex) {
                //ignore
            }
            log(logger, buffer.toString());
            message.setContent(Writer.class, out);
            super.close();
        }
    }

    protected String formatLoggingMessage(LoggingMessage buffer) {
        return buffer.toString();
    }

    class LoggingCallback implements CachedOutputStreamCallback {

        private final Message message;
        private final OutputStream origStream;
        private final Logger logger; //NOPMD
        private final int lim;

        public LoggingCallback(final Logger logger, final Message msg, final OutputStream os) {
            this.logger = logger;
            this.message = msg;
            this.origStream = os;
            this.lim = limit == -1 ? Integer.MAX_VALUE : limit;
        }

        public void onFlush(CachedOutputStream cos) {

        }

        public void onClose(CachedOutputStream cos) {
            LoggingMessage buffer = setupBuffer(message);

            String ct = (String)message.get(Message.CONTENT_TYPE);
            if (!isShowBinaryContent() && isBinaryContent(ct)) {
                buffer.getPayload().append(String.format(BINARY_CONTENT_PAYLOAD, ct, cos.size()));
                log(logger, formatLoggingMessage(buffer));
                return;
            }

            if (cos.getTempFile() == null) {
                //buffer.append("Outbound Message:\n");
                if (cos.size() >= lim) {
                    buffer.getMessage().append("(message truncated to " + lim + " bytes)\n");
                }
            } else {
                buffer.getMessage().append("Outbound Message (saved to tmp file):\n");
                buffer.getMessage().append("Filename: " + cos.getTempFile().getAbsolutePath() + "\n");
                if (cos.size() >= lim) {
                    buffer.getMessage().append("(message truncated to " + lim + " bytes)\n");
                }
            }
            try {
                String encoding = (String)message.get(Message.ENCODING);
                writePayload(buffer.getPayload(), cos, encoding, ct);
            } catch (Exception ex) {
                //ignore
            }

            log(logger, formatLoggingMessage(buffer));
            try {
                //empty out the cache
                cos.lockOutputStream();
                cos.resetOut(null, false);
            } catch (Exception ex) {
                //ignore
            }
            message.setContent(OutputStream.class,
                               origStream);
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;

    }

    @Override
    public boolean isBinaryContent(String contentType) {
        return contentType != null && (BINARY_CONTENT_MEDIA_TYPES.contains(contentType) ||
                super.isBinaryContent(contentType));
    }
}
