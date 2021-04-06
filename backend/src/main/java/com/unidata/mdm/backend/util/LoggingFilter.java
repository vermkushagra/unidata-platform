package com.unidata.mdm.backend.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Custom logback filter. 
 * It's needed to distinguish client logs from backend logs.
 * @author ilya.bykov
 * 
 */
public class LoggingFilter extends AbstractMatcherFilter<ILoggingEvent> {
    /**
     * Logger name of rest client.
     */
    private static final String UNIDATA_REST_CLIENT = "UNIDATA_REST_CLIENT";

    /** {@inheritDoc} */
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getLoggerName().equals(UNIDATA_REST_CLIENT)) {
            return onMatch;
        } else {
            return onMismatch;
        }
    }

}
