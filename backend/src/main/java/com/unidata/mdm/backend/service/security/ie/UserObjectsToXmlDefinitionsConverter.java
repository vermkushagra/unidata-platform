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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.dto.security.PasswordDTO;
import com.unidata.mdm.backend.common.dto.security.UserDTO;
import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.security.UserDef;
import com.unidata.mdm.security.UserPasswordDef;
import com.unidata.mdm.security.UserPropertyDef;
import org.apache.commons.collections4.CollectionUtils;

public final class UserObjectsToXmlDefinitionsConverter {
    private UserObjectsToXmlDefinitionsConverter() {}

    public static List<UserDef> convertUsers(final Collection<UserDTO> users) {
        return convertUsers(users, Collections.emptyList());
    }

    public static List<UserDef> convertUsers(final Collection<UserDTO> users, final Collection<PasswordDTO> passwords) {
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        final Map<String, List<PasswordDTO>> usersPasswords = passwords.stream()
                .collect(Collectors.groupingBy(p -> p.getUser().getLogin()));
        return users.stream()
                .map(u -> UserObjectsToXmlDefinitionsConverter.toXmlObject(u, usersPasswords.get(u.getLogin())))
                .collect(Collectors.toList());
    }

    public static UserDef toXmlObject(final UserDTO userDTO, final Collection<PasswordDTO> userPasswords) {
        return JaxbUtils.getSecurityFactory().createUserDef()
                .withLogin(userDTO.getLogin())
                .withEmail(userDTO.getEmail())
                .withFirstName(userDTO.getFirstName())
                .withLastName(userDTO.getLastName())
                .withActive(userDTO.isActive())
                .withAdmin(userDTO.isAdmin())
                .withExternal(userDTO.isExternal())
                .withLocale(userDTO.getLocale() != null ? userDTO.getLocale().toLanguageTag() : null)
                .withCreatedAt(JaxbUtils.localTimestampValueToXMGregorianCalendar(userDTO.getCreatedAt()))
                .withCreatedBy(userDTO.getCreatedBy())
                .withUpdatedAt(JaxbUtils.localTimestampValueToXMGregorianCalendar(userDTO.getUpdatedAt()))
                .withUpdatedBy(userDTO.getUpdatedBy())
                .withApis(
                        CollectionUtils.isNotEmpty(userDTO.getEndpoints()) ?
                                userDTO.getEndpoints().stream().map(Endpoint::getName).collect(Collectors.toList()) :
                                Collections.emptyList()
                )
                .withPropertiesValues(
                        CommonSecurityObjectsToXmlDefinitionsConverter.convertProperties(userDTO.getCustomProperties())
                )
                .withRoles(
                        CollectionUtils.isNotEmpty(userDTO.getRoles()) ?
                                userDTO.getRoles().stream().map(Role::getName).collect(Collectors.toList()) :
                                Collections.emptyList()
                )
                .withLabels(
                        CommonSecurityObjectsToXmlDefinitionsConverter.convertSecurityLabels(userDTO.getSecurityLabels())
                ).withPasswords(
                        convertUserPasswords(userPasswords)
                );
    }

    private static List<UserPasswordDef> convertUserPasswords(Collection<PasswordDTO> userPasswords) {
        if (CollectionUtils.isEmpty(userPasswords)) {
            return null;
        }
        return userPasswords.stream()
                .map(p -> JaxbUtils.getSecurityFactory().createUserPasswordDef()
                        .withPasswordText(p.getHashedText())
                        .withActive(p.isActive())
                )
                .collect(Collectors.toList());
    }

//    public static List<ApiDef> convertEndpoints(final List<Endpoint> endpoints) {
//        return endpoints.stream().map(endpoint ->
//                JaxbUtils.getSecurityFactory().createApiDef()
//                        .withName(endpoint.getName())
//                        .withDisplayName(endpoint.getDisplayName())
//                        .withDescription(endpoint.getDescription())
//        ).collect(Collectors.toList());
//    }


    public static List<UserPropertyDef> convertUserProperties(List<UserPropertyDTO> userProperties) {
        return userProperties.stream().map(userPropertyDTO ->
                JaxbUtils.getSecurityFactory().createUserPropertyDef()
                        .withName(userPropertyDTO.getName())
                        .withDisplayName(userPropertyDTO.getDisplayName())
        ).collect(Collectors.toList());
    }
}
