package org.unidata.mdm.core.dao;

import org.unidata.mdm.core.dto.EnrichedAuditEvent;

/**
 * Dao for audit
 * @author Dmitry Kopin on 11.04.2017.
 */
public interface AuditDao {
    /**
     * insert new audit event record
     * @param enhancedAuditEvent record for insert
     * @return true if success, else false
     */
    boolean insert(EnrichedAuditEvent enhancedAuditEvent);

    /**
     * Delete audit records for exceeded lifetime.
     *
     * @param maxLifetime lifetime in minutes.
     * @return count of deleted records.
     */
    long deleteOldAuditEvents(long maxLifetime);
}
