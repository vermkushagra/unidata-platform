package org.unidata.mdm.core.service;

import org.unidata.mdm.core.context.AuditEventWriteContext;

/**
 * @author Alexander Malyshev
 */
public interface AuditServiceStorageService {
    String id();
    void write(AuditEventWriteContext auditEventWriteContext);
}
