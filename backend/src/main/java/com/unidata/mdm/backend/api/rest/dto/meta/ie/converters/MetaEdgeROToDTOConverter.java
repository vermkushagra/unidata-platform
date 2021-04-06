package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaEdgeRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;

/**
 * The Class MetaEdgeROToDTOConverter.
 * 
 * @author ilya.bykov
 */
public class MetaEdgeROToDTOConverter {

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	public static List<MetaEdge<MetaVertex>> convert(List<MetaEdgeRO> source) {
		if (source == null) {
			return null;
		}
		List<MetaEdge<MetaVertex>> target = new ArrayList<>();
		for (MetaEdgeRO s : source) {
			target.add(convert(s));
		}
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta edge
	 */
	public static MetaEdge<MetaVertex> convert(MetaEdgeRO source) {
		if (source == null) {
			return null;
		}
		MetaEdge<MetaVertex> target = new MetaEdge<MetaVertex>(
				new MetaVertex(source.getFrom().getId(), MetaTypeROToDTOConverter.convert(source.getFrom().getType())),
				new MetaVertex(source.getTo().getId(), MetaTypeROToDTOConverter.convert(source.getTo().getType())));
		return target;

	}
}
