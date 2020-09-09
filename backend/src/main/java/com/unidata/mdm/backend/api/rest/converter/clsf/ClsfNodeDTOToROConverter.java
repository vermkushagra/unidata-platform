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

package com.unidata.mdm.backend.api.rest.converter.clsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import org.apache.commons.lang3.StringUtils;


/**
 * The Class ClsfNodeDTOToROConverter.
 */
public class ClsfNodeDTOToROConverter {

    /** The Constant CLSF_NODE_COMPARATOR. */
    private static final Comparator<ClsfNodeRO> CLSF_NODE_COMPARATOR = (o1, o2) -> {
        if (o1 == null || o2 == null) {
            return 0;
        }
        if (StringUtils.isNoneBlank(o1.getCode(), o2.getCode())) {
            return o1.getCode().compareTo(o2.getCode());
        }
        else if (StringUtils.isBlank(o1.getCode()) && StringUtils.isNotBlank(o2.getCode())) {
            return 1;
        }
        else if (StringUtils.isBlank(o2.getCode()) && StringUtils.isNotBlank(o1.getCode())) {
            return -1;
        }
        if(o1.getName() == null || o2.getName() == null){
            return 0;
        }
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    };

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @param clsfName
     *            the clsf name
     * @return the clsf node RO
     */
    public static ClsfNodeRO convert(ClsfNodeDTO source, String clsfName) {
        if (source == null) {
            return null;
        }
        ClsfNodeRO target = new ClsfNodeRO();
        target.setChildCount(source.getChildCount());
        target.setChildren(convert(source.getChildren(), clsfName));
        target.setClassifierName(clsfName);
        target.setCode(source.getCode());
        target.setDescription(source.getDescription());
        target.setId(source.getNodeId());

        List<ClsfNodeSimpleAttrDTO> inheritedSimpleAttrs = source.getNodeSimpleAttrs().stream()
                .filter(ClsfNodeSimpleAttrDTO::isInherited)
                .collect(Collectors.toList());
        Map<String, ClsfNodeSimpleAttrDTO> map = new HashMap<>();
        for (ClsfNodeSimpleAttrDTO ia : inheritedSimpleAttrs) {
            if (!map.containsKey(ia.getAttrName()) || ia.getDefaultValue() != null) {
                map.put(ia.getAttrName(), ia);
            }
        }
        inheritedSimpleAttrs.clear();
        inheritedSimpleAttrs.addAll(new ArrayList<>(map.values()));
        List<ClsfNodeSimpleAttrDTO> ownSimpleAttrs = source.getNodeSimpleAttrs().stream().filter(na -> !na.isInherited())
                .collect(Collectors.toList());
        target.setInheritedNodeAttrs(ClsfNodeAttrDTOToROConverter.convertSimpleAttrs(inheritedSimpleAttrs));
        target.setNodeAttrs(ClsfNodeAttrDTOToROConverter.convertSimpleAttrs(ownSimpleAttrs));

        final List<ClsfNodeArrayAttrDTO> inheritedArrayAttrs = source.getNodeArrayAttrs().stream()
                .filter(ClsfNodeAttrDTO::isInherited)
                .collect(Collectors.toList());
        final List<ClsfNodeArrayAttrDTO> ownArrayAttrs = source.getNodeArrayAttrs().stream()
                .filter(a -> !a.isInherited())
                .collect(Collectors.toList());
        final Map<String, ClsfNodeArrayAttrDTO> arrayAttrsMap = new HashMap<>();
        for (ClsfNodeArrayAttrDTO ia : inheritedArrayAttrs) {
            if (!arrayAttrsMap.containsKey(ia.getAttrName()) || ia.getValues() != null) {
                arrayAttrsMap.put(ia.getAttrName(), ia);
            }
        }
        inheritedArrayAttrs.clear();
        inheritedArrayAttrs.addAll(new ArrayList<>(arrayAttrsMap.values()));
        target.setInheritedNodeArrayAttrs(ClsfNodeAttrDTOToROConverter.convertArrayAttrs(inheritedArrayAttrs));
        target.setNodeArrayAttrs(ClsfNodeAttrDTOToROConverter.convertArrayAttrs(ownArrayAttrs));

        target.setName(source.getName());
        target.setOwnNodeAttrs(source.isHasOwnAttrs());
        target.setParentId(source.getParentId());
        target.setCustomProperties(source.getCustomProperties() == null ? Collections.emptyList() : source.getCustomProperties());
        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @param clsfName
     *            the clsf name
     * @return the list
     */
    public static List<ClsfNodeRO> convert(List<ClsfNodeDTO> source, String clsfName) {
        if (source == null) {
            return null;
        }
        List<ClsfNodeRO> target = new ArrayList<>();
        for (ClsfNodeDTO element : source) {
            target.add(convert(element, clsfName));
        }
        target.sort(CLSF_NODE_COMPARATOR);
        return target;
    }
}
