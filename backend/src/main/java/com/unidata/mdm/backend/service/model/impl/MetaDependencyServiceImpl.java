package com.unidata.mdm.backend.service.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.model.MetaDependencyService;
import com.unidata.mdm.backend.service.model.ie.GraphCreator;
import com.unidata.mdm.backend.service.model.ie.dto.FullModelDTO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdgeFactory;
import com.unidata.mdm.backend.service.model.ie.dto.MetaExistence;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;
import com.unidata.mdm.meta.Model;


/**
 * The Class MetaDependencyServiceImpl.
 * @author ilya.bykov
 */
@Component
public class MetaDependencyServiceImpl implements MetaDependencyService {
	
	/** The graph creator. */
	@Autowired
	private GraphCreator graphCreator;
	
	/** The meta model service. */
	@Autowired
	private MetaModelService metaModelService;

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.model.MetaDependencyService#calclulateDependencies(java.lang.String, java.util.Set, java.util.Set)
	 */
	@Override
	public MetaGraph calculateDependencies(String storageId, Set<MetaType> forTypes, Set<MetaType> skipTypes) {
		Model model = metaModelService.exportModel(storageId);
		FullModelDTO fullModelDTO = new FullModelDTO().withModel(model);
		MetaGraph result = new MetaGraph(new MetaEdgeFactory());
		if (forTypes != null && forTypes.size() != 0) {
			graphCreator.enrich(fullModelDTO, result, MetaExistence.EXIST,
					forTypes.toArray(new MetaType[forTypes.size()]));
			skipVertexes(result, skipTypes);
		}
		return result;
	}

	
	/**
	 * Skip vertexes.
	 *
	 * @param graph the graph
	 * @param skipTypes the skip types
	 */
	private void skipVertexes(MetaGraph graph, Set<MetaType> skipTypes) {
		if (skipTypes == null || skipTypes.size() == 0) {
			return;
		}
		Set<MetaVertex> vertexes = graph.vertexSet();
		Set<MetaVertex> vertexesToDelete = new HashSet<>();
		for (MetaVertex vertex : vertexes) {
			if (skipTypes.contains(vertex.getType())) {
				vertexesToDelete.add(vertex);
				Set<MetaEdge<MetaVertex>> incomingEdges = graph.incomingEdgesOf(vertex);
				Set<MetaVertex> setFrom = new HashSet<>();
				if (incomingEdges != null && incomingEdges.size() != 0) {
					for (MetaEdge<MetaVertex> edge : incomingEdges) {
						MetaVertex from = edge.getFrom();
						setFrom.add(from);
					}
				}
				Set<MetaVertex> setTo = new HashSet<>();
				Set<MetaEdge<MetaVertex>> outgoingEdges = graph.outgoingEdgesOf(vertex);
				if (outgoingEdges != null && outgoingEdges.size() != 0) {
					for (MetaEdge<MetaVertex> edge : outgoingEdges) {
						MetaVertex to = edge.getTo();
						setTo.add(to);

					}
				}
				if (setFrom.size() != 0 && setTo.size() != 0) {
					for (MetaVertex from : setFrom) {
						for (MetaVertex to : setTo) {
							if(!graph.containsEdge(from, to)){
								graph.addEdge(from, to);
							}
						}
					}
				}
			}
		}
		if (vertexesToDelete.size() != 0) {
			graph.removeAllVertices(vertexesToDelete);
		}
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.service.configuration.AfterContextRefresh#afterContextRefresh()
	 */
	@Override
	public void afterContextRefresh() {
		// TODO Auto-generated method stub

	}
}
