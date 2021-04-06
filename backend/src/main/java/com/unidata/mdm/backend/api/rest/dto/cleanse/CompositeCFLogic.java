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
