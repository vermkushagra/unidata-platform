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

package com.unidata.mdm.backend.service.job.exchange.in;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.actions.impl.data.DataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.UpsertDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.UpsertRelationAuditAction;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;
import org.apache.commons.lang3.BooleanUtils;

public enum ImportDataJobCvsHeaders implements CvsElementExtractor<SearchResultHitDTO> {
    ID("app.job.import.report.id", AuditHeaderField.ETALON_ID),
    EXTERNAL_ID("app.job.import.report.external_id", AuditHeaderField.EXTERNAL_ID),
    SOURCE_SYSTEM("app.job.import.report.source_system", AuditHeaderField.SOURCE_SYSTEM),
    ENTITY("app.job.import.report.entity_name", AuditHeaderField.ENTITY),
    ORIGIN_KEYS("app.job.import.report.origin_id", AuditHeaderField.ORIGIN_ID),
    SOURCE_NAME("app.job.import.report.source_of_data", AuditHeaderField.DETAILS) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {

            String details = super.getElement(hit);
            int start = details.indexOf(DataAuditAction.IMPORT_SOURCE);
            if (start < 0) {
                return EMPTY;
            }

            return details.substring(start + DataAuditAction.IMPORT_SOURCE.length(), details.indexOf('|', start));
        }
    },
    FROM("app.job.import.report.from", AuditHeaderField.DETAILS) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {
            String details = super.getElement(hit);
            int from = details.indexOf(DataAuditAction.FROM);
            if (from < 0) {
                return "";
            } else {
                from += DataAuditAction.FROM.length();
                int end = details.indexOf(".", from);
                return details.substring(from, end);
            }
        }
    },
    TO("app.job.import.report.to", AuditHeaderField.DETAILS) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {
            String details = super.getElement(hit);
            int to = details.indexOf(DataAuditAction.TO);
            if (to < 0) {
                return "";
            } else {
                to += DataAuditAction.TO.length();
                int end = details.indexOf(".", to);
                return details.substring(to, end);
            }
        }
    },
    IS_ACTIVE("app.job.import.report.is_active", AuditHeaderField.ACTION) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {
            String details = super.getElement(hit);
            boolean isActive = details.equals(UpsertDataAuditAction.DATA_INSERT_ACTION_NAME)
                    || details.equals(UpsertDataAuditAction.DATA_UPDATE_ACTION_NAME);
            boolean isInactive = details.contains(AuditActions.DATA_DELETE.name());
            return isActive
                    ? MessageUtils.getMessage("app.job.import.report.is_active.yes")
                    : isInactive ? MessageUtils.getMessage("app.job.import.report.is_active.no") : "";
        }
    },
    RECORD_STATUS("app.job.import.report.status", AuditHeaderField.ACTION) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {

            SearchResultHitFieldDTO successField = hit.getFieldValue(AuditHeaderField.SUCCESS.getField());
            if (successField != null && successField.getFirstValue() != null
                    && Boolean.FALSE.equals(BooleanUtils.toBoolean(successField.getFirstValue().toString()))) {
                return MessageUtils.getMessage("app.job.import.report.status.rejected");
            }

            SearchResultHitFieldDTO field = hit.getFieldValue(getLinkedAuditField().getField());
            final List<Object> values = field != null && field.getValues() != null ?
                    field.getValues() : Collections.emptyList();
            boolean insert = values.stream().anyMatch(v ->
                    UpsertDataAuditAction.DATA_INSERT_ACTION_NAME.equals(v) || UpsertRelationAuditAction.RELATION_INSERT_ACTION_NAME.equals(v)
            );
            boolean update = !insert && values.stream().anyMatch(v ->
                    UpsertDataAuditAction.DATA_UPDATE_ACTION_NAME.equals(v) || UpsertRelationAuditAction.RELATION_UPDATE_ACTION_NAME.equals(v)
            );

            return insert
                    ? MessageUtils.getMessage("app.job.import.report.status.created")
                    : update
                        ? MessageUtils.getMessage("app.job.import.report.status.updated")
                        : MessageUtils.getMessage("app.job.import.report.status.rejected");
        }
    },
    IMPORT_DESC("app.job.import.report.details", AuditHeaderField.DETAILS);

    private final String header;

    private final AuditHeaderField linkedAuditField;

    ImportDataJobCvsHeaders(String name, AuditHeaderField infoHolder) {
        this.header = name;
        this.linkedAuditField = infoHolder;
    }

    @Nonnull
    @Override
    public String headerName() {
        return this.header;
    }

    /**
     * @return linked with cvs audit audit header
     */
    public AuditHeaderField getLinkedAuditField() {
        return linkedAuditField;
    }
    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getElement(SearchResultHitDTO hit) {
        SearchResultHitFieldDTO field = hit.getFieldValue(this.getLinkedAuditField().getField());
        Object object = field == null ? null : field.getFirstValue();
        return object == null ? EMPTY : object.toString();
    }
}
