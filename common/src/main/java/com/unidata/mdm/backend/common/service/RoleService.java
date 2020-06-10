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

package com.unidata.mdm.backend.common.service;

import java.util.List;

import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;

public interface RoleService {
    /**
     * Gets the role by name.
     *
     * @param roleName
     *            the role name
     * @return the role by name
     */
    Role getRoleByName(String roleName);

    /**
     * Gets the all roles.
     *
     * @return the all roles
     */
    List<Role> getAllRoles();

    /**
     * Gets all roles by user login.
     * @param login the user login
     * @return list of roles
     */
    List<Role> getAllRolesByUserLogin(String login);

    /**
     * Gets the all secured resources.
     *
     * @return the all secured resources
     */
    List<SecuredResourceDTO> getAllSecuredResources();

    /**
     * Gets the all secured resources as flat list
     *
     * @return the all secured resources
     */
    List<SecuredResourceDTO> getSecuredResourcesFlatList();

    /**
     * Gets the all security labels.
     *
     * @return the all security labels
     */
    List<SecurityLabel> getAllSecurityLabels();

    /**
     * Determines is the provided user connected with provided role.
     *
     * @param userName
     *            User name
     * @param roleName
     *            Role name
     * @return <code>true</code> if provided user connected with role, otherwise
     *         <code>false</code>.
     */
    boolean isUserInRole(String userName, String roleName);
}
