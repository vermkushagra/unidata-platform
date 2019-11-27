package org.unidata.mdm.data.audit;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.audit.AuditConstants;
import org.unidata.mdm.core.audit.AuditUtils;
import org.unidata.mdm.core.type.audit.AuditEvent;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Alexander Malyshev
 */
public interface DataRecordAuditEventBuilder extends BiFunction<String, Map<String, Object>, AuditEvent> {

    @Override
    default AuditEvent apply(String eventType, Map<String, Object> parameters) {
        final Map<String, String> params = buildParameters(parameters);
        return parameters.containsKey(AuditConstants.EXCEPTION_FIELD) ?
                AuditUtils.failEvent(eventType, params, (Throwable) parameters.get(AuditConstants.EXCEPTION_FIELD)) :
                AuditUtils.successEvent(eventType, params);
    }

    Map<String, String> buildParameters(Map<String, Object> parameters);

    default Map<String, String> extractRecordInfo(final RecordIdentityContext context) {
        final Map<String, String> recordInfo = new HashMap<>();
        RecordKeys keys = context.keys();
        if (keys != null) {
            recordInfo.put("entity", StringUtils.isBlank(keys.getEntityName()) ? context.getEntityName() : keys.getEntityName());
            recordInfo.put("etalon_id", keys.getEtalonKey() == null ? null : keys.getEtalonKey().getId());
            RecordOriginKey originKey = keys.getOriginKey();
            recordInfo.put("origin_id", originKey == null ? context.getOriginKey() : originKey.getId());
            recordInfo.put("external_id", originKey == null ? context.getExternalId() : originKey.getExternalId());
            recordInfo.put("source_system", originKey == null ? context.getSourceSystem() : originKey.getSourceSystem());
        } else {
            recordInfo.put("entity", context.getEntityName());
            recordInfo.put("etalonId", context.getEtalonKey());
            recordInfo.put("origin_id", context.getOriginKey());
            recordInfo.put("external_id", context.getExternalId());
            recordInfo.put("source_system", context.getSourceSystem());
        }
        return recordInfo;
    }

}
