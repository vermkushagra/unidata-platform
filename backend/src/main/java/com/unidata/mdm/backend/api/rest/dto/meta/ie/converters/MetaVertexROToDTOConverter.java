package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaVertexRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;

/**
 * The Class MetaVertexROToDTOConverter.
 * 
 * @author ilya.bykov
 */
public class MetaVertexROToDTOConverter {

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static List<MetaVertex> convert(List<MetaVertexRO> source) {
		if (source == null) {
			return null;
		}
		List<MetaVertex> target = new ArrayList<>();
		for (MetaVertexRO s : source) {
			target.add(convert(s));
		}
		return target;

	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta vertex
	 */
	public static MetaVertex convert(MetaVertexRO source) {
		if (source == null) {
			return null;
		}
		MetaVertex target = new MetaVertex(source.getId(), source.getDisplayName(),
				MetaTypeROToDTOConverter.convert(source.getType()),
				MetaActionROToDTOConverter.convert(source.getAction()),
				MetaExistenceROToDTOConverter.convert(source.getExistence()));
		return target;

	}
}
