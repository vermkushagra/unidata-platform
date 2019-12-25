package org.unidata.mdm.core.service;

import org.unidata.mdm.core.context.AuditEventWriteContext;

import java.util.Collection;

/**
 * @author Alexander Malyshev
 */
public interface AuditServiceStorageService {
    void write(Collection<AuditEventWriteContext> auditEventWriteContext);
}
