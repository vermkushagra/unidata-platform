package com.unidata.mdm.backend.dao;

import com.unidata.mdm.backend.po.audit.AuditPO;

/**
 * Dao for audit
 * @author Dmitry Kopin on 11.04.2017.
 */
public interface AuditDao {
    /**
     * insert new audit record
     * @param auditPO record for insert
     * @return true if success, else false
     */
    boolean insert(AuditPO auditPO);

    /**
     * Delete audit records for exceeded lifetime.
     *
     * @param maxLifetime lifetime in minutes.
     * @return count of deleted records.
     */
    long deleteOldAuditEvents(long maxLifetime);
}
