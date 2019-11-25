package org.unidata.mdm.core.service.impl;

import org.springframework.stereotype.Service;
import org.unidata.mdm.core.context.AuditEventWriteContext;
import org.unidata.mdm.core.dao.AuditDao;
import org.unidata.mdm.core.service.AuditServiceStorageService;

/**
 * @author Alexander Malyshev
 */
@Service
public class DbAuditServiceStorageService implements AuditServiceStorageService {

    private final AuditDao auditDao;

    public DbAuditServiceStorageService(final AuditDao auditDao) {
        this.auditDao = auditDao;
    }

    @Override
    public String id() {
        return "db";
    }

    @Override
    public void write(AuditEventWriteContext auditEventWriteContext) {
        auditDao.insert(auditEventWriteContext.getEnhancedAuditEvent());
    }
}
