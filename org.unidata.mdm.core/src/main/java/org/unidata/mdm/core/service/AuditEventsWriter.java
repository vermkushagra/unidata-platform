package org.unidata.mdm.core.service;

import javax.annotation.Nonnull;

import org.unidata.mdm.core.type.audit.AuditAction;
import org.unidata.mdm.system.service.AfterContextRefresh;

@Deprecated
public interface AuditEventsWriter extends AfterContextRefresh {

    //event creation also can be async
    void writeSuccessEvent(@Nonnull AuditAction auditAction, Object... input);
    void writeUnsuccessfulEvent(@Nonnull AuditAction auditAction, @Nonnull Exception exception, Object... input);
}
