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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class RoleRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleRO extends BaseSecurityRO {

    /** The name. */
    private String name;

    /** The display name. */
    private String displayName;
    /** The role type. */
    private RoleTypeRO type;
    /** The rights. */
    private List<RightRO> rights;
    /**
     * Security labels.
     */
    private List<SecurityLabelRO> securityLabels;
    /**
     * Role properties.
     */
    private List<RolePropertyRO> properties;
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the rights.
     *
     * @return the rights
     */
    public List<RightRO> getRights() {
        return rights;
    }

    /**
     * Sets the rights.
     *
     * @param rights
     *            the new rights
     */
    public void setRights(List<RightRO> rights) {
        this.rights = rights;
    }

    /**
     * Adds the right.
     *
     * @param right
     *            the right
     */
    public void addRight(RightRO right) {
        if (this.rights == null) {
            this.rights = new ArrayList<>();
        }
        this.rights.add(right);
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName
     *            the new display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public RoleTypeRO getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type
     *            the new type
     */
    public void setType(RoleTypeRO type) {
        this.type = type;
    }

    /**
     * @return the securityLabels
     */
    public List<SecurityLabelRO> getSecurityLabels() {
        if (this.securityLabels == null) {
            this.securityLabels = new ArrayList<>();
        }
        return securityLabels;
    }

    /**
     * @param securityLabels
     *            the securityLabels to set
     */
    public void setSecurityLabels(List<SecurityLabelRO> securityLabels) {
        this.securityLabels = securityLabels;
    }

    public void addSecurityLabel(SecurityLabelRO securityLabelRO) {
        if (this.securityLabels == null) {
            this.securityLabels = new ArrayList<>();
        }
        this.securityLabels.add(securityLabelRO);
    }

    /**
     * @return the properties
     */
    public List<RolePropertyRO> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(List<RolePropertyRO> properties) {
        this.properties = properties;
    }

}
