package org.unidata.mdm.core.util.audit;

import org.unidata.mdm.core.dto.ImmutableAuditEvent;
import org.unidata.mdm.core.type.audit.AuditEvent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public final class AuditUtils {

    public static final String ERROR_PARAMETER = "error";

    private AuditUtils() { }

    public static AuditEvent successEvent(final String type, final Map<String, String> parameters) {
        return new ImmutableAuditEvent(type, parameters, true);
    }

    public static AuditEvent failEvent(final String type, final Map<String, String> parameters, Throwable t) {
        Map<String, String> params = new HashMap<>(parameters);
        params.put(ERROR_PARAMETER, toString(t));
        return new ImmutableAuditEvent(type, params, false);
    }

    public static String toString(Throwable throwable) {
        final StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}
