package com.unidata.mdm.backend.service.security;

import com.unidata.mdm.backend.common.integration.auth.AuthenticationProvider;
import com.unidata.mdm.backend.common.integration.auth.AuthorizationProvider;
import com.unidata.mdm.backend.common.integration.auth.ProfileProvider;

/**
 * Security provider with appropriate contract.
 *
 * @author Denis Kostovarov
 */
public final class SecurityDataSource {
    /**
     * Security data source name.
     */
    private final String name;
    /**
     * Security data source description.
     */
    private final String description;
    /**
     * Optional authentication provider.
     */
    private final AuthenticationProvider authenticationProvider;
    /**
     * Optional authorization provider.
     */
    private final AuthorizationProvider authorizationProvider;
    /**
     * Optional profile provider.
     */
    private final ProfileProvider profileProvider;
    /**
     * Constructor.
     * @param name the name (source ID)
     * @param description source description
     * @param authenticationProvider authentication provider
     * @param authorizationProvider authorization provider
     * @param profileProvider profile provider
     */
    public SecurityDataSource(final String name,
                            final String description,
                            final AuthenticationProvider authenticationProvider,
                            final AuthorizationProvider authorizationProvider, 
                            final ProfileProvider profileProvider) {
        this.name = name;
        this.description = description;
        this.authenticationProvider = authenticationProvider;
        this.authorizationProvider = authorizationProvider;
        this.profileProvider = profileProvider;
    }

    /**
     * Name of security provider.
     *
     * @return security provider name.
     */
    public String getName() {
        return name;
    }

    /**
     * Description of the sec. data source.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Authentication provider to check username/password pair and/or fill other user specific information.
     *
     * @return authentication provider.
     */
    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    /**
     * Authorization provider to get rights for secured resources.
     *
     * @return authorization provider.
     */
    public AuthorizationProvider getAuthorizationProvider() {
        return authorizationProvider;
    }

    /**
     * Profile provider to fetch user specific information.
     *
     * @return profile provider.
     */
    public ProfileProvider getProfileProvider() {
        return profileProvider;
    }
}
