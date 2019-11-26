package org.unidata.mdm.core.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.context.AuthenticationRequestContext;
import org.unidata.mdm.core.dao.UserDao;
import org.unidata.mdm.core.dto.UserWithPasswordDTO;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.service.AuditService;
import org.unidata.mdm.core.service.PasswordPolicyService;
import org.unidata.mdm.core.service.RoleService;
import org.unidata.mdm.core.service.SecurityConfigurationService;
import org.unidata.mdm.core.service.UserService;
import org.unidata.mdm.core.type.security.AuthenticationProvider;
import org.unidata.mdm.core.type.security.AuthenticationSystemParameter;
import org.unidata.mdm.core.type.security.EndpointType;
import org.unidata.mdm.core.type.security.Right;
import org.unidata.mdm.core.type.security.Role;
import org.unidata.mdm.core.type.security.SecurityLabel;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.core.type.security.User;
import org.unidata.mdm.core.type.security.impl.BearerToken;
import org.unidata.mdm.core.type.security.impl.SecurityDataSource;
import org.unidata.mdm.core.type.security.impl.UserInfo;
import org.unidata.mdm.core.audit.AuditConstants;
import org.unidata.mdm.core.audit.SecurityAuditConstants;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.core.util.TransactionUtils;
import org.unidata.mdm.system.exception.PlatformBusinessException;
import org.unidata.mdm.system.exception.PlatformRuntimeException;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;
import org.unidata.mdm.system.util.IdUtils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import javax.annotation.PostConstruct;

/**
 * Service contains methods for authentication and authorization. User and role
 * management in different one.
 *
 * @author ilya.bykov
 */
