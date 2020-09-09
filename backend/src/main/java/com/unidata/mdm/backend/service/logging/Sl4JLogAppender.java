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

package com.unidata.mdm.backend.service.logging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.api.rest.dto.logging.LogEntry;
import com.unidata.mdm.backend.api.rest.dto.logging.LogLevel;

/**
 * SL4J log appender. 
 * This class log incoming messages with help of SL4J.
 * @author ilya.bykov
 */
public class Sl4JLogAppender implements LogAppender {

    /** The Constant DELIMETER. */
    private static final String DELIMETER = " | ";

    /** SL4J logger. */
    private Logger logger;

    /**
     * Instantiates a new sl4j log appender.
     *
     * @param loggerName
     *            the logger name
     */
    public Sl4JLogAppender(String loggerName) {
        this();
        logger = LoggerFactory.getLogger(loggerName);
    }

    /**
     * Instantiates a new sl4j log appender.
     */
    private Sl4JLogAppender() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(LogEntry logEntry, Object... params) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            String string = params[i].toString();
            message.append(DELIMETER).append(string);
        }
        message.append(DELIMETER).append(logEntry.toString());

        if (LogLevel.DEBUG.equals(logEntry.getLevel())) {
            logger.debug(message.toString());
        } else if (LogLevel.ERROR.equals(logEntry.getLevel())) {
            logger.error(message.toString());
        } else if (LogLevel.FATAL.equals(logEntry.getLevel())) {
            logger.error(message.toString());
        } else if (LogLevel.INFO.equals(logEntry.getLevel())) {
            logger.info(message.toString());
        } else if (LogLevel.WARN.equals(logEntry.getLevel())) {
            logger.warn(message.toString());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(List<LogEntry> logEntries, Object... params) {
        if (logEntries != null) {
            logEntries.forEach(entry -> log(entry, params));
        }

    }

}
