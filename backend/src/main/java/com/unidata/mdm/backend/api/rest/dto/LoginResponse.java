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

package com.unidata.mdm.backend.api.rest.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.unidata.mdm.backend.api.rest.dto.security.LicenseRO;
import com.unidata.mdm.backend.api.rest.dto.security.UserRO;
import com.unidata.mdm.backend.common.integration.auth.Right;

/**
 * The Class LoginResponse.
 */
public class LoginResponse {

    /** The token. */
    @JsonProperty(index = 1, value = "token")
    private String token;

    /** User Role. */
    @JsonProperty(index = 2, value = "rights")
    private List<Right> rights;
    /**
     * User info.
     */
    @JsonProperty(index = 3, value = "userInfo")
    private UserRO userInfo;

    /**
     * Force change password flag.
     */
    @JsonProperty(index = 3, value = "forcePasswordChange")
    private Boolean forcePasswordChange;
    /**
     * Force change password flag.
     */
    @JsonProperty(index = 4, value = "tokenTTL")
    private long tokenTTL;

    /**
     * Information about license
     */
    @JsonProperty(index = 5, value = "license")
    private LicenseRO license;

    @JsonProperty(index = 6, value = "buildVersion")
    private String buildVersion;
    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token.
     *
     * @param token
     *            the new token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the rights
     */
    public List<Right> getRights() {
        return rights;
    }

    /**
     * @param rights
     *            the rights to set
     */
    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    /**
     * @return the userInfo
     */
    public UserRO getUserInfo() {
        return userInfo;
    }

    /**
     * @param userInfo
     *            the userInfo to set
     */
    public void setUserInfo(UserRO userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * Get force password change flag.
     * @return true if password should be changed, false otherwise
     */
    public Boolean getForcePasswordChange() {
        return forcePasswordChange;
    }

    /**
     * Set force password change flag.
     * @param forcePasswordChange true if if password should be changed, false otherwise
     */
    public void setForcePasswordChange(Boolean forcePasswordChange) {
        this.forcePasswordChange = forcePasswordChange;
    }
    /**
     * Return token time to live(in seconds)
     * @return token time to live
     */
	public long getTokenTTL() {
		return tokenTTL;
	}
	/**
	 * Sets token time to live.
	 * @param tokenTTL token time to live(in seconds)
	 */
	public void setTokenTTL(long tokenTTL) {
		this.tokenTTL = tokenTTL;
	}

    public LicenseRO getLicense() {
        return license;
    }

    public void setLicense(LicenseRO license) {
        this.license = license;
    }

    /**
     * Information about build version
     */
    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }
}
