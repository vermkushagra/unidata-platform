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
