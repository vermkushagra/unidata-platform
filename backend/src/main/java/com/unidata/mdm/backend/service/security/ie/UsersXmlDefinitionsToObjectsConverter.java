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

package com.unidata.mdm.backend.service.security.ie;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.UserEndpointDTO;
import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.security.PropertyValueDef;
import com.unidata.mdm.security.UserDef;
import com.unidata.mdm.security.UserPropertyDef;
import org.apache.commons.collections4.CollectionUtils;

public final class UsersXmlDefinitionsToObjectsConverter {
    private UsersXmlDefinitionsToObjectsConverter() {}

    public static UserPropertyDTO toDTO(UserPropertyDef userProperty) {
        final UserPropertyDTO userPropertyDTO = new UserPropertyDTO();
        userPropertyDTO.setName(userProperty.getName());
        userPropertyDTO.setDisplayName(userProperty.getDisplayName());
        return userPropertyDTO;
    }

    public static List<UserWithPasswordDTO> toDTOs(final List<UserDef> users, final Map<String, Long> propertiesCache) {
        return users.stream().map(u -> {
            final UserWithPasswordDTO user = new UserWithPasswordDTO();
            user.setLogin(u.getLogin());
            user.setEmail(u.getEmail());
            user.setFirstName(u.getFirstName());
            user.setLastName(u.getLastName());
            user.setActive(u.isActive());
            user.setAdmin(u.isAdmin());
            user.setExternal(u.isExternal());
            user.setEnpoints(convertApis(u.getApis()));
            user.setRoles(convertRoles(u.getRoles()));
            user.setProperties(convertPropertiesValues(u.getPropertiesValues(), propertiesCache));
            user.setSecurityLabels(CommonXmlDefinitionsToObjectsConverter.convertSecurityLabels(u.getLabels()));
            user.setSecurityDataSource(u.getSource());
            user.setLocale(u.getLocale() != null ? new Locale(u.getLocale()) : null);
            return user;
        }).collect(Collectors.toList());
    }

    private static List<UserPropertyDTO> convertPropertiesValues(
            final List<PropertyValueDef> values,
            final Map<String, Long> propertiesCache) {
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptyList();
        }
        return values.stream().map(p -> {
            final UserPropertyDTO userPropertyDTO = new UserPropertyDTO();
            userPropertyDTO.setId(propertiesCache.get(p.getPropertyName()));
            userPropertyDTO.setName(p.getPropertyName());
            userPropertyDTO.setValue(p.getValue());
            return userPropertyDTO;
        }).collect(Collectors.toList());
    }

    private static List<Endpoint> convertApis(final List<String> apis) {
        if (CollectionUtils.isEmpty(apis)) {
            return Collections.emptyList();
        }
        return apis.stream().map(api -> {
            final UserEndpointDTO userEndpoint = new UserEndpointDTO();
            userEndpoint.setName(api);
            return userEndpoint;
        }).collect(Collectors.toList());
    }

    private static List<Role> convertRoles(List<String> roles) {
        return roles.stream().map(r -> {
            final RoleDTO roleDTO = new RoleDTO();
            roleDTO.setName(r);
            return roleDTO;
        }).collect(Collectors.toList());
    }
}
