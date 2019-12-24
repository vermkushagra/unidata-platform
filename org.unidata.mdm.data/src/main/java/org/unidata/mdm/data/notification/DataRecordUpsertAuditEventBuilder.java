package org.unidata.mdm.data.notification;

import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public enum DataRecordUpsertAuditEventBuilder implements DataRecordAuditEventBuilder {
    INSTANCE;

    public static Map<String, Object> transform(final Map<String, Object> body) {
        return INSTANCE.build(body);
    }
}
