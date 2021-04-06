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
