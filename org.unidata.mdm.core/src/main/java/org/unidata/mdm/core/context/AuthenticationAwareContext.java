package org.unidata.mdm.core.context;

import org.unidata.mdm.core.type.security.AuthenticationToken;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * A context, able to return current user authentication context.
 */
public interface AuthenticationAwareContext {
    /**
     * Gets current authentication token.
     * @return token
     */
    public AuthenticationToken getAuthenticationToken();
}
