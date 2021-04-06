package com.unidata.mdm.backend.service.audit.actions.impl.auth;

import com.unidata.mdm.backend.service.audit.SubSystem;
import com.unidata.mdm.backend.service.audit.actions.AuditAction;

import java.util.Map;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public abstract class AuthAuditAction implements AuditAction {


    /**
     * Used when login in not given
     */
    protected static final String NO_LOGIN = "NO-LOGIN-NAME-GIVEN";


    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof Map;
    }

    @Override
    public SubSystem getSubsystem() {
        return SubSystem.AUTH;
    }

}
