package org.unidata.mdm.core.type.security;

import org.unidata.mdm.core.context.AuthenticationRequestContext;

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
