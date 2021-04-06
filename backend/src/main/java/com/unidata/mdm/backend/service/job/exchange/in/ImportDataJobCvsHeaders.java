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
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;

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
            boolean isActive = details.equals(UpsertDataAuditAction.INSERT_ACTION_NAME)
                    || details.equals(UpsertDataAuditAction.UPDATE_ACTION_NAME);
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
            SearchResultHitFieldDTO field = hit.getFieldValue(getLinkedAuditField().getField());
            final List<Object> values = field != null && field.getValues() != null ?
                    field.getValues() : Collections.emptyList();
            boolean insert = values.stream().anyMatch(UpsertDataAuditAction.INSERT_ACTION_NAME::equals);
            boolean update = values.stream().anyMatch(UpsertDataAuditAction.UPDATE_ACTION_NAME::equals);
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
