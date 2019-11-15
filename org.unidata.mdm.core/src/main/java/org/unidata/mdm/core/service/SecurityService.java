package org.unidata.mdm.core.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.unidata.mdm.core.type.security.AuthenticationSystemParameter;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.core.type.security.User;

/**
 * @author Mikhail Mikhailov
 * Security stuff, visible everywhere.
 */
public interface SecurityService {

    /**
     * Check login and password and if they are correct generate token and return it back.
     * TODO refactor this method to accept standard AuthenticationRequestContext.
     *
     * @param params login parameters
     *
     * @return the security token.
     */
    SecurityToken login(Map<AuthenticationSystemParameter, Object> params);
    /**
     * Logout.
     * TODO refactor this method to accept standard AuthenticationRequestContext.
     *
     * @param tokenString the token string
     * @param params addition params
     * @return true, if successful
     */
    boolean logout(String tokenString, Map<AuthenticationSystemParameter, Object> params);
    /**
     * Verifies token and sets authentication context up, if successful.
     * @param token the token string
     * @param prolongTTL whether to prolong token TTL or not
     * @return true if successful, false otherwise
     */
    boolean authenticate(String token, boolean prolongTTL);
    /**
     * Gets the token ttl.
     *
     * @return the tokenTTL
     */
    long getTokenTTL();
    /**
     * Sets the password.
     *
     * @param password    the password
     * @param oldPassword the old password
     * @return true, if successful
     */
    boolean updatePassword(String password, String oldPassword);

    /**
     * Validate token. This is called by Spring via authentication provider.
     * The chain starts  at {@link #authenticate(String, boolean)}.
     *
     * @param tokenString the token string
     * @param prolongTTL prolong token TTL or not.
     * @return true, if successful
     */
    boolean validateAndProlongToken(String tokenString, boolean prolongTTL);
    /**
     * Gets the user by token.
     *
     * @param tokenString the token string
     * @return the user by token
     */
    User getUserByToken(String tokenString);
    /**
     * Gets security token by token string.
     * @param tokenString the string
     * @return token object or null
     */
    SecurityToken getTokenObjectByToken(String tokenString);
    /**
     * Gets the roles by token.
     *
     * @param tokenString the token string
     * @return the roles by token
     */
    List<Right> getRightsByToken(String tokenString);
    /**
     * Logout user by name.
     *
     * @param userName the user name
     */
    void logoutUserByName(String userName);
    /**
     * Logout all currently logged in users that attached to the provided role.
     *
     * @param roleName Role name.
     */
    void logoutByRoleName(String roleName);
    /**
     * Gets current user's name.
     * @return name
     */
    String getCurrentUserName();
    /**
     * Gets current user token.
     * @return token
     */
    String getCurrentUserToken();
    /**
     * Gets current user's storage id.
     * @return storage id
     */
    String getCurrentUserStorageId();
    /**
     * Check whether the user with name is an administrator.
     */
    boolean isAdminUser(String login);
    /**
     *
     * @param login
     * @return
     */
    String getOrCreateInnerTokenByLogin(String login);
    /**
     * Change user locale for a token
     * @param login user login
     * @param locale new locale
     */
    void changeLocale(String login, Locale locale);
    /**
     * Adds role names to all active real user tokens.
     * @param roleNames the role names
     */
    void updateUserTokensWithRole(List<String> roleNames);
}
