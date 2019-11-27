package org.unidata.mdm.data.audit;

import org.unidata.mdm.data.context.DeleteRequestContext;

import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public enum DataRecordDeleteAuditEventBuilder implements DataRecordAuditEventBuilder {
    INSTANCE;

    @Override
    public Map<String, String> buildParameters(Map<String, Object> parameters) {
        final DeleteRequestContext context = (DeleteRequestContext) parameters.get(AuditDataConstants.CONTEXT_FILED);
        return extractRecordInfo(context);
    }
}
