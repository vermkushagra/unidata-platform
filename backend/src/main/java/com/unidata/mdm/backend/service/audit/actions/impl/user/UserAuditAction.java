package com.unidata.mdm.backend.service.audit.actions.impl.user;

import com.unidata.mdm.backend.service.audit.SubSystem;
import com.unidata.mdm.backend.service.audit.actions.AuditAction;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public abstract class UserAuditAction implements AuditAction {

    @Override
    public SubSystem getSubsystem() {
        return SubSystem.USERS;
    }
}
