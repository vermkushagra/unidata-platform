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

import org.apache.commons.collections4.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;
import com.unidata.mdm.backend.api.rest.dto.CodeDataType;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeArrayAttrRO;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeAttrRO;
import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeSimpleAttrRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import org.apache.commons.lang3.StringUtils;


/**
 * The Class ClsfNodeAttrDTOToROConverter.
 */
public class ClsfNodeAttrDTOToROConverter {

    /**
     * Convert.
     *
     * @param source the source
     * @return the clsf node attr RO
     */
    private static <T extends ClsfNodeAttrRO> T convert(final ClsfNodeAttrDTO source, Supplier<T> objectSupplier) {
        T target = objectSupplier.get();
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setHidden(source.isHidden());
        target.setName(source.getAttrName());
        target.setNullable(source.isNullable());
        target.setReadOnly(source.isReadOnly());
        target.setSearchable(source.isSearchable());
        target.setLookupEntityType(source.getLookupEntityType());
        if (source.getLookupEntityCodeAttributeType() != null) {
            target.setLookupEntityCodeAttributeType(CodeDataType.valueOf(source.getLookupEntityCodeAttributeType().name()));
        }

        target.setUnique(source.isUnique());
        target.setOrder(source.getOrder());
        target.setCustomProperties(source.getCustomProperties());
        return target;
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the clsf node attr RO
     */
    public static ClsfNodeSimpleAttrRO convertSimpleAttr(final ClsfNodeSimpleAttrDTO source) {
        if (source == null) {
            return null;
        }
        final ClsfNodeSimpleAttrRO target = convert(source, ClsfNodeSimpleAttrRO::new);

        if (StringUtils.isBlank(source.getEnumDataType())
                && StringUtils.isBlank(source.getLookupEntityType())
                && source.getDataType() != null) {
            target.setSimpleDataType(SimpleDataType.fromValue(source.getDataType().name()));
        }

        target.setEnumDataType(source.getEnumDataType());
        target.setValueObj(source.getDefaultValue());

        return target;
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the list
     */
    public static List<ClsfNodeSimpleAttrRO> convertSimpleAttrs(List<ClsfNodeSimpleAttrDTO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfNodeSimpleAttrRO> target = new ArrayList<>();
        for (ClsfNodeSimpleAttrDTO element : source) {
            target.add(convertSimpleAttr(element));
        }
        return target;
    }

    public static ClsfNodeArrayAttrRO convertArrayAttr(final ClsfNodeArrayAttrDTO source) {
        if (source == null) {
            return null;
        }
        final ClsfNodeArrayAttrRO target = convert(source, ClsfNodeArrayAttrRO::new);

        if (source.getDataType() != null) {
            target.setArrayDataType(ArrayDataType.fromValue(source.getDataType().name()));
        }

        target.setValuesObj(source.getValues());

        return target;
    }

    public static List<ClsfNodeArrayAttrRO> convertArrayAttrs(final List<ClsfNodeArrayAttrDTO> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(ClsfNodeAttrDTOToROConverter::convertArrayAttr)
                .collect(Collectors.toList());
    }
}
