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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.dto.security.RightDTO;
import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelAttributeDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.integration.auth.RoleType;
import com.unidata.mdm.backend.service.security.converters.RoleConverter;
import com.unidata.mdm.security.LabelAttributeDef;
import com.unidata.mdm.security.LabelDef;
import com.unidata.mdm.security.PropertyValueDef;
import com.unidata.mdm.security.RightToResourceDef;
import com.unidata.mdm.security.RoleDef;
import com.unidata.mdm.security.RolePropertyDef;


/**
 * The Class RolesXmlDefinitionsToObjectsConverter.
 */
public final class RolesXmlDefinitionsToObjectsConverter {
	
	/**
	 * Instantiates a new roles xml definitions to objects converter.
	 */
	private RolesXmlDefinitionsToObjectsConverter() {
	}

	/**
	 * To DT os.
	 *
	 * @param labels the labels
	 * @return the list
	 */
	public List<SecurityLabelDTO> toDTOs(final List<LabelDef> labels) {
		return labels.stream().map(RolesXmlDefinitionsToObjectsConverter::toDTO).collect(Collectors.toList());
	}

	/**
	 * To DTO.
	 *
	 * @param label the label
	 * @return the security label DTO
	 */
	public static SecurityLabelDTO toDTO(final LabelDef label) {
		final SecurityLabelDTO securityLabel = new SecurityLabelDTO();
		securityLabel.setName(label.getName());
		securityLabel.setDisplayName(label.getDisplayName());
		securityLabel.setDescription(label.getDescription());
		securityLabel.setAttributes(label.getAttributes().stream().map(RolesXmlDefinitionsToObjectsConverter::toDTO)
				.collect(Collectors.toList()));
		return securityLabel;
	}

	/**
	 * To DTO.
	 *
	 * @param labelAttribute the label attribute
	 * @return the security label attribute DTO
	 */
	private static SecurityLabelAttributeDTO toDTO(final LabelAttributeDef labelAttribute) {
		final SecurityLabelAttributeDTO securityLabelAttribute = new SecurityLabelAttributeDTO();
		securityLabelAttribute.setName(labelAttribute.getName());
		securityLabelAttribute.setDescription(labelAttribute.getDescription());
		securityLabelAttribute.setValue(labelAttribute.getValue());
		securityLabelAttribute.setPath(labelAttribute.getPath());
		return securityLabelAttribute;
	}

	// public static SecuredResourceDTO toDTO(final ResourceDef resource) {
	// return toDTO(resource, null);
	// }

	// public static SecuredResourceDTO toDTO(final ResourceDef resource,
	// SecuredResourceDTO parent) {
	// final SecuredResourceDTO securedResource = new SecuredResourceDTO();
	// securedResource.setName(resource.getName());
	// securedResource.setDisplayName(resource.getDisplayName());
	// securedResource.setType(SecuredResourceType.valueOf(resource.getRType()));
	//// securedResource.setCategory(SecuredResourceCategory.valueOf(resource.get));
	// if (CollectionUtils.isNotEmpty(resource.getChildrenResources())) {
	// securedResource.setChildren(
	// resource.getChildrenResources().stream()
	// .map(r -> toDTO(r, securedResource))
	// .collect(Collectors.toList())
	// );
	// }
	// securedResource.setParent(parent);
	// return securedResource;
	// }

	/**
	 * To DTO.
	 *
	 * @param roleProperty the role property
	 * @return the role property DTO
	 */
	public static RolePropertyDTO toDTO(final RolePropertyDef roleProperty) {

		return null;
	}

	/**
	 * To DT os.
	 *
	 * @param roles the roles
	 * @param rolePropertiesCache the role properties cache
	 * @return the list
	 */
	public static List<RoleDTO> toDTOs(final List<RoleDef> roles, final Map<String, Long> rolePropertiesCache) {
		return roles.stream().map(r -> toDTO(r, rolePropertiesCache)).collect(Collectors.toList());
	}

	/**
	 * To DTO.
	 *
	 * @param roleDef the role def
	 * @param rolePropertiesCache the role properties cache
	 * @return the role DTO
	 */
	private static RoleDTO toDTO(RoleDef roleDef, Map<String, Long> rolePropertiesCache) {
		final RoleDTO roleDTO = new RoleDTO();
		roleDTO.setName(roleDef.getName());
		roleDTO.setDisplayName(roleDef.getDisplayName());
		roleDTO.setRoleType(RoleType.valueOf(roleDef.getRType()));
		roleDTO.setSecurityLabels(roleDef.getLabels().stream().map(RolesXmlDefinitionsToObjectsConverter::toDTO)
				.collect(Collectors.toList()));
		roleDTO.setProperties(roleDef.getPropertiesValues().stream().map(p -> toDTO(p, rolePropertiesCache))
				.collect(Collectors.toList()));
		final Map<String, List<RightToResourceDef>> resources = roleDef.getRightsToResources().stream()
				.collect(Collectors.groupingBy(RightToResourceDef::getResource));
		roleDTO.setRights(resources.entrySet().stream().map(RolesXmlDefinitionsToObjectsConverter::toDTO)
				.collect(Collectors.toList()));
		return roleDTO;
	}

	/**
	 * To DTO.
	 *
	 * @param propertyValue the property value
	 * @param rolePropertiesCache the role properties cache
	 * @return the role property DTO
	 */
	private static RolePropertyDTO toDTO(final PropertyValueDef propertyValue,
			final Map<String, Long> rolePropertiesCache) {
		final RolePropertyDTO roleProperty = new RolePropertyDTO();
		roleProperty.setId(rolePropertiesCache.get(propertyValue.getPropertyName()));
		roleProperty.setName(propertyValue.getPropertyName());
		roleProperty.setValue(propertyValue.getValue());
		return roleProperty;
	}

	/**
	 * To DTO.
	 *
	 * @param entry the entry
	 * @return the right DTO
	 */
	private static RightDTO toDTO(Map.Entry<String, List<RightToResourceDef>> entry) {
		final RightDTO right = new RightDTO();
		final SecuredResourceDTO securedResource = new SecuredResourceDTO();
		securedResource.setName(entry.getKey());
		right.setSecuredResource(securedResource);
		for (RightToResourceDef rightToResource : entry.getValue()) {
			if (RoleConverter.CREATE_LABEL.equals(rightToResource.getRight())) {
				right.setCreate(true);
			} else if (RoleConverter.READ_LABEL.equals(rightToResource.getRight())) {
				right.setRead(true);
			} else if (RoleConverter.DELETE_LABEL.equals(rightToResource.getRight())) {
				right.setDelete(true);
			} else if (RoleConverter.UPDATE_LABEL.equals(rightToResource.getRight())) {
				right.setUpdate(true);
			}
		}
		return right;
	}
}
