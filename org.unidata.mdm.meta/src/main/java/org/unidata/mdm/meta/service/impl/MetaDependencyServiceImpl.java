package org.unidata.mdm.meta.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.dto.FullModelDTO;
import org.unidata.mdm.meta.service.MetaDependencyService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.ie.MetaEdge;
import org.unidata.mdm.meta.type.ie.MetaEdgeFactory;
import org.unidata.mdm.meta.type.ie.MetaGraph;
import org.unidata.mdm.meta.type.ie.MetaType;
import org.unidata.mdm.meta.type.ie.MetaVertex;


/**
 * The Class MetaDependencyServiceImpl.
 * @author ilya.bykov
 */
@Component
public class MetaDependencyServiceImpl implements MetaDependencyService {

	// TODO: @Modules
//	/** The graph creator. */
//	@Autowired
//	private GraphCreator graphCreator;

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
//				.withCleanseFunctions(metaModelService.getCleanseFunctionRootGroup());// TODO: @Modules
		MetaGraph result = new MetaGraph(new MetaEdgeFactory());
		if (forTypes != null && forTypes.size() != 0) {
			// TODO: @Modules
//			graphCreator.enrich(fullModelDTO, result, MetaExistence.EXIST,
//					forTypes.toArray(new MetaType[forTypes.size()]));
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
