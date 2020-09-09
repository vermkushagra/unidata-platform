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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.logging.LogEntry;

/**
 * Simple appender write logs to system.out.
 * @author ilya.bykov
 */
public class ConsoleLogAppender implements LogAppender {

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(LogEntry logEntry, Object... params) {
        StringBuilder message = new StringBuilder(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        message.append(" ")
                .append(logEntry.getLevel().name())
                .append(" ")
                .append(LocalDateTime.from(logEntry.getDateTime().toInstant()).format(DateTimeFormatter.BASIC_ISO_DATE))
                .append(" ").append(logEntry.toString());
        for (Object param : params) {
            message.append(" ").append(param.toString());
        }
        System.out.println(message);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(List<LogEntry> logEntries, Object... params) {
        logEntries.forEach(entry -> log(entry, params));

    }

}
