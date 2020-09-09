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

package com.unidata.mdm.backend.service.job.softdeletecleanup;

import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;

import javax.annotation.Nonnull;

public enum SoftDeleteCleanupJobCvsHeaders implements CvsElementExtractor<SearchResultHitDTO> {
    ETALON_ID("app.job.soft.delete.cleanup.report.id", AuditHeaderField.ETALON_ID) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {
            SearchResultHitFieldDTO field = hit.getFieldValue(getLinkedAuditField().getField());
            Object object = field == null ? null : field.getFirstValue();
            return object == null ? EMPTY : String.valueOf(object);
        }
    },
    OPERATION_RESULT("app.job.soft.delete.cleanup.report.result", AuditHeaderField.SUCCESS) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {
            SearchResultHitFieldDTO field = hit.getFieldValue(getLinkedAuditField().getField());
            Object object = field == null ? null : field.getFirstValue();
            Boolean result = Boolean.parseBoolean(String.valueOf(object));
            return result ? MessageUtils.getMessage(SUCCESS_MODIFYING) : MessageUtils.getMessage(FAILED_MODIFYING);
        }
    },
    DETAILS("app.job.soft.delete.cleanup.report.details", AuditHeaderField.DETAILS) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {
            SearchResultHitFieldDTO field = hit.getFieldValue(getLinkedAuditField().getField());
            Object object = field == null ? null : field.getFirstValue();
            return object == null ? EMPTY : String.valueOf(object);
        }
    };

    /**
     * Empty result
     */
    private static final String EMPTY = "";
    /**
     * Success detail message
     */
    private static final String SUCCESS_MODIFYING = "app.job.soft.delete.cleanup.report.result.success";
    /**
     * Failed detail message
     */
    private static final String FAILED_MODIFYING = "app.job.soft.delete.cleanup.report.result.failed";
    /**
     * Header name
     */
    private final String header;
    /**
     * Linked Audit Field
     */
    private final AuditHeaderField linkedAuditField;

    /**
     * @param header           - header name
     * @param linkedAuditField - linked audit field
     */
    SoftDeleteCleanupJobCvsHeaders(String header, AuditHeaderField linkedAuditField) {
        this.header = header;
        this.linkedAuditField = linkedAuditField;
    }

    /**
     * @return linked with cvs audit audit header
     */
    public AuditHeaderField getLinkedAuditField() {
        return linkedAuditField;
    }

    @Nonnull
    @Override
    public String headerName() {
        return this.header;
    }
}
