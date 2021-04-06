package com.unidata.mdm.backend.service.job.removerelations;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;

/**
 * Cvs headers for remove relation job!
 */
public enum RemoveRelationsJobCvsHeaders implements CvsElementExtractor<SearchResultHitDTO> {
    ETALON_ID("app.job.batch.delete.report.id", AuditHeaderField.ETALON_ID) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {
            SearchResultHitFieldDTO field = hit.getFieldValue(getLinkedAuditField().getField());
            Object object = field == null ? null : field.getFirstValue();
            return object == null ? EMPTY : String.valueOf(object);
        }
    },
    OPERATION_RESULT("app.job.batch.delete.report.result", AuditHeaderField.SUCCESS) {
        @Nonnull
        @Override
        public String getElement(SearchResultHitDTO hit) {
            SearchResultHitFieldDTO field = hit.getFieldValue(getLinkedAuditField().getField());
            Object object = field == null ? null : field.getFirstValue();
            Boolean result = Boolean.parseBoolean(String.valueOf(object));
            return result ? MessageUtils.getMessage(SUCCESS_REMOVING) : MessageUtils.getMessage(FAILED_REMOVING);
        }
    },
    DETAILS("app.job.batch.delete.report.details", AuditHeaderField.DETAILS) {
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
    private static final String SUCCESS_REMOVING = "app.job.batch.delete.report.result.success";
    /**
     * Failed detail message
     */
    private static final String FAILED_REMOVING = "app.job.batch.delete.report.result.failed";
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
    RemoveRelationsJobCvsHeaders(String header, AuditHeaderField linkedAuditField) {
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
