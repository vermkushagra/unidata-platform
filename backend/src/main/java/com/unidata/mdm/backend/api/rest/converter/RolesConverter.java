package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.security.ResourceSpecificRightRO;
import com.unidata.mdm.backend.api.rest.dto.security.RightRO;
import com.unidata.mdm.backend.api.rest.dto.security.RolePropertyRO;
import com.unidata.mdm.backend.api.rest.dto.security.RoleRO;
import com.unidata.mdm.backend.api.rest.dto.security.RoleTypeRO;
import com.unidata.mdm.backend.api.rest.dto.security.SecuredResourceCategoryRO;
import com.unidata.mdm.backend.api.rest.dto.security.SecuredResourceRO;
import com.unidata.mdm.backend.api.rest.dto.security.SecuredResourceTypeRO;
import com.unidata.mdm.backend.api.rest.dto.security.SecurityLabelAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.security.SecurityLabelRO;
import com.unidata.mdm.backend.common.dto.security.ResourceSpecificRightDTO;
import com.unidata.mdm.backend.common.dto.security.RightDTO;
import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelAttributeDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.RoleType;
import com.unidata.mdm.backend.common.integration.auth.SecuredResource;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceType;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.backend.service.security.po.RolePropertyPO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO;

// TODO: Auto-generated Javadoc
/**
 * The Class RolesConverter.
 */
public class RolesConverter {

    /**
     * Convert role ro.
     *
     * @param source
     *            the source
     * @return the role dtof
     */
    public static RoleDTO convertRoleRO(RoleRO source) {

        if (source == null) {
            return null;
        }

        final RoleDTO target = new RoleDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setRoleType(RoleType.valueOf(source.getType().name()));
        target.setRights(convertRightROs(source.getRights()));
        target.setSecurityLabels(from(source.getSecurityLabels()));
        target.setProperties(convertPropertiesValuesRoToDto(source.getProperties()));

        return target;
    }

    /**
     * Convert role r os.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<Role> convertRoleROs(List<RoleRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
            .map(RolesConverter::convertRoleRO)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Convert role dto.
     *
     * @param source
     *            the source
     * @return the role ro
     */
    public static RoleRO convertRoleDTO(Role source) {

        if (source == null) {
            return null;
        }

        final RoleRO target = new RoleRO();
        if (source instanceof RoleDTO) {
            target.setCreatedAt(((RoleDTO) source).getCreatedAt());
            target.setCreatedBy(((RoleDTO) source).getCreatedBy());
            target.setUpdatedAt(((RoleDTO) source).getUpdatedAt());
            target.setUpdatedBy(((RoleDTO) source).getUpdatedBy());
        }

        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setRights(convertRightDTOs(source.getRights()));
        target.setSecurityLabels(convertSecurityLabelDTOs(source.getSecurityLabels()));
        target.setProperties(convertPropertiesValuesDTOToRO(source.getProperties()));
        if (source.getRoleType() == null) {
            target.setType(RoleTypeRO.USER_DEFINED);
        } else {
            target.setType(RoleTypeRO.valueOf(source.getRoleType().name()));
        }

        return target;
    }

    /**
     * Convert role dt os.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<RoleRO> convertRoleDTOs(final List<Role> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
                .map(RolesConverter::convertRoleDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Convert rights.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<Right> convertRightROs(List<RightRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
                .map(RolesConverter::convertRightRO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Convert right ro.
     *
     * @param source
     *            the source
     * @return the right dto
     */
    public static RightDTO convertRightRO(RightRO source) {
        if (source == null) {
            return null;
        }
        RightDTO target = new RightDTO();
        target.setDelete(source.isDelete());
        target.setCreate(source.isCreate());
        target.setRead(source.isRead());
        target.setUpdate(source.isUpdate());
        target.setSecuredResource(convertSecuredResourceRO(source.getSecuredResource()));
        return target;
    }

    /**
     * Convert secured resource.
     *
     * @param source
     *            the source
     * @return the secured resource dto
     */
    private static SecuredResourceDTO convertSecuredResourceRO(SecuredResourceRO source) {

        if (source == null) {
            return null;
        }

        SecuredResourceDTO target = new SecuredResourceDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setType(SecuredResourceType.valueOf(source.getType().name()));
        target.setCategory(SecuredResourceCategory.valueOf(source.getCategory().name()));

        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        return target;
    }

