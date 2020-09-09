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

package com.unidata.mdm.backend.api.rest.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * transfer object for information about license
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LicenseRO {

    private String owner;

    private String expirationDate;

    private String version;

    private String versionDisplayName;

    private String licenseMode;

    private String licenseModeDisplayName;

    private String[] modules;

    public String getOwner() {
        return owner;
    }

    public LicenseRO setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public LicenseRO setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public String getLicenseMode() {
        return licenseMode;
    }

    public LicenseRO setLicenseMode(String licenseMode) {
        this.licenseMode = licenseMode;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public LicenseRO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String[] getModules() {
        return modules;
    }

    public LicenseRO setModules(String[] modules) {
        this.modules = modules;
        return this;
    }

    /**
     * @return the versionDisplayName
     */
    public String getVersionDisplayName() {
        return versionDisplayName;
    }

    /**
     * @param versionDisplayName the versionDisplayName to set
     */
    public LicenseRO setVersionDisplayName(String versionDisplayName) {
        this.versionDisplayName = versionDisplayName;
        return this;
    }

    /**
     * @return the licenseModeDisplayName
     */
    public String getLicenseModeDisplayName() {
        return licenseModeDisplayName;
    }

    /**
     * @param licenseModeDisplayName the licenseModeDisplayName to set
     */
    public LicenseRO setLicenseModeDisplayName(String licenseModeDisplayName) {
        this.licenseModeDisplayName = licenseModeDisplayName;
        return this;
    }
}
