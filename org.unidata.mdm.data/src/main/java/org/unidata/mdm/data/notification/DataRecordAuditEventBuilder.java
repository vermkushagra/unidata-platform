package org.unidata.mdm.data.notification;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public class DataRecordAuditEventBuilder {

    public static Map<String, Object> build(Map<String, Object> body) {
        final Map<String, Object> result = new HashMap<>(body);
        final RecordIdentityContext context = (RecordIdentityContext) result.remove(NotificationDataConstants.CONTEXT_FILED);
        result.putAll(extractRecordInfo(context));
        return result;
    }

    private static Map<String, Object> extractRecordInfo(final RecordIdentityContext context) {
        final Map<String, Object> recordInfo = new HashMap<>();
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
