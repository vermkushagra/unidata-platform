/**
 *
 */
package com.unidata.mdm.backend.service.security.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.service.security.po.LabelAttributeValuePO;

import com.unidata.mdm.backend.common.dto.security.RightDTO;
import com.unidata.mdm.backend.common.dto.security.RoleDTO;
import com.unidata.mdm.backend.common.dto.security.RolePropertyDTO;
import com.unidata.mdm.backend.common.dto.security.SecuredResourceDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelAttributeDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.common.integration.auth.Role;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceCategory;
import com.unidata.mdm.backend.common.integration.auth.SecuredResourceType;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.backend.service.security.po.LabelAttributePO;
import com.unidata.mdm.backend.service.security.po.LabelPO;
import com.unidata.mdm.backend.service.security.po.ResourcePO;
import com.unidata.mdm.backend.service.security.po.ResourceRightPO;
import com.unidata.mdm.backend.service.security.po.RightPO;
import com.unidata.mdm.backend.service.security.po.RolePO;
import com.unidata.mdm.backend.service.security.po.RolePropertyValuePO;
import org.apache.commons.collections4.CollectionUtils;

import static com.unidata.mdm.backend.service.security.utils.SecurityUtils.convertSecurityLabels;

/**
 * @author mikhail
 * Roles PO* objects converter.
 */
public final class RoleConverter {

    /**
     * Create.
     */
    public static final String CREATE_LABEL = "CREATE";
    /**
     * Update.
     */
    public static final String UPDATE_LABEL = "UPDATE";
    /**
     * Delete.
     */
    public static final String DELETE_LABEL = "DELETE";
    /**
     * Read.
     */
    public static final String READ_LABEL = "READ";
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
    public static Role convertRole(RolePO source) {

        if (source == null) {
            return null;
        }

        final RoleDTO target = new RoleDTO();
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setRights(convertRights(source.getConnectedResourceRights()));
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
                    if  (CollectionUtils.isEmpty(s.getLabelAttributeValues())) {
                        target.add(convertAttribute(s));
                    }
                    else {
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
     * Convert rights.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<Right> convertRights(List<ResourceRightPO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<Right> target = new ArrayList<>();
        Map<ResourcePO, List<RightPO>> map = new HashMap<>();
        for (ResourceRightPO rr : source) {
            if (map.containsKey(rr.getResource())) {
                map.get(rr.getResource()).add(rr.getRight());
            } else {
                List<RightPO> list = new ArrayList<>();
                list.add(rr.getRight());
                map.put(rr.getResource(), list);
            }
        }

        Set<ResourcePO> pos = map.keySet();
        for (ResourcePO po : pos) {
            RightDTO dto = new RightDTO();
            SecuredResourceDTO ssd = new SecuredResourceDTO();
            ssd.setName(po.getName());
            ssd.setDisplayName(po.getDisplayName());
            ssd.setType(SecuredResourceType.valueOf(po.getRType()));
            ssd.setCategory(SecuredResourceCategory.valueOf(po.getCategory()));
            dto.setSecuredResource(ssd);
            List<RightPO> list = map.get(po);
            for (RightPO rightPO : list) {
                if (CREATE_LABEL.equals(rightPO.getName())) {
                    dto.setCreate(true);
                } else if (READ_LABEL.equals(rightPO.getName())) {
                    dto.setRead(true);
                } else if (DELETE_LABEL.equals(rightPO.getName())) {
                    dto.setDelete(true);
                } else if (UPDATE_LABEL.equals(rightPO.getName())) {
                    dto.setUpdate(true);
                }
            }
            target.add(dto);
        }

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
}
