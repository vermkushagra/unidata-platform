package org.unidata.mdm.meta.type.ie;

import java.io.Serializable;

import org.jgrapht.EdgeFactory;

/**
 * A factory for creating MetaEdge objects.
 */
public class MetaEdgeFactory implements EdgeFactory<MetaVertex, MetaEdge<MetaVertex>>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.jgrapht.EdgeFactory#createEdge(java.lang.Object, java.lang.Object)
	 */
	@Override
	public MetaEdge<MetaVertex> createEdge(MetaVertex sourceVertex, MetaVertex targetVertex) {
		MetaEdge<MetaVertex> edge = new MetaEdge<MetaVertex>(sourceVertex, targetVertex);
		return edge;
	}

}
