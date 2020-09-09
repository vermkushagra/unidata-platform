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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaEdgeRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;

/**
 * The Class MetaEdgeDTOToROConverter.
 * 
 * @author ilya.bykov
 */
public class MetaEdgeDTOToROConverter {

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static List<MetaEdgeRO> convert(Collection<MetaEdge<MetaVertex>> source) {
		if (source == null) {
			return null;
		}

		List<MetaEdgeRO> target = new ArrayList<>();
		for (MetaEdge<MetaVertex> s : source) {
			target.add(convert(s));
		}
		return target;

	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta edge RO
	 */
	public static MetaEdgeRO convert(MetaEdge<MetaVertex> source) {
		if (source == null) {
			return null;
		}
		MetaEdgeRO target = new MetaEdgeRO(MetaVertexDTOToROConverter.convert(source.getFrom()),
				MetaVertexDTOToROConverter.convert(source.getTo()));
		return target;
	}
}
