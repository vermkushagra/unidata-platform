package org.unidata.mdm.core.dao;

import org.unidata.mdm.core.context.AuditEventWriteContext;
import org.unidata.mdm.core.dto.EnrichedAuditEvent;

import java.util.Collection;
import java.util.List;

/**
 * Dao for audit
 * @author Dmitry Kopin on 11.04.2017.
 */
public interface AuditDao {

    /**
     * insert new audit events
     * @param auditEventWriteContexts list of events to insert
     * @return true if success, else false
     */
    boolean insert(Collection<AuditEventWriteContext> auditEventWriteContexts);

    /**
     * Delete audit records for exceeded lifetime.
     *
     * @param maxLifetime lifetime in minutes.
     * @return count of deleted records.
     */
    long deleteOldAuditEvents(long maxLifetime);
}
