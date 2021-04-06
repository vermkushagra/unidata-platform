package com.unidata.mdm.backend.service.model.ie.dto;

import org.jgrapht.EdgeFactory;

/**
 * A factory for creating MetaEdge objects.
 */
public class MetaEdgeFactory implements EdgeFactory<MetaVertex, MetaEdge<MetaVertex>> {

	/* (non-Javadoc)
	 * @see org.jgrapht.EdgeFactory#createEdge(java.lang.Object, java.lang.Object)
	 */
	@Override
	public MetaEdge<MetaVertex> createEdge(MetaVertex sourceVertex, MetaVertex targetVertex) {
		MetaEdge<MetaVertex> edge = new MetaEdge<MetaVertex>(sourceVertex, targetVertex);
		return edge;
	}

}
