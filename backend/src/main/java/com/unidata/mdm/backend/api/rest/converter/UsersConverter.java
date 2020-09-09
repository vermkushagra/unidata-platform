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

package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.security.SecurityDataSourceRO;
import com.unidata.mdm.backend.api.rest.dto.security.SecurityLabelAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.security.SecurityLabelRO;
import com.unidata.mdm.backend.api.rest.dto.security.UserAPIRO;
import com.unidata.mdm.backend.api.rest.dto.security.UserPropertyRO;
import com.unidata.mdm.backend.api.rest.dto.security.UserRO;
import com.unidata.mdm.backend.api.rest.dto.security.UserWithPasswordRO;
import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelAttributeDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.dto.security.UserDTO;
import com.unidata.mdm.backend.common.dto.security.UserEndpointDTO;
import com.unidata.mdm.backend.common.dto.security.UserPropertyDTO;
import com.unidata.mdm.backend.common.dto.security.UserWithPasswordDTO;
import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.Endpoint;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.backend.service.security.SecurityDataSource;
import com.unidata.mdm.backend.service.security.po.UserPropertyPO;
import com.unidata.mdm.backend.service.security.po.UserPropertyValuePO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;


/**
 * The Class UsersConverter.
 */
public class UsersConverter {

    /**
     * Convert user dto.
     *
     * @param source
     *            the source
     * @return the user ro
     */
    public static UserRO convertUserDTO(final UserDTO source) {

        if (source == null) {
            return null;
        }

        final UserRO target = new UserRO();
        target.setActive(source.isActive());
        target.setAdmin(source.isAdmin());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setEmail(source.getEmail());
        target.setLocale(source.getLocale() != null ? source.getLocale().getLanguage() : null);
        target.setFirstName(source.getFirstName());
        target.setFullName(source.getFullName());
        target.setLastName(source.getLastName());
        target.setLogin(source.getLogin());
        target.setEndpoints(convertAPIsDtoToRo(source.getEndpoints()));
        target.setRoles(CollectionUtils.isEmpty(source.getRoles())
                ? Collections.emptyList()
                : source.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setSecurityLabels(convertSecurityLabelDTOs(source.getSecurityLabels()));
        target.setProperties(convertPropertiesDtoToRo(source.getCustomProperties()));
        target.setExternal(source.isExternal());
        target.setSecurityDataSource(source.getSecurityDataSource());
        return target;
    }

    /**
     * Convert security label dt os.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecurityLabelRO> convertSecurityLabelDTOs(final Collection<SecurityLabel> source) {
        if (source == null) {
            return null;
        }
        final List<SecurityLabelRO> target = new ArrayList<SecurityLabelRO>();
        source.forEach(s -> target.add(convertSecurityLabelDTO(s)));
        return target;
    }

    /**
     * Convert security label dto.
     *
     * @param source
     *            the source
     * @return the security label ro
     */
    private static SecurityLabelRO convertSecurityLabelDTO(SecurityLabel source) {
        if (source == null) {
            return null;
        }
        SecurityLabelRO target = new SecurityLabelRO();
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setAttributes(convertAttributeDTOs(source.getAttributes()));
        return target;
    }

    /**
     * Convert attribute dt os.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<SecurityLabelAttributeRO> convertAttributeDTOs(final List<SecurityLabelAttribute> source) {
        if (source == null) {
            return null;
        }
        final List<SecurityLabelAttributeRO> target = new ArrayList<SecurityLabelAttributeRO>();
        source.forEach(s -> target.add(convertAttributeDTO(s)));
        return target;
    }

    /**
     * Convert attribute dto.
     *
     * @param source
     *            the source
     * @return the security label attribute ro
     */
    private static SecurityLabelAttributeRO convertAttributeDTO(SecurityLabelAttribute source) {
        if (source == null) {
            return null;
        }
        SecurityLabelAttributeRO target = new SecurityLabelAttributeRO();
        target.setName(source.getName());
        target.setValue(source.getValue());
        target.setPath(source.getPath());
        return target;
    }

    /**
     * Convert user ro.
     *
     * @param source
     *            the source
     * @return the user with password dto
     */
    public static UserWithPasswordDTO convertUserRO(final UserWithPasswordRO source) {
        if (source == null) {
            return null;
        }
        final UserWithPasswordDTO target = new UserWithPasswordDTO();
        target.setActive(source.isActive());
        target.setAdmin(source.isAdmin());
        target.setEmail(source.getEmail());
        if (source.getLocale() != null) {
            target.setLocale(new Locale(source.getLocale()));
        }
        target.setFirstName(source.getFirstName());
        target.setFullName(source.getFullName());
        target.setLastName(source.getLastName());
        target.setLogin(source.getLogin());
        target.setPassword(source.getPassword());
        target.setRoles(convertRoles(source.getRoles()));
        target.setEnpoints(convertAPIRoToDtos(source.getEndpoints()));
        target.setSecurityLabels(convertLabels(source.getSecurityLabels()));
        target.setProperties(convertPropertiesRoToDto(source.getProperties()));
        target.setExternal(source.isExternal());
        target.setSecurityDataSource(Objects.isNull(source.getSecurityDataSource())
                ? SecurityUtils.UNIDATA_SECURITY_DATA_SOURCE
                : source.getSecurityDataSource());
        return target;
    }

    /**
     * Convert API ro to dtos.
     *
     * @param source the source
     * @return the list
     */
    private static List<Endpoint> convertAPIRoToDtos(List<UserAPIRO> source) {
		if(source==null){
			return null;
		}
		List<Endpoint> target = new ArrayList<>();
		source.stream().forEach(a->{
			target.add(convertAPIRoToDto(a));
		});
		return target;
	}

	/**
	 * Convert API ro to dto.
	 *
	 * @param source the source
	 * @return the user APIDTO
	 */
	private static UserEndpointDTO convertAPIRoToDto(UserAPIRO source) {
		if(source==null){
			return null;
		}
		UserEndpointDTO target = new UserEndpointDTO();
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		return target;
	}

	/**
     * Convert roles.
     *
     * @param source the source
     * @return the list
     */
    private static List<Role> convertRoles(List<String> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<Role> target = new ArrayList<>();
        source.forEach(name -> {
            RoleDTO r = new RoleDTO();
            r.setName(name);
            target.add(r);
        });

        return target;
    }

    /**
     * Convert property po to dto.
     *
     * @param propertyPO the property PO
     * @return the user property DTO
     */
    public static UserPropertyDTO convertPropertyPoToDto(UserPropertyPO propertyPO) {
        if (propertyPO == null) {
            return null;
        }

        UserPropertyDTO dto = new UserPropertyDTO();

        dto.setId(propertyPO.getId());
        dto.setName(propertyPO.getName());
        dto.setDisplayName(propertyPO.getDisplayName());

        return dto;
    }

    /**
     * Convert properties po to dto.
     *
     * @param propertyPOs the property P os
     * @return the list
     */
    public static List<UserPropertyDTO> convertPropertiesPoToDto(List<UserPropertyPO> propertyPOs) {
        if (propertyPOs == null) {
            return null;
        }
        final List<UserPropertyDTO> target = new ArrayList<>();
        propertyPOs.forEach(s -> target.add(convertPropertyPoToDto(s)));
        return target;
    }

    /**
     * Convert property dto to po.
     *
     * @param propertyDTO the property DTO
     * @return the user property PO
     */
    public static UserPropertyPO convertPropertyDtoToPo(UserPropertyDTO propertyDTO) {
        if (propertyDTO == null) {
            return null;
        }

        UserPropertyPO po = new UserPropertyPO();
        po.setId(propertyDTO.getId());
        po.setName(propertyDTO.getName());
        po.setDisplayName(propertyDTO.getDisplayName());

        return po;
    }

    /**
     * Convert properties dto to po.
     *
     * @param propertyDTOs the property DT os
     * @return the list
     */
    public static List<UserPropertyPO> convertPropertiesDtoToPo(List<UserPropertyDTO> propertyDTOs) {
        if (propertyDTOs == null) {
            return null;
        }

        final List<UserPropertyPO> target = new ArrayList<>();
        propertyDTOs.forEach(s -> target.add(convertPropertyDtoToPo(s)));
        return target;
    }

    /**
     * Convert property value po to dto.
     *
     * @param valuePO the value PO
     * @return the user property DTO
     */
    public static UserPropertyDTO convertPropertyValuePoToDto(UserPropertyValuePO valuePO) {

        if (valuePO == null) {
            return null;
        }

        UserPropertyDTO dto = new UserPropertyDTO();
        if (valuePO.getProperty() != null) {
            dto.setId(valuePO.getProperty().getId());
            dto.setName(valuePO.getProperty().getName());
            dto.setDisplayName(valuePO.getProperty().getDisplayName());
        }

        dto.setValue(valuePO.getValue());
        return dto;
    }

    /**
     * Convert property values po to dto.
     *
     * @param valuePOs the value P os
     * @return the list
     */
    public static List<UserPropertyDTO> convertPropertyValuesPoToDto(List<UserPropertyValuePO> valuePOs) {

        if (CollectionUtils.isEmpty(valuePOs)) {
            return Collections.emptyList();
        }

        return valuePOs.stream()
            .map(UsersConverter::convertPropertyValuePoToDto)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Convert property value dto to po.
     *
     * @param valueDto the value dto
     * @return the user property value PO
     */
    public static UserPropertyValuePO convertPropertyValueDtoToPo(final UserPropertyDTO valueDto) {

        if (valueDto == null) {
            return null;
        }

        final UserPropertyPO  propertyPO = new UserPropertyPO();
        propertyPO.setName(valueDto.getName());
        propertyPO.setDisplayName(valueDto.getDisplayName());
        propertyPO.setId(valueDto.getId());

        final UserPropertyValuePO valuePO = new UserPropertyValuePO();
        valuePO.setProperty(propertyPO);
        valuePO.setValue(valueDto.getValue());

        return valuePO;
    }

    /**
     * Convert property values dto to po.
     *
     * @param valueDtos the value dtos
     * @return the list
     */
    public static List<UserPropertyValuePO> convertPropertyValuesDtoToPo(final List<UserPropertyDTO> valueDtos) {

        if (CollectionUtils.isEmpty(valueDtos)) {
            return Collections.emptyList();
        }

        return valueDtos.stream()
                .map(UsersConverter::convertPropertyValueDtoToPo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Convert AP is dto to ro.
     *
     * @param source the source
     * @return the list
     */
    public static List<UserAPIRO> convertAPIsDtoToRo(final List<? extends Endpoint> source) {

        if (source == null) {
            return null;
        }

        final List<UserAPIRO> target = new ArrayList<>();
        source.forEach(s -> target.add(convertAPIDtoToRo(s)));
        return target;
    }

    /**
     * Convert API dto to ro.
     *
     * @param source the source
     * @return the user APIRO
     */
    private static UserAPIRO convertAPIDtoToRo(Endpoint source) {
        if (source == null) {
            return null;
        }
        UserAPIRO target = new UserAPIRO();
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
		return target;
	}

	/**
     * Convert properties.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<UserPropertyRO> convertPropertiesDtoToRo(final List<? extends CustomProperty> source) {

        if (source == null) {
            return null;
        }

        final List<UserPropertyRO> target = new ArrayList<>();
        source.forEach(s -> target.add(convertPropertyDtoToRo(s)));
        return target;
    }

    /**
     * Convert security data source.
     *
     * @param source the source
     * @return the security data source RO
     */
    public static SecurityDataSourceRO convertSecurityDataSource(SecurityDataSource source) {

        if (Objects.isNull(source)) {
            return null;
        }

        SecurityDataSourceRO target = new SecurityDataSourceRO();
        target.setName(source.getName());
        target.setDescription(source.getDescription());

        return target;
    }

    /**
     * Convert security data sources.
     *
     * @param source the source
     * @return the list
     */
    public static List<SecurityDataSourceRO> convertSecurityDataSources(Collection<SecurityDataSource> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<SecurityDataSourceRO> target = new ArrayList<>();
        source.forEach(s -> target.add(convertSecurityDataSource(s)));
        return target;
    }

    /**
     * Convert property.
     *
     * @param source
     *            the source
     * @return the user property
     */
    public static UserPropertyRO convertPropertyDtoToRo(final CustomProperty source) {

        if (source == null) {
            return null;
        }

        final UserPropertyRO target = new UserPropertyRO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setValue(source.getValue());
        target.setId(UserPropertyDTO.class.isInstance(source) ? ((UserPropertyDTO) source).getId() : 0);
        return target;
    }

    /**
     * Convert properties.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<UserPropertyDTO> convertPropertiesRoToDto(final List<UserPropertyRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
            .map(UsersConverter::convertPropertyRoToDto)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Convert property.
     *
     * @param source
     *            the source
     * @return the user property
     */
    public static UserPropertyDTO convertPropertyRoToDto(final UserPropertyRO source) {

        if (source == null) {
            return null;
        }

        final UserPropertyDTO target = new UserPropertyDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setValue(source.getValue());
        target.setId(source.getId());
        return target;
    }

    /**
     * Convert labels.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<SecurityLabel> convertLabels(List<SecurityLabelRO> source) {
        if (source == null) {
            return null;
        }
        final List<SecurityLabel> target = new ArrayList<>();
        source.forEach(s -> target.add(convertLabel(s)));
        return target;
    }

    /**
     * Convert label.
     *
     * @param source
     *            the source
     * @return the security label dto
     */
    private static SecurityLabel convertLabel(SecurityLabelRO source) {
        if (source == null) {
            return null;
        }
        final SecurityLabelDTO target = new SecurityLabelDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setAttributes(convertAttributes(source.getAttributes()));
        return target;
    }

    /**
     * Convert attributes.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<SecurityLabelAttribute> convertAttributes(List<SecurityLabelAttributeRO> source) {
        if (source == null) {
            return null;
        }
        List<SecurityLabelAttribute> target = new ArrayList<>();
        source.forEach(s -> target.add(convertAttribute(s)));
        return target;
    }

    /**
     * Convert attribute.
     *
     * @param source
     *            the source
     * @return the security label attribute dto
     */
    private static SecurityLabelAttributeDTO convertAttribute(SecurityLabelAttributeRO source) {
        if (source == null) {
            return null;
        }
        SecurityLabelAttributeDTO target = new SecurityLabelAttributeDTO();
        target.setName(source.getName());
        target.setValue(source.getValue());
        return target;
    }

    /**
     * Convert user dt os.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<UserRO> convertUserDTOs(final List<UserDTO> source) {
        if (source == null) {
            return null;
        }
        final List<UserRO> target = new ArrayList<>();
        source.forEach(s -> target.add(convertUserDTO(s)));
        return target;
    }
}
