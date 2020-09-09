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
import java.util.Map;
import java.util.Map.Entry;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaCustomPropRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaMessageRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaVertexRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaMessage;
import com.unidata.mdm.backend.service.model.ie.dto.MetaPropKey;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;


/**
 * The Class MetaVertexDTOToROConverter.
 * 
 * @author ilya.bykov
 */
public class MetaVertexDTOToROConverter {
	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static List<MetaVertexRO> convert(Collection<MetaVertex> source) {
		if (source == null) {
			return null;
		}
		List<MetaVertexRO> target = new ArrayList<>();
		for (MetaVertex s : source) {
			target.add(convert(s));
		}
		return target;

	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta vertex RO
	 */
	public static MetaVertexRO convert(MetaVertex source) {
		if (source == null) {
			return null;
		}
		MetaVertexRO target = new MetaVertexRO(source.getId(), source.getDisplayName(),
				MetaActionDTOToROConverter.convert(source.getAction()),
				MetaTypeDTOToROConverter.convert(source.getType()),
				MetaExistenceDTOToROConverter.convert(source.getStatus()));
		target.setStatuses(convertMessages(source.getMessages()));
		target.setCustomProps(convertCustomProps(source.getCustomProps()));
		return target;
	}

	/**
	 * Convert custom props.
	 *
	 * @param source the source
	 * @return the list
	 */
	private static List<MetaCustomPropRO> convertCustomProps(Map<MetaPropKey, String> source) {
		if (source == null) {
			return null;
		}
		List<MetaCustomPropRO> target = new ArrayList<>();
		for (Entry<MetaPropKey, String> entry : source.entrySet()) {
			MetaCustomPropRO elem = new MetaCustomPropRO();
			elem.setKey(entry.getKey().name());
			elem.setValue(entry.getValue());
			target.add(elem);
		}
		return target;
	}

	/**
	 * Convert messages.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	private static List<MetaMessageRO> convertMessages(List<MetaMessage> source) {
		if (source == null) {
			return null;
		}
		List<MetaMessageRO> target = new ArrayList<>();
		for (MetaMessage metaMessage : source) {
			target.add(convertMessages(metaMessage));
		}
		return target;
	}

	/**
	 * Convert messages.
	 *
	 * @param source
	 *            the source
	 * @return the meta message RO
	 */
	private static MetaMessageRO convertMessages(MetaMessage source) {
		if (source == null) {
			return null;
		}
		MetaMessageRO target = new MetaMessageRO();
		target.setStatus(MetaStatusDTOToROConverter.convert(source.getStatus()));
		target.setMessages(source.getMessages());
		return target;
	}
}
