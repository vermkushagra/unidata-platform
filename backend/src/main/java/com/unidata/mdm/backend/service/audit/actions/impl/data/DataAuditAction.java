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

package com.unidata.mdm.backend.service.audit.actions.impl.data;

import static com.unidata.mdm.backend.service.search.Event.DATE_FORMATTER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.ValidityRange;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.service.audit.SubSystem;
import com.unidata.mdm.backend.service.audit.actions.AuditAction;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public abstract class DataAuditAction implements AuditAction {

    /**
     * Details for new record
     */
    public final static String NEW_RECORD = "Новая Запись";
    /**
     * Details for updating record
     */
    public final static String UPDATE_RECORD = "Обновление Записи";
    /**
     * Details for deleting record
     */
    public final static String DELETE_RECORD = "Удаление Записи";
    /**
     * Range from
     */
    public final static String FROM = "C: ";
    /**
     * Range to
     */
    public final static String TO = "По: ";

    public static final String IMPORT_SOURCE = "Источник импортируемых данных = ";

    /**
     * @param context - record identify ctx
     * @param event   - audit event
     */
    protected static void putRecordInfo(RecordIdentityContext context, Event event) {
        RecordKeys keys = context.keys();
        if (keys != null) {
            event.putEntity(StringUtils.isBlank(keys.getEntityName()) ? context.getEntityName() : keys.getEntityName());
            event.addEtalonId(keys.getEtalonKey() == null ? null : keys.getEtalonKey().getId());
            OriginKey originKey = keys.getOriginKey();
            event.addOriginId(originKey == null ? context.getOriginKey() : originKey.getId());
            event.addExternalId(originKey == null ? context.getExternalId() : originKey.getExternalId());
            event.putSourceSystem(originKey == null ? context.getSourceSystem() : originKey.getSourceSystem());
        } else {
            event.putEntity(context.getEntityName());
            event.addEtalonId(context.getEtalonKey());
            event.addOriginId(context.getOriginKey());
            event.addExternalId(context.getExternalId());
            event.putSourceSystem(context.getSourceSystem());
        }
    }

    protected static String getValidityRange(ValidityRange validityRange) {
        Date from = validityRange.getValidFrom();
        String fromString = from == null ? "Начала времен." : DATE_FORMATTER.get().format(from);
        Date to = validityRange.getValidTo();
        String toString = to == null ? "Окончание времен." : DATE_FORMATTER.get().format(to);
        StringBuilder range = new StringBuilder();
        return range.append(FROM)
                .append(fromString)
                .append(" - ")
                .append(TO)
                .append(toString)
                .toString();
    }

    protected static void enrichWithDQMessages(Event event, UpsertRequestContext context) {

        String dqErrors = null;
        if (Objects.nonNull(context.getDqErrors()) && !context.getDqErrors().isEmpty()) {
            dqErrors = context.getDqErrors()
                    .stream()
                    .map(DataQualityError::getMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        }

        String warnings = "";
        if (context.keys() != null && isNotBlank(context.getEtalonKey())) {
            RecordKeys keys = context.keys();
            if (!Objects.equals(context.getEtalonKey(), keys.getEtalonKey().getId())) {
                warnings = "Предупреждение [Указанный эталоный ключ не соотвествует системному эталонному ключю]";
            }
        }

        boolean hasDq = isNotBlank(dqErrors);
        boolean hasWarnings = isNotBlank(warnings);
        if (hasDq || hasWarnings) {
            String additionInfo = hasDq ? "|Сообщения DQ [" + dqErrors + "]" : "";
            additionInfo = hasWarnings ? additionInfo + "|" + warnings : additionInfo;
            Object existing = event.get(Event.DETAILS);
            if (Objects.nonNull(existing)) {
                event.reclaim(Event.DETAILS, existing.toString() + additionInfo);
            } else {
                // Suppressed by constructor
                event.putDetails(additionInfo);
            }
        }
    }

    protected static void enrichByRowNum(Event event, Integer rowNum) {
        Object existing = event.get(Event.DETAILS);
        if (Objects.nonNull(rowNum) && Objects.nonNull(existing)) {
            event.reclaim(Event.DETAILS,
                    "Порядковый номер в импортируемом сете данных = " + rowNum + "|" + existing.toString());
        }
    }

    protected static void enrichByImportSource(Event event, String sourceDescription) {
        Object existing = event.get(Event.DETAILS);
        if (Objects.nonNull(sourceDescription) && Objects.nonNull(existing)) {
            event.reclaim(Event.DETAILS,
                    IMPORT_SOURCE + sourceDescription + "|" + existing.toString());
        }
    }

    @Override
    public SubSystem getSubsystem() {
        return SubSystem.DATA;
    }
}
