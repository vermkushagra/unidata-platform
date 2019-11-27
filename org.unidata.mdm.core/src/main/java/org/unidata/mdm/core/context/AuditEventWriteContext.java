package org.unidata.mdm.core.context;

import org.unidata.mdm.core.dto.EnrichedAuditEvent;

/**
 * @author Alexander Malyshev
 */
public class AuditEventWriteContext {

    private final EnrichedAuditEvent enhancedAuditEvent;

    private final String currentUserStorageId;

    public AuditEventWriteContext(final AuditEventWriteContextBuilder builder) {
        enhancedAuditEvent = builder.enhancedAuditEvent;
        currentUserStorageId = builder.currentUserStorageId;
    }

    public EnrichedAuditEvent getEnhancedAuditEvent() {
        return enhancedAuditEvent;
    }

    public String getCurrentUserStorageId() {
        return currentUserStorageId;
    }

    public static AuditEventWriteContextBuilder builder() {
        return new AuditEventWriteContextBuilder();
    }

    public static class AuditEventWriteContextBuilder {
        private EnrichedAuditEvent enhancedAuditEvent;

        private String currentUserStorageId;

        public AuditEventWriteContextBuilder enhancedAuditEvent(final EnrichedAuditEvent enhancedAuditEvent) {
            this.enhancedAuditEvent = enhancedAuditEvent;
            return this;
        }

        public AuditEventWriteContextBuilder currentUserStorageId(final String currentUserStorageId) {
            this.currentUserStorageId = currentUserStorageId;
            return this;
        }

        public AuditEventWriteContext build() {
            return new AuditEventWriteContext(this);
        }
    }
}
