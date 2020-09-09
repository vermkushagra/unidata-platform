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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.security.LabelDef;
import com.unidata.mdm.security.RightToResourceDef;
import com.unidata.mdm.security.RoleDef;
import com.unidata.mdm.security.RolePropertyDef;
import org.apache.commons.collections4.CollectionUtils;

public final class RoleObjectsToXmlDefinitionsConverter {
    private RoleObjectsToXmlDefinitionsConverter() {}

    public static List<RoleDef> convertRoles(final List<RoleDTO> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream().map(RoleObjectsToXmlDefinitionsConverter::toXmlObject).collect(Collectors.toList());
    }

    public static RoleDef toXmlObject(final RoleDTO role) {
        return JaxbUtils.getSecurityFactory().createRoleDef()
                .withName(role.getName())
                .withDisplayName(role.getDisplayName())
                .withRType(role.getRoleType().name())
                .withCreatedAt(JaxbUtils.localTimestampValueToXMGregorianCalendar(role.getCreatedAt()))
                .withCreatedBy(role.getCreatedBy())
                .withUpdatedAt(JaxbUtils.localTimestampValueToXMGregorianCalendar(role.getUpdatedAt()))
                .withUpdatedBy(role.getUpdatedBy())
                .withRightsToResources(convertRights(role.getRights()))
                .withPropertiesValues(
                        CommonSecurityObjectsToXmlDefinitionsConverter.convertProperties(role.getProperties())
                )
                .withRoleLabels(
                        role.getSecurityLabels().stream()
                                .map(SecurityLabel::getName)
                                .collect(Collectors.toList())
                )
                .withLabels(
                        CommonSecurityObjectsToXmlDefinitionsConverter.convertSecurityLabels(role.getSecurityLabels())
                );
    }

    public static List<RightToResourceDef> convertRights(final Collection<Right> rights) {
        if (CollectionUtils.isEmpty(rights)) {
            return Collections.emptyList();
        }
        return rights.stream().flatMap(right -> {
                    final List<RightToResourceDef> result = new ArrayList<>();
                    if (right.isCreate()) {
                        result.add(createRightToResource(right, "CREATE"));
                    }
                    if (right.isUpdate()) {
                        result.add(createRightToResource(right, "UPDATE"));
                    }
                    if (right.isRead()) {
                        result.add(createRightToResource(right, "READ"));
                    }
                    if (right.isDelete()) {
                        result.add(createRightToResource(right, "DELETE"));
                    }
                    return result.stream();
                }
        ).collect(Collectors.toList());
    }

    private static RightToResourceDef createRightToResource(final Right right, final String rightName) {
        return JaxbUtils.getSecurityFactory().createRightToResourceDef()
                .withResource(right.getSecuredResource().getName())
                .withRight(rightName);
    }

    public static List<RolePropertyDef> convertRoleProperties(final List<RolePropertyDTO> rolePropertyDTOS) {
        return rolePropertyDTOS.stream().map(rolePropertyDTO ->
                JaxbUtils.getSecurityFactory().createRolePropertyDef()
                        .withName(rolePropertyDTO.getName())
                        .withDisplayName(rolePropertyDTO.getDisplayName())
        ).collect(Collectors.toList());
    }

    public static List<LabelDef> convertSecurityLabels(final List<SecurityLabel> securityLabels) {
        return securityLabels.stream().map(securityLabel ->
                JaxbUtils.getSecurityFactory().createLabelDef()
                        .withName(securityLabel.getName())
                        .withDisplayName(securityLabel.getDisplayName())
                        .withAttributes(securityLabel.getAttributes().stream().map(labelAttribute ->
                                JaxbUtils.getSecurityFactory().createLabelAttributeDef()                                		
                                        .withName(labelAttribute.getName())
                                        .withValue(labelAttribute.getValue()) 
                                        .withPath(labelAttribute.getPath())
                        ).collect(Collectors.toList()))
        ).collect(Collectors.toList());
    }
}
