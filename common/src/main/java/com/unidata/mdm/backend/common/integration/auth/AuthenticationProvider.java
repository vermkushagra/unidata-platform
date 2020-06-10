package com.unidata.mdm.backend.common.integration.auth;

import com.unidata.mdm.backend.common.context.AuthenticationRequestContext;

/**
 * @author Denis Kostovarov
 */
@FunctionalInterface
public interface AuthenticationProvider {
    /**
     * Login a user.
     * @param context system params
     * @return User
     */
    User login(AuthenticationRequestContext context);
}
