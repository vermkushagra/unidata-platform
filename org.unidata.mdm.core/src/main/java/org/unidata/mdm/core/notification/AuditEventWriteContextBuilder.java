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

package org.unidata.mdm.core.notification;

import org.apache.camel.Exchange;
import org.unidata.mdm.core.context.AuditEventWriteContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class AuditEventWriteContextBuilder {
    private AuditEventWriteContextBuilder() {}

    public static AuditEventWriteContext build(final Exchange exchange) {
        final Map<String, Object> msgBody = (Map<String, Object>) exchange.getIn().getBody();
        final Map<String, Object> body = new HashMap<>(msgBody != null ? msgBody : Collections.emptyMap());
        final Map<String, Object> headers = exchange.getIn().getHeaders();
        final Object login = body.remove(NotificationSystemConstants.LOGIN);
        final Object clientIp = body.remove(NotificationSystemConstants.CLIENT_IP);
        final Object serverIp = body.remove(NotificationSystemConstants.SERVER_IP);
        final Object endpoint = body.remove(NotificationSystemConstants.ENDPOINT);
        final Object whenHappened = body.remove(NotificationSystemConstants.WHEN_HAPPENED);
        return AuditEventWriteContext.builder()
                .type(headers.get(NotificationSystemConstants.TYPE).toString())
                .userLogin((login != null ? login : headers.get(NotificationSystemConstants.LOGIN)).toString())
                .clientIp((clientIp != null ? clientIp : headers.get(NotificationSystemConstants.CLIENT_IP)).toString())
                .serverIp((serverIp != null ? serverIp : headers.get(NotificationSystemConstants.SERVER_IP)).toString())
                .endpoint((endpoint != null ? endpoint : headers.get(NotificationSystemConstants.ENDPOINT)).toString())
                .success(wasSuccess(body))
                .whenHappened(
                        (LocalDateTime) (whenHappened != null ? whenHappened : headers.get(NotificationSystemConstants.WHEN_HAPPENED))
                )
                .addParameters(body)
                .build();
    }

    private static boolean wasSuccess(Map<? extends String, ?> body) {
        return !(body.containsKey(NotificationSystemConstants.EXCEPTION)
                || body.containsKey(NotificationSystemConstants.ERROR));
    }
}
