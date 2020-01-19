/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
