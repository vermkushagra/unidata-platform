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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNode;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeArrayAttribute;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifierNodeSimpleAttribute;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeArrayAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeSimpleAttrPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * The Class ClsfNodeDTOToPOConverter.
 */
public class CachedClassifierNodeToClsfNodePOConverter {

	/**
	 * Instantiates a new clsf node DTO to PO converter.
	 */
	private CachedClassifierNodeToClsfNodePOConverter() {
		super();
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the clsf node PO
	 */
	public static ClsfNodePO convert(CachedClassifierNode source) {

	    if (source == null) {
			return null;
		}

		ClsfNodePO target = new ClsfNodePO();

		target.setId(source.getId());
		target.setCode(source.getCode());
		target.setCreatedAt(source.getId() == 0 ? new Date() : null);
		target.setCreatedBy(source.getId() == 0 ? SecurityUtils.getCurrentUserName() : null);
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setNodeId(source.getNodeId());
		target.setParentId(source.getParentNodeId());
		target.setUpdatedAt(source.getId() > 0 ? new Date() : null);
		target.setUpdatedBy(source.getId() > 0 ? SecurityUtils.getCurrentUserName() : null);
		target.setCustomProperties(ClsfCustomPropertyPOToCachedClassifierPropertyConverter.convert(source.getCustomProperties()));

		if (CollectionUtils.isNotEmpty(source.getAttributes().get(AttributeType.ARRAY))) {

		    List<ClsfNodeArrayAttrPO> pos = source.getAttributes().get(AttributeType.ARRAY).stream()
		        .map(a -> (CachedClassifierNodeArrayAttribute) a)
		        .map(CachedClassifierNodeAttributeToClsfNodeAttrPOConverter::convert)
		        .collect(Collectors.toList());
		    target.getNodeArrayAttrs().addAll(pos);
		}

		if (CollectionUtils.isNotEmpty(source.getAttributes().get(AttributeType.SIMPLE))) {

		    List<ClsfNodeSimpleAttrPO> pos = source.getAttributes().get(AttributeType.SIMPLE).stream()
	            .map(a -> (CachedClassifierNodeSimpleAttribute) a)
                .map(CachedClassifierNodeAttributeToClsfNodeAttrPOConverter::convert)
                .collect(Collectors.toList());
            target.getNodeSimpleAttrs().addAll(pos);
        }

		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<ClsfNodePO> convert(List<CachedClassifierNode> source) {

	    if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}

		List<ClsfNodePO> target = new ArrayList<>();
		for (CachedClassifierNode element : source) {
			target.add(convert(element));
		}

		return target;
	}
}
