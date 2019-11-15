package org.unidata.mdm.core.type.security;

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
