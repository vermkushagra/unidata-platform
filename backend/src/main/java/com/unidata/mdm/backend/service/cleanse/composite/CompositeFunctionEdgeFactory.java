package com.unidata.mdm.backend.service.cleanse.composite;

import org.jgrapht.EdgeFactory;

import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;


/**
 * A factory for creating CompositeFunctionEdge objects.
 */
public class CompositeFunctionEdgeFactory implements EdgeFactory<Node, NodeLink> {

    /* (non-Javadoc)
     * @see org.jgrapht.EdgeFactory#createEdge(java.lang.Object, java.lang.Object)
     */
    @Override
    public NodeLink createEdge(Node sourceVertex, Node targetVertex) {
        NodeLink edge = new NodeLink();
        edge.setFromNodeId(sourceVertex.getNodeId());
        edge.setToNodeId(targetVertex.getNodeId());
        return edge;
    }

}
