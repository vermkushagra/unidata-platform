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

package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaTypeRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;

/**
 * The Class MetaTypeROToDTOConverter.
 * 
 * @author ilya.bykov
 */
public class MetaTypeROToDTOConverter {

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static Set<MetaType> convert(List<MetaTypeRO> source) {
		if (source == null) {
			return null;
		}
		Set<MetaType> target = new HashSet<>();
		for (MetaTypeRO metaTypeRo : source) {
			target.add(convert(metaTypeRo));
		}
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta type
	 */
	public static MetaType convert(MetaTypeRO source) {
		if (source == null) {
			return null;
		}
		MetaType target = MetaType.valueOf(source.name());
		return target;
	}
}