    /**
     * Convert security label dt os.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecurityLabelRO> convertSecurityLabelDTOs(final List<SecurityLabel> source) {
        if (source == null || source.size() == 0) {
            return null;
        }
        List<SecurityLabelRO> target = new ArrayList<SecurityLabelRO>();
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
    public static SecurityLabelRO convertSecurityLabelDTO(SecurityLabel source) {
        if (source == null) {
            return null;
        }
        SecurityLabelRO target = new SecurityLabelRO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.setAttributes(convertSecurityAttributeDTOs(source.getAttributes()));
        return target;
    }

    public static SecurityLabel from(SecurityLabelRO source) {

        if (Objects.isNull(source)) {
            return null;
        }

        final SecurityLabelDTO target = new SecurityLabelDTO();
        target.setName(source.getName());
        target.setAttributes(convertLabelAttributesRO(source.getAttributes()));
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());

        return target;
    }

    public static List<SecurityLabel> from(List<SecurityLabelRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        final List<SecurityLabel> labels = new ArrayList<>();
        for (final SecurityLabelRO securityLabelRO : source) {
            labels.add(from(securityLabelRO));
        }

        return labels;
    }

    /**
     * Convert properties.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<CustomProperty> convertPropertiesValuesRoToDto(List<RolePropertyRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
            .map(RolesConverter::convertPropertyRoToDto)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Convert property.
     *
     * @param source
     *            the source
     * @return the role property
     */
    public static CustomProperty convertPropertyValueRoToDto(RolePropertyRO source) {

        if (source == null) {
            return null;
        }

        RolePropertyDTO target = new RolePropertyDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setValue(source.getValue());
        target.setId(source.getId());
        return target;
    }

    /**
     * Convert properties.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<RolePropertyRO> convertPropertiesValuesDTOToRO(List<CustomProperty> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
            .map(RolesConverter::convertPropertyValueDTOToRO)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Convert property.
     *
     * @param source
     *            the source
     * @return the role property
     */
    public static RolePropertyRO convertPropertyValueDTOToRO(CustomProperty source) {

        if (source == null) {
            return null;
        }

        RolePropertyRO target = new RolePropertyRO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setValue(source.getValue());
        target.setId(RolePropertyDTO.class.isInstance(source) ? ((RolePropertyDTO) source).getId() : -1);
        return target;
    }

    /**
     * Convert security attribute dt os.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<SecurityLabelAttributeRO> convertSecurityAttributeDTOs(List<SecurityLabelAttribute> source) {
        if (source == null || source.size() == 0) {
            return null;
        }
        List<SecurityLabelAttributeRO> target = new ArrayList<SecurityLabelAttributeRO>();
        source.forEach(s -> target.add(convertSecurityLabelAttributeDTO(s)));
        return target;
    }

    /**
     * Convert security label attribute dto.
     *
     * @param source
     *            the source
     * @return the security label attribute ro
     */
    private static SecurityLabelAttributeRO convertSecurityLabelAttributeDTO(SecurityLabelAttribute source) {
        if (source == null) {
            return null;
        }
        SecurityLabelAttributeRO target = new SecurityLabelAttributeRO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setValue(source.getValue());
        target.setPath(source.getPath());
        return target;
    }

    /**
     * Convert right dt os.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<RightRO> convertRightDTOs(List<Right> source) {

        if (CollectionUtils.isEmpty(source)) {
            return null;
        }

        List<RightRO> target = new ArrayList<>();
        source.forEach(s -> target.add(convertRightDTO(s)));
        return target;
    }

    /**
     * Convert right dto.
     *
     * @param source
     *            the source
     * @return the right ro
     */
    public static RightRO convertRightDTO(Right source) {
        if (source == null) {
            return null;
        }

        RightRO target = new RightRO();
        target.setCreate(source.isCreate());
        if (source instanceof RightDTO) {
            target.setCreatedAt(((RightDTO)source).getCreatedAt());
            target.setCreatedBy(((RightDTO)source).getCreatedBy());
            target.setUpdatedAt(((RightDTO)source).getUpdatedAt());
            target.setUpdatedBy(((RightDTO)source).getUpdatedBy());
        }
        target.setDelete(source.isDelete());
        target.setRead(source.isRead());
        target.setSecuredResource(convertSecuredResourceDTO(source.getSecuredResource()));
        target.setUpdate(source.isUpdate());
        return target;
    }

