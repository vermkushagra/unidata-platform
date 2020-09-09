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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeArrayAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeSimpleAttribute;

/**
 * The Class ClsfNodeAttrDTOToPOConverter.
 */
public class ClsfNodeAttrDTOToCachedClassifierNodeAttributeConverter {

	/**
	 * Instantiates a new clsf node attr DTO to PO converter.
	 */
	private ClsfNodeAttrDTOToCachedClassifierNodeAttributeConverter() {
		super();
	}

	/**
     * Does sets common part for all types of attributes.
     * @param target the target
     * @param source the source
     */
    private static void convert(CachedClassifierNodeAttribute target, ClsfNodeAttrDTO source) {
        target.setName(source.getAttrName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.setReadOnly(source.isReadOnly());
        target.setHidden(source.isHidden());
        target.setNullable(source.isNullable());
        target.setUnique(source.isUnique());
        target.setSearchable(source.isSearchable());
        target.setOrder(source.getOrder());
        target.setCustomProperties(ClsfCustomPropertyPOToCachedClassifierPropertyConverter.convert(source.getCustomProperties()));
    }
    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr DTO
     */
    public static CachedClassifierNodeAttribute convert(ClsfNodeArrayAttrDTO source) {

        if (source == null) {
            return null;
        }

        final CachedClassifierNodeArrayAttribute target = new CachedClassifierNodeArrayAttribute();

        // Common
        convert(target, source);

        // Array stuff
        if (StringUtils.isNotBlank(source.getLookupEntityType())
         && Objects.nonNull(source.getLookupEntityCodeAttributeType())) {
            target.setDataType(ArrayDataType.valueOf(source.getLookupEntityCodeAttributeType().name()));
            target.setLookupLink(true);
            target.setLookupName(source.getLookupEntityType());
        } else {
            target.setDataType(Objects.nonNull(source.getDataType()) ? ArrayDataType.valueOf(source.getDataType().name()) : null);
        }

        target.setValues(source.getValues().stream()
                .map(v -> (Serializable) v)
                .filter(Objects::nonNull)
                .toArray(size -> new Serializable[size]));

        return target;
    }

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr DTO
     */
    public static CachedClassifierNodeAttribute convert(ClsfNodeSimpleAttrDTO source) {

        if (source == null) {
            return null;
        }

        final CachedClassifierNodeSimpleAttribute target = new CachedClassifierNodeSimpleAttribute();

        // Common
        convert(target, source);

        // Simple stuff
        if (StringUtils.isNotBlank(source.getLookupEntityType())
         && Objects.nonNull(source.getLookupEntityCodeAttributeType())) {
            target.setDataType(DataType.valueOf(source.getLookupEntityCodeAttributeType().name()));
            target.setLookupLink(true);
            target.setLookupName(source.getLookupEntityType());
        } else if (StringUtils.isNotBlank(source.getEnumDataType())) {
            target.setDataType(DataType.STRING);
            target.setEnumLink(true);
            target.setEnumName(source.getEnumDataType());
        } else {
            target.setDataType(Objects.nonNull(source.getDataType()) ? DataType.valueOf(source.getDataType().name()) : null);
        }

        target.setValue((Serializable) source.getDefaultValue());
        return target;
    }

	public static Map<AttributeType, List<CachedClassifierNodeAttribute>> convert(List<ClsfNodeAttrDTO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyMap();
        }

        Map<AttributeType, List<CachedClassifierNodeAttribute>> target = new EnumMap<>(AttributeType.class);
        for (ClsfNodeAttrDTO element : source) {

            if (Objects.isNull(element)) {
                continue;
            }

            if (element instanceof ClsfNodeSimpleAttrDTO) {
                target.computeIfAbsent(AttributeType.SIMPLE, key -> new ArrayList<>(4))
                    .add(convert((ClsfNodeSimpleAttrDTO) element));
            } else if (element instanceof ClsfNodeArrayAttrDTO) {
                target.computeIfAbsent(AttributeType.ARRAY, key -> new ArrayList<>(4))
                    .add(convert((ClsfNodeArrayAttrDTO) element));
            }
        }

        return target;
    }
}
