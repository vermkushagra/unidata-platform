package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaGraphRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;

/**
 * The Class MetaGraphDTOToROConverter.
 * @author ilya.bykov
 */
public class MetaGraphDTOToROConverter {
	
	/**
	 * Convert.
	 *
	 * @param source the source
	 * @return the meta graph RO
	 */
	public static MetaGraphRO convert(MetaGraph source) {
		if (source == null) {
			return null;
		}
		MetaGraphRO target = new MetaGraphRO(source.getId(), source.getFileName(),
				MetaVertexDTOToROConverter.convert(source.vertexSet()),
				MetaEdgeDTOToROConverter.convert(source.edgeSet()));
		target.setOverride(source.isOverride());
		return target;
	}
}
