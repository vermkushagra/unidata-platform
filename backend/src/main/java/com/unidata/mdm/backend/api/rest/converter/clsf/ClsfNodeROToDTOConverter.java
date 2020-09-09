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
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;


/**
 * The Class ClsfNodeROToDTOConverter.
 */
public class ClsfNodeROToDTOConverter {

    /**
     * Convert.
     *
     * @param source the source
     * @return the clsf node DTO
     */
    public static ClsfNodeDTO convert(ClsfNodeRO source) {
        if (source == null) {
            return null;
        }
        ClsfNodeDTO target = new ClsfNodeDTO();
        target.setChildren(convert(source.getChildren()));
        target.setCode(source.getCode());
        target.setDescription(source.getDescription());
        target.setName(source.getName());
        target.setNodeSimpleAttrs(ClsfNodeAttrROToDTOConverter.convertSimpleAttrs(source.getNodeAttrs()));
        target.setNodeArrayAttrs(ClsfNodeAttrROToDTOConverter.convertArrayAttrs(source.getNodeArrayAttrs()));
        target.setNodeId(source.getId());
        target.setParentId(source.getParentId());
        target.setCustomProperties(source.getCustomProperties());
        return target;
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the list
     */
    public static List<ClsfNodeDTO> convert(List<ClsfNodeRO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfNodeDTO> target = new ArrayList<>();
        for (ClsfNodeRO element : source) {
            target.add(convert(element));
        }
        return target;
    }
}
