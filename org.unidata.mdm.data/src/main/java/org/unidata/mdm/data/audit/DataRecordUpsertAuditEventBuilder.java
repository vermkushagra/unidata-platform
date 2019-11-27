package org.unidata.mdm.data.audit;

import org.unidata.mdm.core.audit.AuditConstants;
import org.unidata.mdm.core.audit.AuditUtils;
import org.unidata.mdm.core.type.audit.AuditEvent;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.data.context.UpsertRequestContext;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Alexander Malyshev
 */
public enum  DataRecordUpsertAuditEventBuilder implements BiFunction<String, Map<String, Object>, AuditEvent> {
    INSTANCE;

    @Override
    public AuditEvent apply(String eventType, Map<String, Object> parameters) {
        final UpsertRequestContext context = (UpsertRequestContext) parameters.get(AuditDataConstants.CONTEXT_FILED);
        final Map<String, String> params = Maps.of(
                "etalon_key", context.getEtalonKey(),
                "origin_key", context.getOriginKey()
        );
        return parameters.containsKey(AuditConstants.EXCEPTION_FIELD) ?
                AuditUtils.failEvent(eventType, params, (Throwable) parameters.get(AuditConstants.EXCEPTION_FIELD)) :
                AuditUtils.successEvent(eventType, params);
    }
}
