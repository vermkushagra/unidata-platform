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
