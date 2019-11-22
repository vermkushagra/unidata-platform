package org.unidata.mdm.core.service;

import org.unidata.mdm.core.type.audit.AuditEvent;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Alexander Malyshev
 */
public interface AuditEventBuildersRegistryService {
    BiFunction<String, Map<String, Object>, AuditEvent> eventBuilder(String eventType);
    boolean eventBuilderRegistered(String eventType);
    void registerEventBuilder(String eventType, BiFunction<String, Map<String, Object>, AuditEvent> builder);
    BiFunction<String, Map<String, Object>, AuditEvent> defaultEventBuilder(String eventType);
}
