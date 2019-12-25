package org.unidata.mdm.data.notification;

/**
 * @author Alexander Malyshev
 */
public final class NotificationDataConstants {
    private NotificationDataConstants() { }

    public static final String CONTEXT_FILED = "context";

    public static final String RECORD_UPSERT_EVENT_TYPE = "record-upsert";
    public static final String RECORD_GET_EVENT_TYPE = "record-get";
    public static final String RECORD_DELETE_EVENT_TYPE = "record-delete";
}
