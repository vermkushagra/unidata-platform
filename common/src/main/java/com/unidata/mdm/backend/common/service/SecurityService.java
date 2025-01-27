package com.unidata.mdm.backend.common.service;

import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.context.AuthenticationRequestContext;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;

/**
 * @author Mikhail Mikhailov
 * Security stuff, visible everywhere.
 */
public interface SecurityService {

    /**
     * Check login and password and if they are correct generate token and return it back.
     * TODO refactor this method to accept standard {@linkplain AuthenticationRequestContext}.
     *
     * @param params login parameters
     *
     * @return the security token.
     * @throws Exception the exception.
     */
    SecurityToken login(Map<AuthenticationSystemParameter, Object> params);

    /**
     * Logout.
     * TODO refactor this method to accept standard {@linkplain AuthenticationRequestContext}.
     *
     * @param tokenString the token string
     * @param params addition params
     * @return true, if successful
     * @throws Exception the exception
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
     * Sets the password.
     *
     * @param user        the user
     * @param password    the password
     * @param oldPassword the old password
     * @return true, if successful
     * @throws Exception the exception
     */
    boolean updatePassword(String user, String password, String oldPassword);

    /**
     * Validate token. This is called by Spring via authentication provider.
     * The chain starts  at {@link #authenticate(String, boolean)}.
     *
     * @param tokenString the token string
     * @param prolongTTL prolong token TTL or not.
     * @return true, if successful
     * @throws Exception the exception
     */
    boolean validateAndProlongToken(String tokenString, boolean prolongTTL);

    /**
     * Gets the user by token.
     *
     * @param tokenString the token string
     * @return the user by token
     * @throws Exception the exception
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
     * @throws Exception the exception
     */
    List<Right> getRightsByToken(String tokenString);

    /**
     * Logout user by name.
     *
     * @param userName the user name
     * @throws Exception the exception
     */
    void logoutUserByName(String userName);

    /**
     * Logout all currently logged in users that attached to the provided role.
     *
     * @param roleName Role name.
     * @throws Exception In case if any exception occurs.
     */
    void logoutByRoleName(String roleName);

    /**
     * Create secured resources from the list of lookup entities.
     *
     * @param lookupEntityUpdate list with lookup entities.
     */
    void createResourceFromLookup(List<LookupEntityDef> lookupEntityUpdate);

    /**
     * Create secured resources from the list of  entities.
     *
     * @param entityUpdate list with  entities.
     * @param refs nested references
     */
    void createResourceFromEntity(List<EntityDef> entityUpdate, List<NestedEntityDef> refs);

    /**
     * @param name        - name of classifier
     * @param displayName - display name of classifier
     */
    void createResourceForClassifier(String name, String displayName);

    /**
     * Create resources for entity and attributes
     * @param entityDef  entity definition
     * @param attrs attribute map for create
     */
    void createResourceFromEntity(AbstractEntityDef entityDef, Map<String, AttributeInfoHolder> attrs);

    /**
     * drop attribute resources for entity
     * @param entityDef entity definition
     * @param deletedAttrs attribute map for remove
     */
    void dropAttributeResourceFromEntity(AbstractEntityDef entityDef, Map<String, AttributeInfoHolder> deletedAttrs);

    /**
     * Delete resource by names.
     *
     * @param resources list with resource names.
     */
    void deleteResources(List<String> resources);

    /**
     * Drop all security resources.
     */
    void dropAllResources();

    /**
     * Drop all security resources of the specified category.
     */
    void dropResources(SecuredResourceCategory... category);
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

}
