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

import org.apache.commons.collections4.CollectionUtils;

import com.unidata.mdm.backend.common.dto.security.SecurityLabelAttributeDTO;
import com.unidata.mdm.backend.common.dto.security.SecurityLabelDTO;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;
import com.unidata.mdm.security.LabelAttributeDef;
import com.unidata.mdm.security.LabelDef;


/**
 * The Class CommonXmlDefinitionsToObjectsConverter.
 */
public final class CommonXmlDefinitionsToObjectsConverter {
	
	/**
	 * Instantiates a new common xml definitions to objects converter.
	 */
	private CommonXmlDefinitionsToObjectsConverter() {
	}

	/**
	 * Convert security labels.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<SecurityLabel> convertSecurityLabels(List<LabelDef> source) {
		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}
		List<SecurityLabel> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the security label
	 */
	private static SecurityLabel convert(LabelDef source) {
		if (source == null) {
			return null;
		}
		SecurityLabelDTO target = new SecurityLabelDTO();
		target.setName(source.getName());
		target.setAttributes(convert(source.getAttributes()));
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<SecurityLabelAttribute> convert(List<LabelAttributeDef> source) {
		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}
		List<SecurityLabelAttribute> target = new ArrayList<>();
		source.stream().forEach(s -> target.add(convert(s)));
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the security label attribute
	 */
	private static SecurityLabelAttribute convert(LabelAttributeDef source) {
		if (source == null) {
			return null;
		}
		SecurityLabelAttributeDTO target = new SecurityLabelAttributeDTO();
		target.setName(source.getName());
		target.setPath(source.getPath());
		target.setValue(source.getValue());

		return target;
	}

}
