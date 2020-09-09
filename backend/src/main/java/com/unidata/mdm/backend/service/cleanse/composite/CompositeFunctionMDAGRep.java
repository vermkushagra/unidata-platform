/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
