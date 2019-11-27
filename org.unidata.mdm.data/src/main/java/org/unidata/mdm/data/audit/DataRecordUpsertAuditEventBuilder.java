package org.unidata.mdm.data.audit;

import org.unidata.mdm.data.context.UpsertRequestContext;

import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public enum DataRecordUpsertAuditEventBuilder implements DataRecordAuditEventBuilder {
    INSTANCE;

    @Override
    public Map<String, String> buildParameters(Map<String, Object> parameters) {
        final UpsertRequestContext context = (UpsertRequestContext) parameters.get(AuditDataConstants.CONTEXT_FILED);
        return extractRecordInfo(context);
    }
}
