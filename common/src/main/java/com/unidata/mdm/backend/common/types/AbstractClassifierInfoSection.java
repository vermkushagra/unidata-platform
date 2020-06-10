package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Common part for all classifier info sections.
 */
public abstract class AbstractClassifierInfoSection extends InfoSection {
    /**
     * The classifier name.
     */
    protected String classifierName;
    /**
     * Node id.
     */
    protected String nodeId;
    /**
     * Record entity name.
     */
    protected String recordEntityName;
    /**
     * Gets the entity name.
     * @return name
     */
    public String getClassifierName() {
        return classifierName;
    }
    /**
     * Sets entity name field.
     * @param classifierName value to set
     */
    public void setClassifierName(String classifierName) {
        this.classifierName = classifierName;
    }
    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
    }
    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    /**
     * @return the recordEntityName
     */
    public String getRecordEntityName() {
        return recordEntityName;
    }
    /**
     * @param recordEntityName the recordEntityName to set
     */
    public void setRecordEntityName(String recordEntityName) {
        this.recordEntityName = recordEntityName;
    }
}
