package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;
import java.util.List;


/**
 * The Class ClassifierMapping.
 */
public class ClassifierMapping implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8695207624428893944L;

    /** The node id. */
    private ExchangeField nodeId;

    /** The fields. */
    private List<ExchangeField> fields;
    /**
     * The version range.
     */
    private VersionRange versionRange;
    /**
     * Gets the node id.
     *
     * @return the node id
     */
    public ExchangeField getNodeId() {
        return nodeId;
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    public List<ExchangeField> getFields() {
        return fields;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId
     *            the new node id
     */
    public void setNodeId(ExchangeField nodeId) {
        this.nodeId = nodeId;
    }
    /**
     * @return the versionRange
     */
    public VersionRange getVersionRange() {
        return versionRange;
    }

    /**
     * @param versionRange the versionRange to set
     */
    public void setVersionRange(VersionRange versionRange) {
        this.versionRange = versionRange;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId
     *            the new node id
     */
    public ClassifierMapping withNodeId(ExchangeField nodeId) {
        this.nodeId = nodeId;
        return this;
    }
    /**
     * Sets the node id.
     *
     * @param nodeId
     *            the new node id
     */
    public ClassifierMapping withVersionRange(VersionRange range) {
        setVersionRange(range);
        return this;
    }
    /**
     * Sets the fields.
     *
     * @param fields
     *            the new fields
     */
    public void setFields(List<ExchangeField> fields) {
        this.fields = fields;
    }
    /**
     * Sets the fields.
     *
     * @param fields
     *            the new fields
     */
    public ClassifierMapping withFields(List<ExchangeField> fields) {
        this.fields = fields;
        return this;
    }
}
