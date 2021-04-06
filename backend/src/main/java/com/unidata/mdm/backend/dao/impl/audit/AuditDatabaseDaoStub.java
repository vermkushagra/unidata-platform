package com.unidata.mdm.backend.dao.impl.audit;

import com.unidata.mdm.backend.dao.AuditDao;
import com.unidata.mdm.backend.po.audit.AuditPO;

/**
 * audit dao stub
 * @author Dmitry Kopin on 03.08.2017.
 */
public class AuditDatabaseDaoStub implements AuditDao {

    @Override
    public boolean insert(AuditPO auditEvent){
        return true;
    }

    @Override
    public long deleteOldAuditEvents(long maxLifetime) {
        return 0;
    }
}
