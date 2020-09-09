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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeArrayAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeSimpleAttribute;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;


/**
 * The Class ClsfNodeAttrPOToDTOConverter.
 */
public class CachedClassifierNodeAttributeToClsfNodeAttrDTOConverter {

	/**
	 * Instantiates a new clsf node attr PO to DTO converter.
	 */
	private CachedClassifierNodeAttributeToClsfNodeAttrDTOConverter() {
		super();
	}
	/**
     * Does sets common part for all types of attributes.
     * @param target the target
     * @param source the source
     * @param nodeId the node id
     */
    private static void convert(ClsfNodeAttrDTO target, CachedClassifierNodeAttribute source) {
        target.setAttrName(source.getName());
        target.setCreatedAt(new Date());
        target.setCreatedBy(SecurityUtils.getCurrentUserName());
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setHidden(source.isHidden());
        target.setReadOnly(source.isReadOnly());
        target.setSearchable(source.isSearchable());
        target.setUnique(source.isUnique());
        target.setNullable(source.isNullable());
        target.setUpdatedAt(null);
        target.setUpdatedBy(null);
        target.setCustomProperties(ClsfCustomPropertyPOToCachedClassifierPropertyConverter.convertToDTO(source.getCustomProperties()));
    }
    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr PO
     */
    public static ClsfNodeArrayAttrDTO convert(CachedClassifierNodeArrayAttribute source) {

        if (source == null) {
            return null;
        }

        ClsfNodeArrayAttrDTO target = new ClsfNodeArrayAttrDTO();
        convert(target, source);

        if (source.isLookupLink()) {
            target.setLookupEntityType(source.getLookupName());
            target.setLookupEntityCodeAttributeType(CodeDataType.valueOf(source.getDataType().name()));
        } else {
            target.setDataType(DataType.valueOf(source.getDataType().name()));
        }

        target.setValues(source.getValues() == null
                ? Collections.emptyList()
                : Arrays.stream(source.getValues()).collect(Collectors.toList()));

        return target;
    }
    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf node attr PO
     */
    public static ClsfNodeSimpleAttrDTO convert(CachedClassifierNodeSimpleAttribute source) {

        if (source == null) {
            return null;
        }

        ClsfNodeSimpleAttrDTO target = new ClsfNodeSimpleAttrDTO();
        convert(target, source);

        if (source.isEnumLink()) {
            target.setEnumDataType(source.getEnumName());
            target.setDataType(DataType.STRING);
        } else if (source.isLookupLink()) {
            target.setLookupEntityType(source.getLookupName());
            target.setLookupEntityCodeAttributeType(CodeDataType.valueOf(source.getDataType().name()));
        } else {
            target.setDataType(source.getDataType());
        }

        target.setDefaultValue(source.getValue());
        return target;
    }
    public static ClsfNodeAttrDTO convert(CachedClassifierNodeAttribute element) {

        if (Objects.isNull(element)) {
            return null;
        }

        if (element.isArray()) {
            return convert((CachedClassifierNodeArrayAttribute) element);
        } else if (element.isSimple()) {
            return convert((CachedClassifierNodeSimpleAttribute) element);
        }

        return null;
    }
	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @param nodeId
	 * @return the list
	 */
	public static List<ClsfNodeAttrDTO> convert(List<CachedClassifierNodeAttribute> source) {

	    if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}

		List<ClsfNodeAttrDTO> target = new ArrayList<>();
		for (CachedClassifierNodeAttribute element : source) {
		    if (element.isArray()) {
		        target.add(convert((CachedClassifierNodeArrayAttribute) element));
		    } else if (element.isSimple()) {
		        target.add(convert((CachedClassifierNodeSimpleAttribute) element));
		    }
		}

		return target;
	}
}
