package org.unidata.mdm.data.audit;

import org.unidata.mdm.data.context.GetRequestContext;

import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public enum  DataRecordGetAuditEventBuilder implements DataRecordAuditEventBuilder {
    INSTANCE;

    @Override
    public Map<String, String> buildParameters(Map<String, Object> parameters) {
        final GetRequestContext context = (GetRequestContext) parameters.get(AuditDataConstants.CONTEXT_FILED);
        return extractRecordInfo(context);
    }
}
