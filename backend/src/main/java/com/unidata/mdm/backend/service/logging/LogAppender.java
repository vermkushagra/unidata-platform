package com.unidata.mdm.backend.service.logging;

import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.logging.LogEntry;

/**
 * Implement this interface if you will need custom log appender.
 * @author ilya.bykov
 */
public interface LogAppender {

    /**
     * Log single entry.
     *
     * @param logEntry
     *            the log entry
     * @param params
     *            additional parameters.
     */
    void log(LogEntry logEntry, Object... params);

    /**
     * Log multiple entries.
     *
     * @param logEntries
     *            the log entries
     * @param params
     *            additional parameters.
     */
    void log(List<LogEntry> logEntries, Object... params);
}
