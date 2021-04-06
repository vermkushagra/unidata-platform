package com.unidata.mdm.backend.service.cleanse.composite;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;

/**
 * The Class CompositeFunctionMDAGRep.
 */
public class CompositeFunctionMDAGRep extends DirectedMultigraph<Node, NodeLink> {

    /**
     * Instantiates a new composite function mdag rep.
     *
     * @param ef
     *            the ef
     */
    public CompositeFunctionMDAGRep(EdgeFactory<Node, NodeLink> ef) {
        super(ef);
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -418455145560220340L;

}
