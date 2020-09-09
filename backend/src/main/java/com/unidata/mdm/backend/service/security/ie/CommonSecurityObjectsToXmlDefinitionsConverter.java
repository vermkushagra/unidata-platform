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

package com.unidata.mdm.backend.service.security.ie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.unidata.mdm.backend.common.integration.auth.CustomProperty;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.security.LabelAttributeDef;
import com.unidata.mdm.security.LabelDef;
import com.unidata.mdm.security.PropertyValueDef;

/**
 * The Class CommonSecurityObjectsToXmlDefinitionsConverter.
 */
public final class CommonSecurityObjectsToXmlDefinitionsConverter {
	
	/**
	 * Instantiates a new common security objects to xml definitions converter.
	 */
	private CommonSecurityObjectsToXmlDefinitionsConverter() {
	}

	/**
	 * Convert properties.
	 *
	 * @param customProperties the custom properties
	 * @return the list
	 */
	public static List<PropertyValueDef> convertProperties(final List<CustomProperty> customProperties) {
		if (CollectionUtils.isEmpty(customProperties)) {
			return Collections.emptyList();
		}
		return customProperties.stream()
				.map(customProperty -> JaxbUtils.getSecurityFactory().createPropertyValueDef()
						.withPropertyName(customProperty.getName()).withValue(customProperty.getValue()))
				.collect(Collectors.toList());
	}

	/**
	 * Convert security labels.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<LabelDef> convertSecurityLabels(List<SecurityLabel> source) {
		if (source == null) {
			return null;
		}
		List<LabelDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the label def
	 */
	private static LabelDef convert(SecurityLabel source) {
		if (source == null) {
			return null;
		}
		LabelDef target = new LabelDef();
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getDisplayName());
		target.setName(source.getName());
		target.withAttributes(convert(source.getAttributes()));
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<LabelAttributeDef> convert(List<SecurityLabelAttribute> source) {
		if (source == null) {
			return null;
		}
		List<LabelAttributeDef> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the label attribute def
	 */
	private static LabelAttributeDef convert(SecurityLabelAttribute source) {
		if (source == null) {
			return null;
		}
		LabelAttributeDef target = new LabelAttributeDef();
		target.setName(source.getName());
		target.setPath(source.getPath());
		target.setValue(source.getValue());
		return target;
	}
}
