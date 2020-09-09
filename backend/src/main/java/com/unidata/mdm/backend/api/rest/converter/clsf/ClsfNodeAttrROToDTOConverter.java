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
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeArrayAttrRO;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeSimpleAttrRO;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;


/**
 * The Class ClsfNodeAttrROToDTOConverter.
 */
public class ClsfNodeAttrROToDTOConverter {

    /**
     * Convert.
     *
     * @param source the source
     * @return the clsf node attr DTO
     */
    private static <T extends ClsfNodeAttrDTO> T convert(ClsfNodeAttrRO source, Supplier<T> objectSupplier) {
        if (source == null) {
            return null;
        }
        T target = objectSupplier.get();
        target.setAttrName(source.getName());
        target.setLookupEntityType(source.getLookupEntityType());
        if (source.getLookupEntityCodeAttributeType() != null) {
            target.setLookupEntityCodeAttributeType(
                    CodeAttribute.CodeDataType.valueOf(source.getLookupEntityCodeAttributeType().name())
            );
        }
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setHidden(source.isHidden());
        target.setNullable(source.isNullable());
        target.setReadOnly(source.isReadOnly());
        target.setSearchable(source.isSearchable());
        target.setUnique(source.isUnique());
        target.setOrder(source.getOrder());
        target.setCustomProperties(source.getCustomProperties());
        return target;
    }

    public static ClsfNodeSimpleAttrDTO convertSimpleAttr(ClsfNodeSimpleAttrRO source) {
        final ClsfNodeSimpleAttrDTO target = convert(source, ClsfNodeSimpleAttrDTO::new);
        if (StringUtils.isBlank(source.getLookupEntityType())
                && StringUtils.isBlank(source.getEnumDataType())
                && source.getSimpleDataType() != null) {
            target.setDataType(DataType.valueOf(source.getSimpleDataType().name()));
        }
        target.setEnumDataType(source.getEnumDataType());
        target.setDefaultValue(source.getValue());
        return target;
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the list
     */
    public static List<ClsfNodeSimpleAttrDTO> convertSimpleAttrs(List<ClsfNodeSimpleAttrRO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfNodeSimpleAttrDTO> target = new ArrayList<>();
        for (ClsfNodeSimpleAttrRO element : source) {
            target.add(convertSimpleAttr(element));
        }
        return target;
    }

    public static List<ClsfNodeArrayAttrDTO> convertArrayAttrs(List<ClsfNodeArrayAttrRO> nodeArrayAttrs) {
        if (CollectionUtils.isEmpty(nodeArrayAttrs)) {
            return Collections.emptyList();
        }
        return nodeArrayAttrs.stream()
                .map(ClsfNodeAttrROToDTOConverter::convertArrayAttr)
                .collect(Collectors.toList());
    }

    public static ClsfNodeArrayAttrDTO convertArrayAttr(ClsfNodeArrayAttrRO source) {
        final ClsfNodeArrayAttrDTO target = convert(source, ClsfNodeArrayAttrDTO::new);
        if (StringUtils.isBlank(source.getLookupEntityType()) && source.getArrayDataType() != null) {
            target.setDataType(DataType.valueOf(source.getArrayDataType().name()));
        }
        target.setValues(source.getValues());
        return target;
    }
}
