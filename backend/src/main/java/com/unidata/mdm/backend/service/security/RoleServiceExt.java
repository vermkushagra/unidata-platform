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

package com.unidata.mdm.backend.service.security;

import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.security.RoleRO;
import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.service.RoleService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

public interface RoleServiceExt extends AfterContextRefresh, RoleService {

    /**
     * Creates the new role.
     *
     * @param role
     *            the role dto
     */
    void create(Role role);

    /**
     * Delete role.
     *
     * @param roleName
     *            the role name
     */
    void delete(String roleName);

    /**
     * Update role.
     *
     * @param roleName
     *            the role name
     * @param role
     *            the role dto
     */
    void update(String roleName, Role role);

    /**
     * Unlink resource.
     *
     * @param roleName
     *            the role name
     * @param resourceName
     *            the resource name
     */
    void unlink(String roleName, String resourceName);

    /**
     * Creates the label.
     *
     * @param label
     *            the label
     */
    void createLabel(SecurityLabel label);

    /**
     * Update label.
     *
     * @param label
     *            the label
     * @param labelName
     *            the label name
     */
    void updateLabel(SecurityLabel label, String labelName);

    /**
     * Find label.
     *
     * @param labelName
     *            the label name
     * @return the security label dto
     */
    SecurityLabel findLabel(String labelName);

    /**
     * Delete label.
     *
     * @param labelName
     *            the label name
     */
    void deleteLabel(String labelName);

    /**
     * Create secured resources.
     * @param resources list with secured resources.
     */
    void createResources(List<SecuredResourceDTO> resources);

    /**
     * Delete resource by name.
     * @param resourceName resource name.
     */
    void deleteResource(String resourceName);

    /**
     * Drop all security resources.
     * @param categories the categories to drop.
     */
    void dropResources(SecuredResourceCategory... categories);

    /**
     * Post construct.
     */
    @Override
    void afterContextRefresh();

    /**
     * Gets the all properties.
     *
     * @return the all properties
     */
    List<RolePropertyDTO> loadAllProperties();

    /**
     * @param property
     */
    void saveProperty(RolePropertyDTO property);

    /**
     * @param id
     */
    void deleteProperty(long id);

    /**
     * @param roleId
     * @return
     */
    List<RolePropertyDTO> loadPropertyValues(int roleId);

    /**
     * @param roleId
     * @param roleProperties
     */
    void savePropertyValues(long roleId, List<CustomProperty> roleProperties);

    List<RoleDTO> loadAllRoles();

    void removeRolesByName(List<String> roles);

    List<Role> loadRolesData(List<String> rolesName);
}