    /**
     * Convert right dto.
     *
     * @param source
     *            the source
     * @return the right ro
     */
    public static ResourceSpecificRightRO convertResourceSpecificRights(ResourceSpecificRightDTO source) {

        if (source == null) {
            return null;
        }

        ResourceSpecificRightRO target = new ResourceSpecificRightRO();
        target.setCreate(source.isCreate());
        target.setDelete(source.isDelete());
        target.setRead(source.isRead());
        target.setUpdate(source.isUpdate());
        target.setMerge(source.isMerge());
        target.setRestore(source.isRestore());

        target.setSecuredResource(convertSecuredResourceDTO(source.getSecuredResource()));

        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());

        return target;
    }

    /**
     * Convert secured resource dto.
     *
     * @param source
     *            the source
     * @return the secured resource ro
     */
    private static SecuredResourceRO convertSecuredResourceDTO(final SecuredResource source) {

        if (source == null) {
            return null;
        }

        final SecuredResourceRO target = new SecuredResourceRO();

        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setCategory(source.getCategory() == null
                ? SecuredResourceCategoryRO.SYSTEM
                : SecuredResourceCategoryRO.valueOf(source.getCategory().name()));
        target.setType(source.getType() == null
                ? SecuredResourceTypeRO.SYSTEM
                : SecuredResourceTypeRO.valueOf(source.getType().name()));

        if (source instanceof SecuredResourceDTO) {

            SecuredResourceDTO srDTO = (SecuredResourceDTO) source;

            target.setCreatedAt(srDTO.getCreatedAt());
            target.setCreatedBy(srDTO.getCreatedBy());
            target.setUpdatedAt(srDTO.getUpdatedAt());
            target.setUpdatedBy(srDTO.getUpdatedBy());

            List<SecuredResourceRO> children = new ArrayList<>();
            srDTO.getChildren().forEach(child -> {
                SecuredResourceRO childRO = convertSecuredResourceDTO(child);
                if (childRO != null) {
                    children.add(childRO);
                }
            });

            target.setChildren(CollectionUtils.isEmpty(children) ? null : children);
        }

        return target;
    }

    /**
     * Convert secured resource dt os.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecuredResourceRO> convertSecuredResourceDTOs(List<SecuredResourceDTO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<SecuredResourceRO> target = new ArrayList<>();
        source.forEach(s -> target.add(convertSecuredResourceDTO(s)));
        return target;
    }

    /**
     * Convert security label ro.
     *
     * @param source
     *            the source
     * @return the security label dto
     */
    public static SecurityLabel convertSecurityLabelRO(SecurityLabelRO source) {
        if (source == null) {
            return null;
        }
        SecurityLabelDTO target = new SecurityLabelDTO();
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setAttributes(convertLabelAttributesRO(source.getAttributes()));
        return target;
    }

    /**
     * Convert label attributes ro.
     *
     * @param source
     *            the source
     * @return the list
     */
    private static List<SecurityLabelAttribute> convertLabelAttributesRO(List<SecurityLabelAttributeRO> source) {
        if (source == null) {
            return null;
        }
        List<SecurityLabelAttribute> target = new ArrayList<>();
        source.forEach(s -> target.add(convertAttributeRO(s)));
        return target;
    }

    /**
     * Convert attribute ro.
     *
     * @param source
     *            the source
     * @return the security label attribute dto
     */
    private static SecurityLabelAttributeDTO convertAttributeRO(SecurityLabelAttributeRO source) {
        if (source == null) {
            return null;
        }
        SecurityLabelAttributeDTO target = new SecurityLabelAttributeDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPath(source.getPath());
        target.setDescription(source.getDescription());
        target.setValue(source.getValue());
        return target;
    }

    /**
     *
     * @param propertyPO
     * @return
     */
    public static RolePropertyDTO convertPropertyPoToDto(RolePropertyPO propertyPO) {
        if (propertyPO == null) {
            return null;
        }

        RolePropertyDTO dto = new RolePropertyDTO();

        dto.setId(propertyPO.getId());
        dto.setName(propertyPO.getName());
        dto.setDisplayName(propertyPO.getDisplayName());

        return dto;
    }

    /**
     *
     * @param propertyPOs
     * @return
     */
    public static List<RolePropertyDTO> convertPropertiesPoToDto(List<RolePropertyPO> propertyPOs) {
        if (propertyPOs == null) {
            return null;
        }
        final List<RolePropertyDTO> target = new ArrayList<>();
        propertyPOs.forEach(s -> target.add(convertPropertyPoToDto(s)));
        return target;
    }

    /**
     *
     * @param propertyDTO
     * @return
     */
    public static RolePropertyPO convertPropertyDtoToPo(RolePropertyDTO propertyDTO) {
        if (propertyDTO == null) {
            return null;
        }

        RolePropertyPO po = new RolePropertyPO();
        po.setId(propertyDTO.getId());
        po.setName(propertyDTO.getName());
        po.setDisplayName(propertyDTO.getDisplayName());

        return po;
    }

    /**
     *
     * @param propertyDTOs
     * @return
     */
    public static List<RolePropertyPO> convertPropertiesDtoToPo(List<RolePropertyDTO> propertyDTOs) {
        if (propertyDTOs == null) {
            return null;
        }

        final List<RolePropertyPO> target = new ArrayList<>();
        propertyDTOs.forEach(s -> target.add(convertPropertyDtoToPo(s)));
        return target;
    }

    /**
     *
     * @param valueDto
     * @return
     */
    public static RolePropertyValuePO convertPropertyValueDtoToPo(CustomProperty valueDto) {

        if (valueDto == null) {
            return null;
        }

        RolePropertyValuePO valuePO = new RolePropertyValuePO();

        RolePropertyPO  propertyPO = new RolePropertyPO();
        propertyPO.setId(RolePropertyDTO.class.isInstance(valueDto) ? ((RolePropertyDTO) valueDto).getId() : 0);
        propertyPO.setName(valueDto.getName());
        propertyPO.setDisplayName(valueDto.getDisplayName());

        valuePO.setProperty(propertyPO);
        valuePO.setValue(valueDto.getValue());

        return valuePO;
    }

    /**
     *
     * @param source
     * @return
     */
    public static List<RolePropertyValuePO> convertPropertyValuesDtoToPo(List<CustomProperty> source) {

        if (source == null) {
            return Collections.emptyList();
        }

        return source.stream()
                .map(RolesConverter::convertPropertyValueDtoToPo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Convert properties.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<RolePropertyRO> convertPropertiesDtoToRo(final List<RolePropertyDTO> source) {
        if (source == null) {
            return null;
        }
        final List<RolePropertyRO> target = new ArrayList<>();
        source.forEach(s -> target.add(convertPropertyDtoToRo(s)));
        return target;
    }

    /**
     * Convert property.
     *
     * @param source
     *            the source
     * @return the role property
     */
    public static RolePropertyRO convertPropertyDtoToRo(final RolePropertyDTO source) {
        if (source == null) {
            return null;
        }
        RolePropertyRO target = new RolePropertyRO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setValue(source.getValue());
        target.setId(source.getId());
        return target;
    }

    /**
     * Convert properties.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<RolePropertyDTO> convertPropertiesRoToDto(List<RolePropertyRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        return source.stream()
            .map(RolesConverter::convertPropertyRoToDto)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Convert property.
     *
     * @param source
     *            the source
     * @return the role property
     */
    public static RolePropertyDTO convertPropertyRoToDto(RolePropertyRO source) {

        if (source == null) {
            return null;
        }

        RolePropertyDTO target = new RolePropertyDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setValue(source.getValue());
        target.setId(source.getId());
        return target;
    }

}
