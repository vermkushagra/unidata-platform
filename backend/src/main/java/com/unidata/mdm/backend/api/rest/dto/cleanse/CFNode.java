package com.unidata.mdm.backend.api.rest.dto.cleanse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class Node.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CFNode {

    /** The node id. */
    private String nodeId;

    /** The node type. */
    private CFNodeType nodeType;

    /** The function name. */
    private String functionName;

    /** The ui relative position. */
    private String uiRelativePosition;

    /**
     * Gets the node id.
     *
     * @return the node id
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId
     *            the new node id
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Gets the node type.
     *
     * @return the node type
     */
    public CFNodeType getNodeType() {
        return nodeType;
    }

    /**
     * Sets the node type.
     *
     * @param nodeType
     *            the new node type
     */
    public void setNodeType(CFNodeType nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Gets the function name.
     *
     * @return the function name
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Sets the function name.
     *
     * @param functionName
     *            the new function name
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Gets the ui relative position.
     *
     * @return the ui relative position
     */
    public String getUiRelativePosition() {
        return uiRelativePosition;
    }

    /**
     * Sets the ui relative position.
     *
     * @param uiRelativePosition
     *            the new ui relative position
     */
    public void setUiRelativePosition(String uiRelativePosition) {
        this.uiRelativePosition = uiRelativePosition;
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
        result = prime * result + ((functionName == null) ? 0 : functionName.hashCode());
        result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
        result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
        result = prime * result + ((uiRelativePosition == null) ? 0 : uiRelativePosition.hashCode());
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
        CFNode other = (CFNode) obj;
        if (functionName == null) {
            if (other.functionName != null)
                return false;
        } else if (!functionName.equals(other.functionName))
            return false;
        if (nodeId == null) {
            if (other.nodeId != null)
                return false;
        } else if (!nodeId.equals(other.nodeId))
            return false;
        if (nodeType != other.nodeType)
            return false;
        if (uiRelativePosition == null) {
            if (other.uiRelativePosition != null)
                return false;
        } else if (!uiRelativePosition.equals(other.uiRelativePosition))
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
        builder.append("Node [nodeId=");
        builder.append(nodeId);
        builder.append(", nodeType=");
        builder.append(nodeType);
        builder.append(", functionName=");
        builder.append(functionName);
        builder.append(", uiRelativePosition=");
        builder.append(uiRelativePosition);
        builder.append("]");
        return builder.toString();
    }
}
