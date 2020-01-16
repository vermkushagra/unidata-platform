/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.core.type.security.impl;

import org.unidata.mdm.core.type.security.AuthenticationProvider;
import org.unidata.mdm.core.type.security.AuthorizationProvider;
import org.unidata.mdm.core.type.security.ProfileProvider;

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
