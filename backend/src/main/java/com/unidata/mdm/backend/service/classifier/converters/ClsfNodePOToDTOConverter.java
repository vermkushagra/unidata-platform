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

package com.unidata.mdm.backend.service.classifier.converters;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;


/**
 * The Class ClsfNodePOToDTOConverter.
 */
public class ClsfNodePOToDTOConverter {

    /**
     * Instantiates a new clsf node PO to DTO converter.
     */
    private ClsfNodePOToDTOConverter() {
        super();
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the clsf node DTO
     */
    public static ClsfNodeDTO convert(ClsfNodePO source) {

        if (source == null) {
            return null;
        }

        ClsfNodeDTO target = new ClsfNodeDTO();
        target.setClsfName(source.getClsfName());
        target.setChildren(convert(source.getChildren()));
        target.setCode(source.getCode());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setNodeSimpleAttrs(ClsfNodeAttrPOToDTOConverter.convertSimpleAttrs(source.getNodeSimpleAttrs(), source.getId()));
        target.setNodeArrayAttrs(ClsfNodeAttrPOToDTOConverter.convertArrayAttrs(source.getNodeArrayAttrs(), source.getId()));
        target.setNodeId(source.getNodeId());
        target.setParentId(source.getParentId());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setChildCount(source.getChildCount());
        target.setHasOwnAttrs(source.isHasOwnAttrs());
        target.setCustomProperties(ClsfCustomPropertyToPOConverter.convert(source.getCustomProperties()));
        return target;
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the list
     */
    public static List<ClsfNodeDTO> convert(List<ClsfNodePO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfNodeDTO> target = new ArrayList<>();
        for (ClsfNodePO element : source) {
            target.add(convert(element));
        }
        return target;
    }
}
