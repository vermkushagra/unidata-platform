package com.unidata.mdm.backend.common.integration.auth;

/**
 * @author Denis Kostovarov
 */
public interface ProfileProvider {
    /**
     * Adds profile information to the user object.
     * @param user the user object
     */
    void load(User user);
}
