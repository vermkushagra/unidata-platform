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

package com.unidata.mdm.backend.api.rest.dto.logging;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * The Class LogEntry.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogEntry {
    
    /** The exception. */
    private String exception;
    
    /** The url. */
    private String url;
    
    /** The logger. */
    private String logger;
    
    /** The user agent. */
    private String userAgent;
    
    /** The ip address. */
    private String ipAddress;
    /** The message. */
    private String message;
    
    /** The level. */
    private LogLevel level;
    
    /** The date time. */
    private Date dateTime;

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the level.
     *
     * @return the level
     */
    public LogLevel getLevel() {
        return level;
    }

    /**
     * Sets the level.
     *
     * @param level            the level to set
     */
    public void setLevel(LogLevel level) {
        this.level = level;
    }

    /**
     * Gets the date time.
     *
     * @return the dateTime
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * Sets the date time.
     *
     * @param dateTime            the dateTime to set
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Gets the exception.
     *
     * @return the exception
     */
    public String getException() {
        return exception;
    }

    /**
     * Sets the exception.
     *
     * @param exception            the exception to set
     */
    public void setException(String exception) {
        this.exception = exception;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the logger.
     *
     * @return the logger
     */
    public String getLogger() {
        return logger;
    }

    /**
     * Sets the logger.
     *
     * @param logger            the logger to set
     */
    public void setLogger(String logger) {
        this.logger = logger;
    }

    /**
     * Gets the user agent.
     *
     * @return the userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the user agent.
     *
     * @param userAgent the userAgent to set
     */
    public void setUserAgent(String userAgent) {       
        this.userAgent = userAgent;
    }

    /**
     * Gets the ip address.
     *
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the ip address.
     *
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LogEntry [exception=");
        builder.append(exception);
        builder.append(", url=");
        builder.append(url);
        builder.append(", logger=");
        builder.append(logger);
        builder.append(", userAgent=");
        builder.append(userAgent);
        builder.append(", ipAddress=");
        builder.append(ipAddress);
        builder.append(", message=");
        builder.append(message);
        builder.append(", level=");
        builder.append(level);
        builder.append(", dateTime=");
        builder.append(Instant.ofEpochMilli(dateTime.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
        builder.append("]");
        return builder.toString();
    }
}
