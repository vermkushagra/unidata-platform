package org.unidata.mdm.core.dto;

import org.unidata.mdm.core.notification.NotificationSystemConstants;
import org.unidata.mdm.core.util.Maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BusMessage {
    private final Object body;
    private final Map<String, Object> headers = new HashMap<>();

    public BusMessage(final Object body) {
        this.body = body;
    }

    public BusMessage(final Object body, final Map<String, Object> headers) {
        this.body = body;
        if (headers != null) {
            this.headers.putAll(headers);
        }
    }

    public Object getBody() {
        return body;
    }

    public Map<String, Object> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public static BusMessage withType(final String type, Object body) {
        return new BusMessage(
                body,
                Maps.of(NotificationSystemConstants.TYPE, type)
        );
    }
}
