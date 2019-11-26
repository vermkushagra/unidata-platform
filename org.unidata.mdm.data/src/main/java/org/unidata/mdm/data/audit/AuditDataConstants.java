package org.unidata.mdm.data.audit;

/**
 * @author Alexander Malyshev
 */
public final class AuditDataConstants {
    private AuditDataConstants() { }

    public static final String CONTEXT_FILED = "context";

    public static final String RECORD_UPSERT_EVENT_TYPE = "RECORD_UPSERT";
    public static final String RECORD_GET_EVENT_TYPE = "RECORD_GET";
    public static final String RECORD_DELETE_EVENT_TYPE = "RECORD_DELETE";
}
