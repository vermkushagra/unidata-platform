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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.unidata.mdm.backend.api.rest.dto.logging.LogEntry;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * Logging service.
 * @author ilya.bykov
 */
public class LogService {

    /** The Constant USER_NAME. */
    private static final String USER_NAME = "UserName";

    /** The log appender. */
    private LogAppender logAppender;

    /** The log cache. */
    private Cache<String, List<LogEntry>> logCache;

    /**
     * Instantiates a new log service.
     *
     * @param logAppender
     *            the log appender.
     * @param cacheTTL
     *            the cache ttl.</br> Cache will be evicted after ttl is
     *            expired.</br> However it is not guaranted that it will be done
     *            right at the moment when ttl is over.</br> If for some reason
     *            logging messages should be persisted at the exact moment, then
     *            use method 'persistCache'(e.g. from quartz)</br>
     * @param timeUnit
     *            the time unit. </br> Accepted values:
     *            <ul>
     *            <li><code>NANOSECONDS</code></li>
     *            <li><code>MICROSECONDS</code></li>
     *            <li><code>MILLISECONDS</code></li>
     *            <li><code>SECONDS</code></li>
     *            <li><code>MINUTES</code></li>
     *            <li><code>HOURS</code></li>
     *            <li><code>DAYS</code></li>
     *            </ul>
     */
    public LogService(LogAppender logAppender, int cacheTTL, String timeUnit) {
        this.logAppender = logAppender;
        this.logCache = CacheBuilder.newBuilder().expireAfterWrite(cacheTTL, TimeUnit.valueOf(timeUnit))
                .removalListener(new RemovalListener<String, List<LogEntry>>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, List<LogEntry>> notification) {
                        logAppender.log(notification.getValue(), USER_NAME, notification.getKey());
                    }
                }).build();
    }

    /**
     * Log immediate.
     *
     * @param logEntries
     *            the log entries
     * @param params
     *            the params
     */
    public void logImmediate(List<LogEntry> logEntries, Object... params) {
        logAppender.log(logEntries, params);
    }

    /**
     * Log immediate.
     *
     * @param logEntrie
     *            the log entrie
     * @param params
     *            the params
     */
    public void logImmediate(LogEntry logEntrie, Object... params) {
        logAppender.log(logEntrie, params);
    }

    /**
     * Log delayed.
     *
     * @param logEntries
     *            the log entries
     * @param params
     *            the params
     */
    public void logDelayed(List<LogEntry> logEntries, Object... params) {
        logCache.cleanUp();
        List<LogEntry> oldEntries = logCache.getIfPresent(SecurityUtils.getCurrentUserName());
        if (oldEntries != null) {
            oldEntries.addAll(logEntries);
        } else {
            logCache.put(SecurityUtils.getCurrentUserName(), logEntries);
        }
    }

    /**
     * Log delayed.
     *
     * @param logEntrie
     *            the log entrie
     * @param params
     *            the params
     */
    public void logDelayed(LogEntry logEntrie, Object... params) {
        logCache.cleanUp();
        List<LogEntry> logEntries = logCache.getIfPresent(SecurityUtils.getCurrentUserName());
        if (logEntries == null) {
            logEntries = new ArrayList<LogEntry>();
            logCache.put(SecurityUtils.getCurrentUserName(), logEntries);
        }
        logEntries.add(logEntrie);
    }

    /**
     * Method will persist all messages from log cache to configured appender.
     * It may be called e.g. from quartz job.
     */
    public void persistCache() {
        logCache.invalidateAll();
    }
}