@Service("securityService")
public class SecurityServiceImpl implements SecurityServiceExt {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceImpl.class);
    private static final String LOGIN_AUDIT_EVENT_PARAMETER = "login";
    // ODO: Commented out in scope of UN-11834. Refactor this notification API!
    /*
    @Autowired
    private UserNotificationService userNotificationService;
    */

    @Autowired
    private AuditService auditService;

    /**
     * User service. Contains methods for user management.
     */
    @Autowired
    private UserService userService;
    /**
     * Role service. Contains methods for role management.
     */
    @Autowired
    private RoleService roleService;
    /**
     * Password policy
     */
    @Autowired
    private PasswordPolicyService passwordPolicy;
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
    private SecurityConfigurationService securityConfigurationService;

    /**
     * Authentication manager.
     */
    @Autowired
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
     * get or create inner token without endpoints
     *
     * @return token
     */
    @Override
    public String getOrCreateInnerTokenByLogin(String login) {
        SecurityToken token = tokenCache.values().stream()
                .filter(Objects::nonNull)
                .filter(t -> t.isInner() && login.equals(t.getUser().getLogin()))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(token)) {
            UserWithPasswordDTO dto = userService.getUserByName(login);

            if (Objects.isNull(dto) || !dto.isActive()) {
                return null;
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setLogin(login);
            userInfo.setRights(dto.getRights());
            userInfo.setRoles(dto.getRoles());
            userInfo.setLabels(dto.getSecurityLabels());
            userInfo.setAdmin(dto.isAdmin());

            Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
            params.put(AuthenticationSystemParameter.PARAM_USER_NAME, login);

            token = createToken(userInfo, params, true);
            tokenCache.put(token.getToken(), token);
        }

        return token.getToken();
    }

    @Override
    public void changeLocale(String login, Locale locale) {
        List<SecurityToken> tokens = tokenCache.values().stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.getUser() != null)
                .filter(t -> login.equals(t.getUser().getLogin()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(tokens)) {
            return;
        }

        for (SecurityToken token : tokens) {
            if (token.getUser() != null) {
                token.getUser().setLocale(locale);
                tokenCache.put(token.getToken(), token);
            }
        }

    }

    @Override
    public void updateInnerToken(String login) {
        SecurityToken token = tokenCache.values().stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.getUser() != null)
                .filter(t -> t.isInner() && login.equals(t.getUser().getLogin()))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(token)) {
            return;
        }
        updateInnerToken(token);
    }

    private void updateInnerToken(SecurityToken token) {
        if (!token.isInner()) {
            return;
        }
        refreshToken(token);
    }

    private void refreshToken(SecurityToken token) {
        UserWithPasswordDTO dto = userService.getUserByName(token.getUser().getLogin());
        if (Objects.isNull(dto)) {
            return;
        }

        if (!dto.isActive()) {
            tokenCache.delete(token.getToken());
            return;
        }

        setTokenAuthority(token, dto.getRoles(), dto.getRights(), dto.getSecurityLabels());
        // refresh roles manually
        token.getUser().setRoles(dto.getRoles());

        tokenCache.put(token.getToken(), token);
    }

    private void updateToken(SecurityToken token) {

        if (token.isInner()) {
            return;
        }

        refreshToken(token);
    }

    @Override
    public void updateInnerTokensWithRole(String roleName) {
        List<SecurityToken> toUpdate = tokenCache.values().stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.getUser() != null)
                .filter(t ->
                        t.isInner() &&
                                t.getRolesMap().values().stream()
                                        .anyMatch(role -> role.getName().equals(roleName))
                )
                .collect(Collectors.toList());


        toUpdate.forEach(this::updateInnerToken);
    }

    @Override
    public void updateUserTokensWithRole(List<String> roleNames) {
        List<SecurityToken> toUpdate = tokenCache.values().stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.getUser() != null)
                .filter(t ->
                        roleNames.stream().anyMatch(roleName -> t.getRolesMap().get(roleName) != null)
                )
                .collect(Collectors.toList());


        toUpdate.forEach(this::updateToken);
    }

    /**
     * Login procedure, v3.
     *
     * @return new token or null in case of failure
     */
    @Override
    public SecurityToken login(Map<AuthenticationSystemParameter, Object> params) {

        final Map<String, Object> auditParams = new HashMap<>();
        auditParams.put(LOGIN_AUDIT_EVENT_PARAMETER, params.get(AuthenticationSystemParameter.PARAM_USER_NAME));
        auditParams.put(AuditConstants.CLIENT_IP_FIELD, params.get(AuthenticationSystemParameter.PARAM_CLIENT_IP));
        auditParams.put(AuditConstants.SERVER_IP_FIELD, params.get(AuthenticationSystemParameter.PARAM_SERVER_IP));
        auditParams.put(AuditConstants.ENDPOINT_FIELD, params.get(AuthenticationSystemParameter.PARAM_ENDPOINT));

        MeasurementPoint.start();
        try {

            if (!preValidate(params)) {
                final String message = "Invalid input. Pre-validate failed.";
                LOGGER.warn(message);
                throwLoginOrPasswordNotValid();
            }

            // 1. Load existing, if login was supplied
            UserWithPasswordDTO existing = null;
            if (!Objects.isNull(params.get(AuthenticationSystemParameter.PARAM_USER_NAME))) {
                existing = userService.getUserByName(params.get(AuthenticationSystemParameter.PARAM_USER_NAME).toString());
            }

            User result;
            String dataSourceName = null;
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
                dataSourceName = !existing.isExternal()
                        ? SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE
                        : existing.getSecurityDataSource();

                // TODO: Commented out in scope of UN-11834. Reenable ASAP.
                SecurityDataSource source = securityConfigurationService.getSecurityDataSources().get(dataSourceName);
                if (Objects.isNull(source)) {
                    LOGGER.warn("Trying to authenticate user '{}' with not existing SDS '{}'. Aborting.",
                            existing.getLogin(), dataSourceName);
                    return null;
                }

                result = processLogin(params, source, true);
            }

            if (Objects.isNull(result)) {
                throwLoginOrPasswordNotValid();
            }
            if (!isCorrectEndpoint(result, params)) {
                throw new PlatformBusinessException("No rights for endpoint!", CoreExceptionIds.EX_SECURITY_USER_HAS_NO_RIGHTS_FOR_ENDPOINT);
            }

            if (SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE.equals(dataSourceName)
                    && result.getPasswordUpdatedAt() != null
                    && passwordPolicy.isExpired(result.getPasswordUpdatedAt().toInstant(), result.isAdmin())
                    && !result.getForcePasswordChangeFlag()) {
                throw new PlatformBusinessException("User password expired!", CoreExceptionIds.EX_SECURITY_USER_PASSWORD_EXPIRED);
            }

            // 3. Enrich user object and create token
            SecurityToken token = createToken(result, params, false);

            userService.insertToken(token);
            tokenCache.set(token.getToken(), token);

            params.put(AuthenticationSystemParameter.PARAM_USER_TOKEN, token.getToken());
            // TODO Fix audit event
            auditService.writeEvent(SecurityAuditConstants.LOGIN_EVENT_TYPE, auditParams);
            /*
            userNotificationService.onLogin(result, (String) params.get(AuthenticationSystemParameter.PARAM_USER_LOCALE));
            */
            return token;
        } catch (Exception e) {
            auditParams.put(AuditConstants.EXCEPTION_FIELD, e);
            auditService.writeEvent(SecurityAuditConstants.LOGIN_EVENT_TYPE, auditParams);
            throw e;
        } finally {
            MeasurementPoint.stop();
        }
    }

    private void throwLoginOrPasswordNotValid() {
        throw new PlatformBusinessException("Login or password not valid!", CoreExceptionIds.EX_SECURITY_CANNOT_LOGIN);
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
        return result.getEndpoints().stream().map(org.unidata.mdm.core.type.security.Endpoint::getName)
                .anyMatch(el ->
                        StringUtils.equals(
                                el,
                                ((EndpointType) params.get(AuthenticationSystemParameter.PARAM_ENDPOINT)).name()
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

        // TODO: Commented out in scope of UN-11834. Reenable ASAP.
        for (Entry<String, SecurityDataSource> entry
                : Collections.<String, SecurityDataSource>emptyMap().entrySet() /* configurationService.getSecurityDataSources().entrySet() */ ) {
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
     * @param sds security data source
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
        } catch (final PlatformRuntimeException sdpe) {
            LOGGER.warn("Unable to authenticate against security provider '" + sds.getName() + "'", sdpe);
        }

        return null;
    }

    /**
     * Calls authorization part.
     *
     * @param authUser user
     * @param sds source
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
            // TODO: Commented out in scope of UN-11834. Reenable ASAP.
            // configurationService.getSystemSecurityDataSource().getAuthorizationProvider().authorize(authUser);
        }
    }

    /**
     * Calls profile part.
     *
     * @param authUser user
     * @param sds source
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
            // TODO: Commented out in scope of UN-11834. Reenable ASAP.
            // configurationService.getSystemSecurityDataSource().getProfileProvider().load(authUser);
        }
    }

    /**
     * Fill security token with data. Enrich user object, if needed.
     *
     * @return the security token
     */
    private SecurityToken createToken(final User user, Map<AuthenticationSystemParameter, Object> params, boolean isInnerToken) {

        // 1. Create token and set necessary fields.
        final SecurityToken token = new SecurityToken(isInnerToken);
        if (!StringUtils.isBlank(user.getCustomToken())) {
            token.setToken(user.getCustomToken());
        } else {
            token.setToken(generateToken());
        }

        token.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        token.setUser(user);

        setTokenAuthority(token, user.getRoles(), user.getRights(), user.getLabels());

        token.setEndpoint((EndpointType) params.get(AuthenticationSystemParameter.PARAM_ENDPOINT));

        return token;
    }

    private void setTokenAuthority(SecurityToken token, List<Role> roles, List<Right> rights, List<SecurityLabel> userLabels) {
        // Extract stuff, which may have been supplied by authorization
        roles = roles == null ? Collections.emptyList() : roles;
        rights = rights == null ? Collections.emptyList() : rights;
        userLabels = CollectionUtils.isEmpty(userLabels) ? Collections.emptyList() : userLabels;
        List<SecurityLabel> rolesLabels =
                CollectionUtils.isEmpty(roles) ?
                        Collections.emptyList() :
                        roles.stream()
                                .filter(r -> !CollectionUtils.isEmpty(r.getSecurityLabels()))
                                .flatMap(r -> r.getSecurityLabels().stream())
                                .collect(Collectors.toList());

        // Create maps from roles
        final Map<String, Right> rightsMap = SecurityUtils.createRightsMap(roles);
        final Map<String, Role> rolesMap = SecurityUtils.createRolesMap(roles);

        // Overwrite calculated rights with manually supplied one
        Map<String, Right> overwriteRights = SecurityUtils.extractRightsMap(rights);
        overwriteRights.forEach(rightsMap::put);

        token.getRightsMap().clear();
        token.getRightsMap().putAll(rightsMap);

        token.getRolesMap().clear();
        token.getRolesMap().putAll(rolesMap);

        token.getLabelsMap().clear();
        token.getLabelsMap().putAll(
                SecurityUtils.extractLabelsMap(
                        SecurityUtils.mergeSecurityLabels(userLabels, rolesLabels)
                )
        );
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#logout(java.lang.String)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean logout(String tokenString, Map<AuthenticationSystemParameter, Object> params) {
        final String userName = (String) params.get(AuthenticationSystemParameter.PARAM_USER_NAME);
        params.put(AuthenticationSystemParameter.PARAM_USER_TOKEN, tokenString);
        try {
            SecurityToken token = tokenCache.get(tokenString);
            boolean isTokenValid = token != null;
            if (token != null) {

                tokenCache.delete(tokenString);
                auditService.writeEvent(
                        SecurityAuditConstants.LOGOUT_EVENT_TYPE, Maps.of(LOGIN_AUDIT_EVENT_PARAMETER, userName)
                );
                /*
                userNotificationService.onLogout(token.getUser());
                */
            }

            return isTokenValid;
        } catch (Exception e) {
            auditService.writeEvent(
                    SecurityAuditConstants.LOGOUT_EVENT_TYPE,
                    Maps.of(LOGIN_AUDIT_EVENT_PARAMETER, userName, AuditConstants.EXCEPTION_FIELD, e)
            );
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
    public boolean updatePassword(String password, String oldPassword) {

        final UserWithPasswordDTO oldUser = userService.getUserByName(SecurityUtils.getCurrentUserName());
        if (!oldUser.isActive()) {
            throw new PlatformBusinessException("User account is not active!", CoreExceptionIds.EX_SECURITY_USER_NOT_ACTIVE);
        }

        if (StringUtils.isBlank(oldPassword)) {
            SecurityToken token = getTokenObjectByToken(getCurrentUserToken());
            if (token == null || !token.getUser().getForcePasswordChangeFlag()) {
                throwPasswordChangeFailed("Old password is null!");
            }
            userService.updatePassword(oldUser.getLogin(), null, password, false);
            return true;
        }

        if (!BCrypt.checkpw(oldPassword, oldUser.getPassword())) {
            throwPasswordChangeFailed("Old password doesn't match!");
        }

        userService.updatePassword(oldUser.getLogin(), null, password, false);
        return true;
    }

    private void throwPasswordChangeFailed(String message) {
        throw new PlatformBusinessException(message, CoreExceptionIds.EX_SECURITY_USER_PASSWORD_CHANGE_FAILED);
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
    @PostConstruct
    public void afterContextRefresh() {
        this.tokenCache = cache.getMap(getMapName());
        this.tokenCache.addEntryListener(new TokenListener(auditService, userDao), true);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.security.ISecurityService#logoutUserByName(java.lang.String)
     */
    @Transactional
    @Override
    public void logoutUserByName(String userName) {
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, userName);
        // params.put(AuthenticationSystemParameter.PARAM_DETAILS, MessageUtils.getMessage(AuditLocalizationConstants.LOGOUT_AFTER_CHANGE_SETTINGS));
        tokenCache.entrySet()
                .stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity.getValue() != null && !entity.getValue().isInner())
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
                .filter(entity -> entity.getValue() != null && !entity.getValue().isInner())
                .filter(entity -> entity.getValue().getUser() != null)
                .filter(entity -> roleService.isUserInRole(entity.getValue().getUser().getLogin(), roleName))
                .forEach(this::forceLogout);
        TransactionUtils.executeAfterCommitAction(() -> updateInnerTokensWithRole(roleName));
    }

    private void forceLogout(Entry<String, SecurityToken> entity) {
        Map<AuthenticationSystemParameter, Object> params = new EnumMap<>(AuthenticationSystemParameter.class);
        /*
        params.put(AuthenticationSystemParameter.PARAM_DETAILS,
                MessageUtils.getMessage(AuditLocalizationConstants.LOGOUT_AFTER_CHANGE_ROLES));
        */
        params.put(AuthenticationSystemParameter.PARAM_USER_NAME, entity.getValue().getUser().getLogin());
        this.logout(entity.getKey(), params);
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
