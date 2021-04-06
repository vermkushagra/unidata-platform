package com.unidata.mdm.backend.api.rest.dto.cleanse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class Link.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CFLink {

    /** The from node id. */
    private String fromNodeId;

    /** The from port. */
    private String fromPort;

    /** The to node id. */
    private String toNodeId;

    /** The to port. */
    private String toPort;

    /**
     * Gets the from node id.
     *
     * @return the from node id
     */
    public String getFromNodeId() {
        return fromNodeId;
    }

    /**
     * Sets the from node id.
     *
     * @param fromNodeId
     *            the new from node id
     */
    public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    /**
     * Gets the from port.
     *
     * @return the from port
     */
    public String getFromPort() {
        return fromPort;
    }

    /**
     * Sets the from port.
     *
     * @param fromPort
     *            the new from port
     */
    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    /**
     * Gets the to node id.
     *
     * @return the to node id
     */
    public String getToNodeId() {
        return toNodeId;
    }

    /**
     * Sets the to node id.
     *
     * @param toNodeId
     *            the new to node id
     */
    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
    }

    /**
     * Gets the to port.
     *
     * @return the to port
     */
    public String getToPort() {
        return toPort;
    }

    /**
     * Sets the to port.
     *
     * @param toPort
     *            the new to port
     */
    public void setToPort(String toPort) {
        this.toPort = toPort;
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
        result = prime * result + ((fromNodeId == null) ? 0 : fromNodeId.hashCode());
        result = prime * result + ((fromPort == null) ? 0 : fromPort.hashCode());
        result = prime * result + ((toNodeId == null) ? 0 : toNodeId.hashCode());
        result = prime * result + ((toPort == null) ? 0 : toPort.hashCode());
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
        CFLink other = (CFLink) obj;
        if (fromNodeId == null) {
            if (other.fromNodeId != null)
                return false;
        } else if (!fromNodeId.equals(other.fromNodeId))
            return false;
        if (fromPort == null) {
            if (other.fromPort != null)
                return false;
        } else if (!fromPort.equals(other.fromPort))
            return false;
        if (toNodeId == null) {
            if (other.toNodeId != null)
                return false;
        } else if (!toNodeId.equals(other.toNodeId))
            return false;
        if (toPort == null) {
            if (other.toPort != null)
                return false;
        } else if (!toPort.equals(other.toPort))
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
        builder.append("Link [fromNodeId=");
        builder.append(fromNodeId);
        builder.append(", fromPort=");
        builder.append(fromPort);
        builder.append(", toNodeId=");
        builder.append(toNodeId);
        builder.append(", toPort=");
        builder.append(toPort);
        builder.append("]");
        return builder.toString();
    }
}
