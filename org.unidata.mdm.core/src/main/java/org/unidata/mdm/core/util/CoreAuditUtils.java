package org.unidata.mdm.core.util;

import org.unidata.mdm.core.dto.ImmutableAuditEvent;
import org.unidata.mdm.core.type.audit.AuditEvent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public final class CoreAuditUtils {

    private static final String ROLE_CREATE_EVENT_TYPE = "ROLE_CREATE";
    private static final String ROLE_DELETE_EVENT_TYPE = "ROLE_DELETE";
    private static final String ROLE_UPDATE_EVENT_TYPE = "ROLE_UPDATE";
    private static final String ROLE_LABEL_ATTACH_EVENT_TYPE = "ROLE_LABEL_ATTACH";
    private static final String LABEL_CREATE_EVENT_TYPE = "LABEL_CREATE";
    private static final String LABEL_UPDATE_EVENT_TYPE = "LABEL_UPDATE";
    private static final String LABEL_DELETE_EVENT_TYPE = "LABEL_DELETE";

    private CoreAuditUtils() {}

    public static AuditEvent roleCreateSuccess(final Map<String, String> parameters) {
        return successEvent(ROLE_CREATE_EVENT_TYPE, parameters);
    }

    public static AuditEvent roleCreateFail(final Map<String, String> parameters, Throwable t) {
        return failEvent(ROLE_CREATE_EVENT_TYPE, parameters, t);
    }

    public static AuditEvent roleDeleteSuccess(final Map<String, String> parameters) {
        return successEvent(ROLE_DELETE_EVENT_TYPE, parameters);
    }

    public static AuditEvent roleDeleteFail(final Map<String, String> parameters, Throwable t) {
        return failEvent(ROLE_DELETE_EVENT_TYPE, parameters, t);
    }

    public static AuditEvent roleUpdateSuccess(final Map<String, String> parameters) {
        return successEvent(ROLE_UPDATE_EVENT_TYPE, parameters);
    }

    public static AuditEvent roleUpdateFail(final Map<String, String> parameters, Throwable t) {
        return failEvent(ROLE_UPDATE_EVENT_TYPE, parameters, t);
    }

    public static AuditEvent roleLabelAttach(final Map<String, String> parameters) {
        return successEvent(ROLE_LABEL_ATTACH_EVENT_TYPE, parameters);
    }

    public static AuditEvent labelCreateSuccess(final Map<String, String> parameters) {
        return successEvent(LABEL_CREATE_EVENT_TYPE, parameters);
    }

    public static AuditEvent labelCreateFail(final Map<String, String> parameters, Throwable t) {
        return failEvent(LABEL_CREATE_EVENT_TYPE, parameters, t);
    }

    public static AuditEvent labelUpdateSuccess(final Map<String, String> parameters) {
        return successEvent(LABEL_UPDATE_EVENT_TYPE, parameters);
    }

    public static AuditEvent labelUpdateFail(final Map<String, String> parameters, Throwable t) {
        return failEvent(LABEL_UPDATE_EVENT_TYPE, parameters, t);
    }

    public static AuditEvent labelDeleteSuccess(final Map<String, String> parameters) {
        return successEvent(LABEL_DELETE_EVENT_TYPE, parameters);
    }

    public static AuditEvent labelDeleteFail(final Map<String, String> parameters, Throwable t) {
        return failEvent(LABEL_DELETE_EVENT_TYPE, parameters, t);
    }

    private static AuditEvent successEvent(final String type, final Map<String, String> parameters) {
        return new ImmutableAuditEvent(type, parameters, true);
    }

    private static AuditEvent failEvent(final String type, final Map<String, String> parameters, Throwable t) {
        Map<String, String> params = new HashMap<>(parameters);
        params.put("error", toString(t));
        return new ImmutableAuditEvent(type, params, false);
    }

    private static String toString(Throwable throwable) {
        final StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}
