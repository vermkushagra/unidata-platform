package org.unidata.mdm.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.context.AuthenticationRequestContext;
import org.unidata.mdm.core.dto.UserWithPasswordDTO;
import org.unidata.mdm.core.service.PasswordPolicyService;
import org.unidata.mdm.core.service.UserService;
import org.unidata.mdm.core.type.security.AuthenticationProvider;
import org.unidata.mdm.core.type.security.AuthenticationSystemParameter;
import org.unidata.mdm.core.type.security.AuthorizationProvider;
import org.unidata.mdm.core.type.security.ProfileProvider;
import org.unidata.mdm.core.type.security.User;
import org.unidata.mdm.core.type.security.impl.UserInfo;
import org.unidata.mdm.core.util.SecurityUtils;

import java.time.Duration;
import java.util.Map;

/**
 * @author mikhail
 * Unidata security data source.
 */
@Component
public class StandardSecurityDataProvider
        implements AuthenticationProvider, AuthorizationProvider, ProfileProvider {

    /**
     * User service.
     */
    @Autowired
    private UserService userService;
    /**
     * Password policy
     */
    @Autowired
    private PasswordPolicyService passwordPolicy;
    /**
     * {@inheritDoc}
     */
    @Override
    public void load(User user) {

        // External user, but UD profile support
        UserWithPasswordDTO existing = userService.getUserByName(user.getLogin());
        user.setPasswordUpdatedAt(existing.getPasswordLastChangedAt());
        user.setAdmin(existing.isAdmin());
        user.setSecurityDataSource(existing.getSecurityDataSource() == null
                ? SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE
                : existing.getSecurityDataSource());
        user.setUpdatedAt(existing.getUpdatedAt());
        user.setForcePasswordChangeFlag(false);
        user.setEmail(existing.getEmail());
        user.setLocale(existing.getLocale());
        user.setEndpoints(existing.getEndpoints());
        user.setName(existing.getFullName());
        user.setHasProfile(true);
        user.setEmailNotification(existing.isEmailNotification());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void authorize(User user) {

        // External user, but UD authorization support
        UserWithPasswordDTO existing = userService.getUserByName(user.getLogin());
        user.setRoles(existing.getRoles());
        user.setLabels(existing.getSecurityLabels());
        user.setEndpoints(existing.getEndpoints());
        user.setHasAuthorization(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User login(AuthenticationRequestContext context) {

        Map<AuthenticationSystemParameter, Object> params = context.getParams();
        String username = (String) params.get(AuthenticationSystemParameter.PARAM_USER_NAME);
        String password = (String) params.get(AuthenticationSystemParameter.PARAM_USER_PASSWORD);

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return null;
        }

        // Authenticate
        UserWithPasswordDTO existing = userService.getUserByName(username);
        boolean isAuthenticated = existing != null
                && existing.getPassword() != null
                && existing.isActive()
                && BCrypt.checkpw(password, existing.getPassword());

        if (isAuthenticated) {

            UserInfo user = new UserInfo();

            // Authorize
            user.setRoles(existing.getRoles());
            user.setLabels(existing.getSecurityLabels());
            user.setCustomProperties(existing.getCustomProperties());
            user.setHasAuthorization(true);
            user.setEndpoints(existing.getEndpoints());

            // Profile
            user.setLogin(username);
            user.setPassword(password);
            user.setPasswordUpdatedAt(existing.getPasswordLastChangedAt());
            user.setPasswordUpdatedBy(existing.getPasswordUpdatedBy());
            user.setAdmin(existing.isAdmin());
            user.setSecurityDataSource(existing.getSecurityDataSource() == null
                    ? SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE
                    : existing.getSecurityDataSource());
            user.setCreatedAt(existing.getCreatedAt());
            user.setUpdatedBy(existing.getPasswordUpdatedBy());
            user.setUpdatedAt(existing.getUpdatedAt());
            user.setExternal(existing.isExternal());
            user.setForcePasswordChangeFlag(forceChangePassword(user));
            user.setEmail(existing.getEmail());
            user.setLocale(existing.getLocale());
            user.setName(existing.getFullName());
            user.setHasProfile(true);
            user.setEmailNotification(existing.isEmailNotification());

            return user;
        }

        return null;
    }

    /**
     * Check if the password should be changed.
     *
     * @param user user
     * @return true if the password should be chnaged
     */
    private boolean forceChangePassword(UserInfo user) {

        if (user.getPasswordUpdatedAt() != null) {
            return Duration.between(user.getPasswordUpdatedAt().toInstant(), user.getCreatedAt().toInstant()).abs().getSeconds() < 3
                    || (passwordPolicy.isExpired(user.getPasswordUpdatedAt().toInstant(), user.isAdmin()) && passwordPolicy.isAllowChangeExpiredPassword(user.isAdmin()))
                    || (!StringUtils.equals(user.getLogin(), user.getPasswordUpdatedBy()) && passwordPolicy.isAllowChangeExpiredPassword(user.isAdmin()));
        }

        return Boolean.FALSE;
    }
}
