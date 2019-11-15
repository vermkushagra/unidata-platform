package org.unidata.mdm.core.type.security;

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
