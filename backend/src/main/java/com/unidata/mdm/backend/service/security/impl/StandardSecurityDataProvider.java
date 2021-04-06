/**
 *
 */
package com.unidata.mdm.backend.service.security.impl;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.AuthenticationRequestContext;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationProvider;
import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;
import com.unidata.mdm.backend.common.integration.auth.AuthorizationProvider;
import com.unidata.mdm.backend.common.integration.auth.ProfileProvider;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.dto.storage.UserInfo;
import com.unidata.mdm.backend.service.security.UserService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

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
     * Password TTL. By default set to 30 days.
     */
    @Value(value = "${unidata.security.password.expiration:2592000}")
    private long passwordTTL;
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

        boolean result = false;
        if (user.getPasswordUpdatedAt() == null) {
            result = false;
        } else {
            if ((Duration.between(user.getPasswordUpdatedAt().toInstant(), user.getCreatedAt().toInstant()).abs().getSeconds() < 3
                    || new Date(System.currentTimeMillis() + passwordTTL * 1000).before(user.getPasswordUpdatedAt()))
                    || (!StringUtils.equals(user.getLogin(), user.getPasswordUpdatedBy()))) {
                result = true;
            }
        }

        return result;
    }
}
