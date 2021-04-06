package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import java.util.List;

import org.jgrapht.EdgeFactory;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaEdgeRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaGraphRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaVertexRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdgeFactory;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;

/**
 * The Class MetaGraphROToDTOConverter.
 * 
 * @author ilya.bykov
 */
public class MetaGraphROToDTOConverter {

	/** The ef. */
	private static EdgeFactory<MetaVertex, MetaEdge<MetaVertex>> EF = new MetaEdgeFactory();

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the meta graph
	 */
	public static MetaGraph convert(MetaGraphRO source) {
		if (source == null) {
			return null;
		}
		MetaGraph target = new MetaGraph(EF);
		List<MetaVertexRO> vertexes = source.getVertexes();
		for (MetaVertexRO vertex : vertexes) {
			target.addVertex(MetaVertexROToDTOConverter.convert(vertex));
		}
		List<MetaEdgeRO> edges = source.getEdges();
		for (MetaEdgeRO edge : edges) {
			target.addEdge(MetaVertexROToDTOConverter.convert(edge.getFrom()),
					MetaVertexROToDTOConverter.convert(edge.getTo()));
		}
		target.setOverride(source.isOverride());
		target.setId(source.getId());
		target.setFileName(source.getFileName());
		return target;
	}

}
