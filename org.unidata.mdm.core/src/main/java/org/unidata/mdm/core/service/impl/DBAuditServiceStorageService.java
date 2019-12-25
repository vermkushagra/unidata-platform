package org.unidata.mdm.core.service.impl;

import org.springframework.stereotype.Service;
import org.unidata.mdm.core.context.AuditEventWriteContext;
import org.unidata.mdm.core.dao.AuditDao;
import org.unidata.mdm.core.service.AuditServiceStorageService;

import java.util.Collection;

/**
 * @author Alexander Malyshev
 */
@Service("dbAuditServiceStorageService")
public class DBAuditServiceStorageService implements AuditServiceStorageService {

    private final AuditDao auditDao;

    public DBAuditServiceStorageService(final AuditDao auditDao) {
        this.auditDao = auditDao;
    }

    @Override
    public void write(Collection<AuditEventWriteContext> auditEventWriteContext) {
        auditDao.insert(auditEventWriteContext);
    }
}
