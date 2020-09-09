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

package com.unidata.mdm.backend.api.rest.dto.cleanse;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class FunctionLogic.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositeCFLogic {

    /** The nodes. */
    private List<CFNode> nodes;

    /** The links. */
    private List<CFLink> links;

    /**
     * Gets the nodes.
     *
     * @return the nodes
     */
    public List<CFNode> getNodes() {
        return nodes;
    }

    /**
     * Sets the nodes.
     *
     * @param nodes
     *            the new nodes
     */
    public void setNodes(List<CFNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * Adds new node to node list.
     * 
     * @param node
     *            new node.
     */
    public void addNode(CFNode node) {
        if (this.nodes == null) {
            this.nodes = new ArrayList<CFNode>();
        }
        this.nodes.add(node);
    }

    /**
     * Gets the links.
     *
     * @return the links
     */
    public List<CFLink> getLinks() {
        return links;
    }

    /**
     * Sets the links.
     *
     * @param links
     *            the new links
     */
    public void setLinks(List<CFLink> links) {
        this.links = links;
    }

    /**
     * Add new node link.
     * 
     * @param link
     *            node link.
     */
    public void addLink(CFLink link) {
        if (this.links == null) {
            this.links = new ArrayList<CFLink>();
        }
        this.links.add(link);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((links == null) ? 0 : links.hashCode());
        result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CompositeCFLogic other = (CompositeCFLogic) obj;
        if (links == null) {
            if (other.links != null)
                return false;
        } else if (!links.equals(other.links))
            return false;
        if (nodes == null) {
            if (other.nodes != null)
                return false;
        } else if (!nodes.equals(other.nodes))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FunctionLogic [nodes=");
        builder.append(nodes);
        builder.append(", links=");
        builder.append(links);
        builder.append("]");
        return builder.toString();
    }
}
