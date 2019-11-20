package org.unidata.mdm.core.type.audit;

import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public interface AuditEvent {
    String type();
    Map<String, String> parameters();
    boolean success();
}
