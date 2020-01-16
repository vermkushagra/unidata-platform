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

package org.unidata.mdm.core.dto;

import org.apache.commons.collections4.MapUtils;
import org.unidata.mdm.core.type.audit.AuditEvent;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Malyshev
 */
public class EnrichedAuditEvent implements AuditEvent {

    public static final String TYPE_FIELD = "type";
    public static final String PARAMETERS_FIELD = "parameters";
    public static final String SUCCESS_FIELD = "success";
    public static final String LOGIN_FIELD = "login";
    public static final String CLIENT_IP_FIELD = "client_ip";
    public static final String SERVER_IP_FIELD = "server_ip";
    public static final String ENDPOINT_FIELD = "endpoint";
    public static final String WHEN_HAPPENED_FIELD = "when_happened";

    private final String type;

    private final Map<String, String> parameters = new HashMap<>();

    private final boolean success;

    private final String login;

    private final String clientIp;

    private final String serverIp;

    private final String endpoint;

    private final LocalDateTime whenwHappened;

    public EnrichedAuditEvent(
            final String type,
            final Map<String, String> parameters,
            final boolean success,
            final String login,
            final String clientIp,
            final String serverIp,
            final String endpoint,
            final LocalDateTime whenHappened
    ) {
        this.type = Objects.requireNonNull(type);
        if (MapUtils.isNotEmpty(parameters)) {
            this.parameters.putAll(parameters);
        }
        this.success = success;
        this.login = login;
        this.clientIp = clientIp;
        this.serverIp = serverIp;
        this.endpoint = endpoint;
        this.whenwHappened = whenHappened;
    }

    public EnrichedAuditEvent(
            final AuditEvent auditEvent,
            final String login,
            final String clientIp,
            final String serverIp,
            final String endpoint,
            final LocalDateTime whenwHappened
    ) {
        this(auditEvent.type(), auditEvent.parameters(), auditEvent.success(), login, clientIp, serverIp, endpoint, whenwHappened);
    }

    @Nonnull
    @Override
    public String type() {
        return type;
    }

    @Nonnull
    @Override
    public Map<String, String> parameters() {
        return parameters;
    }

    @Override
    public boolean success() {
        return success;
    }

    public String getLogin() {
        return login;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public LocalDateTime getWhenwHappened() {
        return whenwHappened;
    }
}
