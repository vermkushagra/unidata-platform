package org.unidata.mdm.core.convert.security;

import static org.unidata.mdm.core.util.SecurityUtils.convertSecurityLabels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.core.dto.RoleDTO;
import org.unidata.mdm.core.dto.RolePropertyDTO;
import org.unidata.mdm.core.dto.SecurityLabelAttributeDTO;
import org.unidata.mdm.core.dto.SecurityLabelDTO;
import org.unidata.mdm.core.po.security.LabelAttributePO;
import org.unidata.mdm.core.po.security.LabelAttributeValuePO;
import org.unidata.mdm.core.po.security.LabelPO;
import org.unidata.mdm.core.po.security.RolePO;
import org.unidata.mdm.core.po.security.RolePropertyValuePO;
import org.unidata.mdm.core.type.security.CustomProperty;
import org.unidata.mdm.core.type.security.RoleType;
import org.unidata.mdm.core.type.security.SecurityLabel;
import org.unidata.mdm.core.type.security.SecurityLabelAttribute;

/**
 * @author mikhail
 * Roles PO* objects converter.
 */
public final class RoleConverter {


    /**
     * Disable instances.
     */
    private RoleConverter() {
        super();
    }

    /**
     * Convert role po.
     *
     * @param source
     *            the source
     * @return the role dto
     */
    public static RoleDTO convertRole(RolePO source) {

        if (source == null) {
            return null;
        }

        final RoleDTO target = new RoleDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        if (source.getRType() != null) {
            target.setRoleType(RoleType.valueOf(source.getRType()));
        }
        target.setRights(RightConverter.convertRightsPoToDto(source.getConnectedResourceRights()));
        final List<SecurityLabel> securityLabels = convertSecurityLabels(source.getLabelAttributeValues());
        target.setSecurityLabels(addSecurityLabelsWithoutValues(securityLabels, source.getLabelPOs()));
        target.setProperties(convertPropertyValues(source.getProperties()));
        return target;
    }


    private static List<SecurityLabel> addSecurityLabelsWithoutValues(List<SecurityLabel> securityLabels, List<LabelPO> labelPOs) {
        if (CollectionUtils.isEmpty(labelPOs)) {
            return securityLabels;
        }
        final List<SecurityLabel> result = new ArrayList<>(securityLabels);
        final Set<String> existLabelNames = securityLabels.stream()
                .map(SecurityLabel::getName)
                .collect(Collectors.toSet());
        result.addAll(
                labelPOs.stream()
                        .filter(l -> !existLabelNames.contains(l.getName()))
                        .map(RoleConverter::convertLabel)
                        .collect(Collectors.toList())
        );
        return result;
    }

    /**
     * Convert labels.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecurityLabel> convertLabels(final List<LabelPO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
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
    public static SecurityLabel convertLabel(LabelPO source) {

        if (source == null) {
            return null;
        }

        SecurityLabelDTO target = new SecurityLabelDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.setAttributes(convertAttributes(source.getLabelAttribute()));
        return target;
    }

    /**
     * Convert attributes.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<SecurityLabelAttribute> convertAttributes(List<LabelAttributePO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<SecurityLabelAttribute> target = new ArrayList<>();
        source.forEach(s -> {
                    if (CollectionUtils.isEmpty(s.getLabelAttributeValues())) {
                        target.add(convertAttribute(s));
                    } else {
                        target.addAll(convertAttributeValues(s.getLabelAttributeValues()));
                    }
                }
        );
        return target;
    }

    private static Collection<SecurityLabelAttribute> convertAttributeValues(Collection<LabelAttributeValuePO> labelAttributeValues) {
        return labelAttributeValues.stream()
                .map(lav ->
                        new SecurityLabelAttributeDTO(
                                lav.getId(),
                                lav.getLabelAttribute().getName(),
                                lav.getLabelAttribute().getPath(),
                                lav.getValue(),
                                lav.getLabelAttribute().getDescription()
                        )
                )
                .collect(Collectors.toList());
    }

    /**
     * Convert attribute.
     *
     * @param source
     *            the source
     * @return the security label attribute dto
     */
    public static SecurityLabelAttributeDTO convertAttribute(LabelAttributePO source) {

        if (source == null) {
            return null;
        }

        SecurityLabelAttributeDTO target = new SecurityLabelAttributeDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPath(source.getPath());
        target.setValue(null);
        target.setDescription(source.getDescription());
        return target;
    }







    /**
     *
     * @param valuePO
     * @return
     */
    public static RolePropertyDTO convertPropertyValue(RolePropertyValuePO valuePO) {

        if (valuePO == null) {
            return null;
        }

        RolePropertyDTO dto = new RolePropertyDTO();
        if (valuePO.getProperty() != null) {
            dto.setId(valuePO.getProperty().getId());
            dto.setName(valuePO.getProperty().getName());
            dto.setDisplayName(valuePO.getProperty().getDisplayName());
            dto.setRequired(valuePO.getProperty().isRequired());
        }

        dto.setValue(valuePO.getValue());
        return dto;
    }

    /**
     *
     * @param valuePOs
     * @return
     */
    public static List<CustomProperty> convertPropertyValues(List<RolePropertyValuePO> valuePOs) {

        if (valuePOs == null) {
            return Collections.emptyList();
        }

        return valuePOs.stream()
                .map(RoleConverter::convertPropertyValue)
                .filter(Objects::nonNull)
                .map(v -> (CustomProperty) v)
                .collect(Collectors.toList());
    }

    public static List<RoleDTO> convertRoles(Collection<RolePO> roles) {
        return roles.stream().map(RoleConverter::convertRole).collect(Collectors.toList());
    }




}
