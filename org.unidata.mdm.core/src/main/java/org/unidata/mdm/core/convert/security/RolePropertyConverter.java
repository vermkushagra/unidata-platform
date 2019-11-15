package org.unidata.mdm.core.convert.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.dto.RolePropertyDTO;
import org.unidata.mdm.core.po.security.RolePropertyPO;
import org.unidata.mdm.core.po.security.RolePropertyValuePO;
import org.unidata.mdm.core.type.security.CustomProperty;

public class RolePropertyConverter {

    private RolePropertyConverter() {
        super();
    }

    private static RolePropertyDTO convertPropertyPoToDto(RolePropertyPO propertyPO) {
        if (propertyPO == null) {
            return null;
        }

        RolePropertyDTO dto = new RolePropertyDTO();

        dto.setId(propertyPO.getId());
        dto.setRequired(propertyPO.isRequired());
        dto.setName(propertyPO.getName());
        dto.setDisplayName(propertyPO.getDisplayName());

        return dto;
    }

    public static List<RolePropertyDTO> convertPropertiesPoToDto(List<RolePropertyPO> propertyPOs) {
        if (propertyPOs == null) {
            return new ArrayList<>();
        }
        final List<RolePropertyDTO> target = new ArrayList<>();
        propertyPOs.forEach(s -> target.add(convertPropertyPoToDto(s)));
        return target;
    }

    public static RolePropertyPO convertPropertyDtoToPo(RolePropertyDTO propertyDTO) {
        if (propertyDTO == null) {
            return null;
        }

        RolePropertyPO po = new RolePropertyPO();
        po.setId(propertyDTO.getId());
        po.setName(StringUtils.trim(propertyDTO.getName()));
        po.setRequired(propertyDTO.isRequired());
        po.setDisplayName(StringUtils.trim(propertyDTO.getDisplayName()));

        return po;
    }

    public static List<RolePropertyPO> convertPropertiesDtoToPo(List<RolePropertyDTO> propertyDTOs) {
        if (propertyDTOs == null) {
            return new ArrayList<>();
        }

        final List<RolePropertyPO> target = new ArrayList<>();
        propertyDTOs.forEach(s -> target.add(convertPropertyDtoToPo(s)));
        return target;
    }


    public static RolePropertyValuePO convertPropertyValueDtoToPo(CustomProperty valueDto) {

        if (valueDto == null) {
            return null;
        }

        RolePropertyValuePO valuePO = new RolePropertyValuePO();

        RolePropertyPO propertyPO = new RolePropertyPO();
        propertyPO.setId(valueDto instanceof RolePropertyDTO ? ((RolePropertyDTO) valueDto).getId() : Long.valueOf(0l));
        propertyPO.setName(valueDto.getName());
        propertyPO.setDisplayName(valueDto.getDisplayName());

        valuePO.setProperty(propertyPO);
        valuePO.setValue(valueDto.getValue());

        return valuePO;
    }

    public static List<RolePropertyValuePO> convertPropertyValuesDtoToPo(List<CustomProperty> source) {

        if (source == null) {
            return Collections.emptyList();
        }

        return source.stream()
                .map(RolePropertyConverter::convertPropertyValueDtoToPo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
