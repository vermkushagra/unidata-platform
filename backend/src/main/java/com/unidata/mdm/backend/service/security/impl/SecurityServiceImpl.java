package com.unidata.mdm.backend.service.security.impl;

import static com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter.PARAM_ENDPOINT;
import static com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter.PARAM_USER_NAME;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.common.context.AuthenticationRequestContext;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationProvider;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceType;
import com.unidata.mdm.backend.common.integration.auth.SecurityDataProviderException;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.security.Endpoint;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.dao.UserDao;
import com.unidata.mdm.backend.dto.storage.UserInfo;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.security.RoleServiceExt;
import com.unidata.mdm.backend.service.security.SecurityDataSource;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.UserService;
import com.unidata.mdm.backend.service.security.utils.BearerToken;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;

/**
 * Service contains methods for authentication and authorization. User and role
 * management in different one.
 *
 * @author ilya.bykov
 */
@Component("securityService")
public class SecurityServiceImpl implements SecurityServiceExt {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceImpl.class);
    /**
     * Event writer
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;
    /**
     * User service. Contains methods for user management.
     */
    @Autowired
    private UserService userService;

    /**
     * Role service. Contains methods for role management.
     */
    @Autowired
    private RoleServiceExt roleServiceExt;
    /**
     * The token cache.
     */
    private IMap<String, SecurityToken> tokenCache;
    /**
     * Bogus operation for timestamp renewal.
     */
    private TokenTimestampRefresher tokenRefresher = new TokenTimestampRefresher();
    /**
     * HZ cluster name, configured via backend.properties.
     */
    @Value(value = "${unidata.common.cache.cluster:unidata}")
    private String clusterName;
    /**
     * Name of the map, configured via backend.properties.
     */
    @Value(value = "${unidata.common.cache.cluster.security.map:tokens}")
    private String mapName;
    /**
     * HZ instance.
     */
    @Autowired
    private HazelcastInstance cache;
    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;
    /**
     * Authentication manager.
     */
    @Autowired(required = false)
    @Qualifier(value = "authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDao userDao;
    /**
     * Token time to live. By default set to 30 minutes.
     */
    @Value(value = "${unidata.security.token.ttl:1800}")
    private long tokenTTL;
    /**
     * Password TTL. By default set to 30 days.
     */
    @Value(value = "${unidata.security.password.expiration:2592000}")
    private long passwordTTL;

    /**
     * Login procedure, v3.
     *
     * @return new token or null in case of failure
     */
    @Override
    public SecurityToken login(Map<AuthenticationSystemParameter, Object> params) {

        MeasurementPoint.start();
        try {

            if (!preValidate(params)) {
                final String message = "Invalid input. Pre-validate failed.";
                LOGGER.warn(message);
                throw new BusinessException("Login or password not valid!", ExceptionId.EX_SECURITY_CANNOT_LOGIN);
            }

            // 1. Load existing, if login was supplied
            UserWithPasswordDTO existing = null;
            if (!Objects.isNull(params.get(PARAM_USER_NAME))) {
                existing = userService.getUserByName(params.get(PARAM_USER_NAME).toString());
            }

            User result;
            // UC 1. Try all registered external SDS, if existing user is null
            // This is the case for external users, which may even have no login or password,
            // but cookies or similar mechanics for authentication instead.
            // A user will be created in UD though.
            if (existing == null || existing.isExternal()) {
                result = processLogin(params);
                // UC 2. Authentication with login name. This includes two sub use cases:
                // - external user, authenticated elsewhere, but still using authorization and / or profile in UD
                // - internal user managed in UD completely.
            } else {
                String dataSourceName = !existing.isExternal()
                        ? SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE
                        : existing.getSecurityDataSource();

                SecurityDataSource source = configurationService.getSecurityDataSources().get(dataSourceName);
                if (Objects.isNull(source)) {
                    LOGGER.warn("Trying to authenticate user '{}' with not existing SDS '{}'. Aborting.",
                            existing.getLogin(), dataSourceName);
                    return null;
                }

                result = processLogin(params, source, true);
            }

            if (Objects.isNull(result)) {
                throw new BusinessException("Login or password not valid!", ExceptionId.EX_SECURITY_CANNOT_LOGIN);
            }
            if (!isCorrectEndpoint(result, params)) {
                throw new BusinessException("No rights for endpoint!", ExceptionId.EX_SECURITY_USER_HAVE_NO_RIGHTS_FOR_ENDPOINT);
            }
            // 3. Enrich user object and create token
            SecurityToken token = createToken(result, params);

            userService.insertToken(token);
            tokenCache.set(token.getToken(), token);

            params.put(AuthenticationSystemParameter.PARAM_USER_TOKEN, token.getToken());
            auditEventsWriter.writeSuccessEvent(AuditActions.LOGIN, params);

            return token;
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.LOGIN, e, params);
            throw e;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Determines is user have rights to access this endpoint.
     *
     * @param result result
     * @param params param map.
     * @return is user have rights to access this endpoint.
     */
    private boolean isCorrectEndpoint(User result, Map<AuthenticationSystemParameter, Object> params) {
        if (result == null || result.getEndpoints() == null) {
            return false;
        }
        return result.getEndpoints().stream().map(com.unidata.mdm.backend.common.integration.auth.Endpoint::getName)
                .anyMatch(el ->
                        StringUtils.equals(
                                el,
                                ((Endpoint) params.get(AuthenticationSystemParameter.PARAM_ENDPOINT)).name()
                        )
                );
    }

    /**
     * Go thru all security data sources and try to authenticate with any authentication provider.
     *
     * @param params authentication params
     * @return user or null
     */
    private User processLogin(Map<AuthenticationSystemParameter, Object> params) {

        for (Entry<String, SecurityDataSource> entry
                : configurationService.getSecurityDataSources().entrySet()) {
            User result = processLogin(params, entry.getValue(), false);
            if (Objects.isNull(result)) {
                continue;
            }

            result.setSecurityDataSource(entry.getKey());

            try {
                userService.verifyAndUpserExternalUser(result);
            } catch (Exception exc) {
                LOGGER.warn("Cannot verify and save external user.", exc);
                return null;
            }

            return result;
        }

        return null;
    }

    /**
     * Tries to authenticate / authorize / get profile, using the supplied security data source and given credentials.
     *
     * @param params auth params
     * @param sds             security data source
     * @param existingProfile existing profile or not
     * @return user or null
     */
    private User processLogin(Map<AuthenticationSystemParameter, Object> params, SecurityDataSource sds, boolean existingProfile) {

        try {

            final AuthenticationProvider authProvider = sds.getAuthenticationProvider();
            if (Objects.isNull(authProvider)) {
                return null;
            }

            User authUser = authProvider.login(AuthenticationRequestContext.of(params));
            if (authUser != null) {

                processAuthorization(authUser, sds, existingProfile);
                processProfile(authUser, sds, existingProfile);
            }

            return authUser;
        } catch (final SecurityDataProviderException sdpe) {
            LOGGER.warn("Unable to authenticate against security provider '" + sds.getName() + "'", sdpe);
        }

        return null;
    }

    /**
     * Calls authorization part.
     *
     * @param authUser        user
     * @param sds             source
     * @param existingProfile existing profile
     */
    private void processAuthorization(User authUser, SecurityDataSource sds, boolean existingProfile) {

        boolean runSelectedAuthorize
                = !authUser.hasAuthorization() && !Objects.isNull(sds.getAuthorizationProvider());
        boolean runSystemAuthorize
                = !authUser.hasAuthorization()
                && Objects.isNull(sds.getAuthorizationProvider())
                && existingProfile;
        if (runSelectedAuthorize) {
            sds.getAuthorizationProvider().authorize(authUser);
        } else if (runSystemAuthorize) {
            configurationService.getSystemSecurityDataSource().getAuthorizationProvider().authorize(authUser);
        }
    }

    /**
     * Calls profile part.
     *
     * @param authUser        user
     * @param sds             source
     * @param existingProfile existing profile
     */
    private void processProfile(User authUser, SecurityDataSource sds, boolean existingProfile) {

        boolean runSelectedProfile
                = !authUser.hasProfile() && !Objects.isNull(sds.getProfileProvider());
        boolean runSystemProfile
                = !authUser.hasProfile() && Objects.isNull(sds.getProfileProvider()) && existingProfile;
        if (runSelectedProfile) {
            sds.getProfileProvider().load(authUser);
        } else if (runSystemProfile) {
            configurationService.getSystemSecurityDataSource().getProfileProvider().load(authUser);
        }
    }

    /**
     * Fill security token with data. Enrich user object, if needed.
     *
     * @return the security token
     */
    private SecurityToken createToken(final User user, Map<AuthenticationSystemParameter, Object> params) {

        // 1. Create token and set necessary fields.
        final SecurityToken token = new SecurityToken();
        if (!StringUtils.isBlank(user.getCustomToken())) {
            token.setToken(user.getCustomToken());
        } else {
            token.setToken(generateToken());
        }

        token.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        token.setUser(user);

        // 2. Extract stuff, which may have been supplied by authorization
        List<Role> roles = user.getRoles() == null ? Collections.emptyList() : user.getRoles();
        List<Right> rights = user.getRights() == null ? Collections.emptyList() : user.getRights();
        List<SecurityLabel> userLabels = CollectionUtils.isEmpty(user.getLabels()) ? Collections.emptyList() : user.getLabels();
        List<SecurityLabel> rolesLabels =
                CollectionUtils.isEmpty(user.getRoles()) ?
                        Collections.emptyList() :
                        user.getRoles().stream()
                                .filter(r -> !CollectionUtils.isEmpty(r.getSecurityLabels()))
                                .flatMap(r -> r.getSecurityLabels().stream())
                                .collect(Collectors.toList());

        // 3. Create maps from roles
        final Map<String, Right> rightsMap = SecurityUtils.createRightsMap(roles);
        final Map<String, Role> rolesMap = SecurityUtils.createRolesMap(roles);

        // 4. Overwrite calculated rights with manually supplied one
        Map<String, Right> overwriteRights = SecurityUtils.extractRightsMap(rights);
        overwriteRights.forEach(rightsMap::put);

        token.getRightsMap().putAll(rightsMap);
        token.getRolesMap().putAll(rolesMap);
        token.getLabelsMap().putAll(
                SecurityUtils.extractLabelsMap(
                        SecurityUtils.mergeSecurityLabels(userLabels, rolesLabels)
                )
        );

        token.setEndpoint((Endpoint) params.get(PARAM_ENDPOINT));

        return token;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#logout(java.lang.String)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logout(String tokenString, Map<AuthenticationSystemParameter, Object> params) {

        params.put(AuthenticationSystemParameter.PARAM_USER_TOKEN, tokenString);
        try {

            boolean isTokenValid = tokenCache.containsKey(tokenString);
            if (isTokenValid) {
                tokenCache.delete(tokenString);
                auditEventsWriter.writeSuccessEvent(AuditActions.LOGOUT, params);
            }

            return isTokenValid;
        } catch (Exception e) {
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.LOGOUT, e, params);
            throw e;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authenticate(String token, boolean prolongTTL) {
        // Will call #validateToken and #getTokenByTokenString via delegation
        // This method is about to hide BearerToken type and auth context setting
        Authentication authentication = authenticationManager == null
                ? null
                : authenticationManager.authenticate(new BearerToken(token, prolongTTL));
        boolean isAuthenticated = authentication != null;
        if (isAuthenticated) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        return isAuthenticated;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#updatePassword(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(String user, String password, String oldPassword) {

        final UserWithPasswordDTO oldUser = userService.getUserByName(user);
        if (oldPassword != null) {
            UserInfo userObj = new UserInfo();
            userObj.setPassword(oldPassword);
            userObj.setLogin(user);
            if (!validateCredentials(userObj, oldUser)) {
                return false;
            }
        }

        oldUser.setPassword(password);
        userService.updateUser(user, oldUser);
        return true;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#validateToken(java.lang.String, boolean)
     */
    @Override
    public boolean validateAndProlongToken(String tokenString, boolean prolongTTL) {

        SecurityToken token = tokenCache.get(tokenString);

        // 1. No such object exist
        if (Objects.isNull(token)) {
            return false;
        }

        // 2. Some failure caused data corruption / inconsistency
        String existingToken = token.getToken();
        boolean isValid = StringUtils.isNotBlank(existingToken) && StringUtils.equals(tokenString, existingToken);

        // 3. Submit bogus get operation to master partition, to cause access time renewal,
        // since near cache gets do not renew access times.
        if (isValid && prolongTTL) {
            tokenCache.submitToKey(tokenString, tokenRefresher, null);
        }

        return isValid;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#getUserByToken(java.lang.String)
     */
    @Override
    public User getUserByToken(String tokenString) {
        SecurityToken securityToken = tokenCache.get(tokenString);
        return securityToken != null ? securityToken.getUser() : null;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#getTokenByTokenString(java.lang.String)
     */
    @Override
    public SecurityToken getTokenObjectByToken(String tokenString) {
        return tokenCache.get(tokenString);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#getRightsByToken(java.lang.String)
     */
    @Override
    public List<Right> getRightsByToken(final String tokenString) {
        SecurityToken token = tokenCache.get(tokenString);
        return token == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(token.getRightsMap().values()));
    }

    /**
     * Mostly a placeholder for pre-validating logic, which may be suitable in the future.
     *
     * @param params auth params
     * @return
     */
    private boolean preValidate(Map<AuthenticationSystemParameter, Object> params) {
        return MapUtils.isNotEmpty(params);
    }

    /**
     * Validate credentials.
     *
     * @param toCheck the user
     * @param fromDB  DB object
     * @return true, if successful
     * @throws Exception the exception
     */
    private boolean validateCredentials(UserInfo toCheck, UserWithPasswordDTO fromDB) {

        if (Objects.isNull(toCheck) || Objects.isNull(fromDB)) {
            return false;
        }

        toCheck.setPasswordUpdatedAt(fromDB.getPasswordLastChangedAt());
        toCheck.setPasswordUpdatedBy(fromDB.getPasswordUpdatedBy());
        toCheck.setCreatedAt(fromDB.getCreatedAt());
        toCheck.setAdmin(fromDB.isAdmin());
        toCheck.setEmail(fromDB.getEmail());
        toCheck.setName(fromDB.getFullName());
        toCheck.setSecurityDataSource(fromDB.getSecurityDataSource());

        return toCheck.getPassword() != null
                && fromDB.getPassword() != null
                && fromDB.isActive()
                && BCrypt.checkpw(toCheck.getPassword(), fromDB.getPassword());
    }

    /**
     * Generate token.
     *
     * @return the string
     */
    private static String generateToken() {
        return IdUtils.v4String();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#getTokenTTL()
     */
    @Override
    public long getTokenTTL() {
        return this.tokenTTL;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#setTokenTTL(long)
     */
    @Override
    public void setTokenTTL(long tokenTTL) {
        this.tokenTTL = tokenTTL;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#getClusterName()
     */
    @Override
    public String getClusterName() {
        return clusterName;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#setClusterName(java.lang.String)
     */
    @Override
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#getMapName()
     */
    @Override
    public String getMapName() {
        return mapName;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#setMapName(java.lang.String)
     */
    @Override
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#afterContextRefresh()
     */
    @Override
    public void afterContextRefresh() {
        this.tokenCache = cache.getMap(getMapName());
        this.tokenCache.addEntryListener(new TokenListener(auditEventsWriter, userDao), true);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#logoutUserByName(java.lang.String)
     */
    @Override
    public void logoutUserByName(String userName) {
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, userName);
        params.put(AuthenticationSystemParameter.PARAM_DETAILS, "Автоматический выход из-за обновление настроек пользователя");
        tokenCache.entrySet()
                .stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.getValue() != null)
                .filter(entity -> entity.getValue().getUser() != null)
                .filter(entity -> StringUtils.equals(entity.getValue().getUser().getLogin(), userName))
                .forEach(entity -> this.logout(entity.getKey(), params));
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#logoutByRoleName(java.lang.String)
     */
    @Override
    public void logoutByRoleName(String roleName) {
        tokenCache.entrySet()
                .stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.getValue() != null)
                .filter(entity -> entity.getValue().getUser() != null)
                .filter(entity -> roleServiceExt.isUserInRole(entity.getValue().getUser().getLogin(), roleName))
                .forEach(this::forceLogout);
    }

    private void forceLogout(Entry<String, SecurityToken> entity) {
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        params.put(AuthenticationSystemParameter.PARAM_DETAILS,
                "Автоматический выход из-за обновление ролей пользователя");
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, entity.getValue().getUser().getLogin());
        this.logout(entity.getKey(), params);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#createResourceFromLookup(java.util.List)
     */
    @Override
    public void createResourceFromLookup(List<LookupEntityDef> lookupEntityUpdate) {
        if (CollectionUtils.isEmpty(lookupEntityUpdate)) {
            return;
        }
        List<SecuredResourceDTO> resources = new ArrayList<>();
        for (LookupEntityDef def : lookupEntityUpdate) {

            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(def, Collections.emptyList());
            SecuredResourceDTO resource = createResources(def.getName(), def.getDisplayName(),
                    SecuredResourceCategory.META_MODEL, attrs);
            resources.add(resource);
        }

        roleServiceExt.createResources(resources);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#createResourceFromEntity(java.util.List)
     */
    @Override
    public void createResourceFromEntity(List<EntityDef> entityUpdate, List<NestedEntityDef> refs) {
        if (CollectionUtils.isEmpty(entityUpdate)) {
            return;
        }
        List<SecuredResourceDTO> resources = new ArrayList<>();
        for (EntityDef def : entityUpdate) {

            Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(def, refs);
            SecuredResourceDTO resource = createResources(def.getName(), def.getDisplayName(),
                    SecuredResourceCategory.META_MODEL, attrs);
            resources.add(resource);
        }

        roleServiceExt.createResources(resources);
    }

    @Override
    public void createResourceFromEntity(AbstractEntityDef entityDef, Map<String, AttributeInfoHolder> attrs) {
        SecuredResourceDTO resource = createResources(entityDef.getName(), entityDef.getDisplayName(),
                SecuredResourceCategory.META_MODEL, attrs);
        roleServiceExt.createResources(Collections.singletonList(resource));
    }

    @Override
    public void dropAttributeResourceFromEntity(AbstractEntityDef entityDef, Map<String, AttributeInfoHolder> deletedAttrs) {
        List<String> deleteResources = deletedAttrs.values()
                .stream()
                .map( attr -> String.join(".", entityDef.getName(), attr.getPath()))
                .collect(Collectors.toList());
            deleteResources(deleteResources);
    }

    /**
     * TODO move to own service.
     * Creates a resource.
     *
     * @param holder       the name of the attribute
     * @param topLevelName name of the top level object
     * @param parent       parent
     * @return {@link SecuredResourceDTO}
     */
    private SecuredResourceDTO createResource(AttributeInfoHolder holder, String topLevelName, SecuredResourceDTO parent) {

        SecuredResourceDTO resource = new SecuredResourceDTO();
        resource.setName(String.join(".", topLevelName, holder.getPath()));
        resource.setDisplayName(holder.getAttribute().getDisplayName());
        resource.setParent(parent);
        resource.setCreatedAt(new Date());
        resource.setCreatedBy(SecurityUtils.getCurrentUserName());
        resource.setType(SecuredResourceType.USER_DEFINED);
        resource.setCategory(parent.getCategory());
        resource.setUpdatedAt(new Date());
        resource.setUpdatedBy(SecurityUtils.getCurrentUserName());

        if (holder.hasChildren()) {
            List<SecuredResourceDTO> children = new ArrayList<>();
            for (AttributeInfoHolder child : holder.getChildren()) {
                children.add(createResource(child, topLevelName, resource));
            }

            resource.setChildren(children);
        }

        return resource;
    }

    /**
     * Create resource
     *
     * @param name        - name of resource
     * @param displayName - display name of resource
     * @param category    the category to set
     * @param attrs       attributes
     * @return resource
     */
    private SecuredResourceDTO createResources(String name, String displayName, SecuredResourceCategory category,
                                               Map<String, AttributeInfoHolder> attrs) {

        // 1. Top level object
        SecuredResourceDTO resource = new SecuredResourceDTO();
        resource.setName(name);
        resource.setDisplayName(displayName);
        resource.setCreatedAt(new Date());
        resource.setCreatedBy(SecurityUtils.getCurrentUserName());
        resource.setType(SecuredResourceType.USER_DEFINED);
        resource.setCategory(category);
        resource.setUpdatedAt(new Date());
        resource.setUpdatedBy(SecurityUtils.getCurrentUserName());

        // 2. Attributes. Only top level are processed
        List<SecuredResourceDTO> children = new ArrayList<>();
        for (Entry<String, AttributeInfoHolder> entry : attrs.entrySet()) {

            if (entry.getValue().hasParent()) {
                continue;
            }

            children.add(createResource(entry.getValue(), name, resource));
        }


        resource.setChildren(children);
        return resource;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#createResourceForClassifier(java.lang.String, java.lang.String)
     */
    @Override
    public void createResourceForClassifier(String name, String displayName) {
        SecuredResourceDTO resource = createResources(name, displayName,
                SecuredResourceCategory.CLASSIFIER, Collections.emptyMap());
        roleServiceExt.createResources(Collections.singletonList(resource));
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#deleteResources(java.util.List)
     */
    @Override
    public void deleteResources(List<String> resources) {

        if (CollectionUtils.isEmpty(resources)) {
            return;
        }

        for (String resource : resources) {
            roleServiceExt.deleteResource(resource);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropAllResources() {
        dropResources(SecuredResourceCategory.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropResources(SecuredResourceCategory... categories) {

        if (ArrayUtils.isEmpty(categories)) {
            return;
        }

        roleServiceExt.dropResources(categories);
    }

    @Override
    public boolean isAdminUser(String login) {
        return userService.isAdminUser(login);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentUserName() {
        return SecurityUtils.getCurrentUserName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentUserToken() {
        return SecurityUtils.getCurrentUserToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentUserStorageId() {
        return SecurityUtils.getCurrentUserStorageId();
    }
}
