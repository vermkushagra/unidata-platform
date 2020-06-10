package com.unidata.mdm.backend.common.integration.auth;

/**
 * @author Denis Kostovarov
 */
public interface AuthorizationProvider {
    /**
     * Calls authorization. procedure.
     * @param user the user
     */
    void authorize(User user);
}
