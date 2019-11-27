package org.unidata.mdm.system.type.event.impl;

import org.unidata.mdm.system.type.event.AbstractForeignEvent;
import org.unidata.mdm.system.util.IdUtils;

/**
 * @author Mikhail Mikhailov on Nov 26, 2019
 */
public class PipelineUpdate extends AbstractForeignEvent {
    /**
     * Type of the operation.
     * @author Mikhail Mikhailov on Nov 27, 2019
     */
    public enum PipelineUpdateType {
        UPSERT,
        REMOVAL
    }
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = 9017170992401345410L;
    /**
     * This type name.
     */
    private static final String TYPE_NAME = "PIPELINE_UPDATE_EVENT";
    /**
     * The start id.
     */
    private String startId;
    /**
     * The subject id;
     */
    private String subjectId;
    /**
     * The update type.
     */
    private PipelineUpdateType updateType;
    /**
     * Constructor.
     */
    public PipelineUpdate() {
        super(TYPE_NAME, IdUtils.v1String());
    }
    /**
     * @return the startId
     */
    public String getStartId() {
        return startId;
    }
    /**
     * @param startId the startId to set
     */
    public void setStartId(String startId) {
        this.startId = startId;
    }
    /**
     * @return the subjectId
     */
    public String getSubjectId() {
        return subjectId;
    }
    /**
     * @param subjectId the subjectId to set
     */
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
    /**
     * @return the updateType
     */
    public PipelineUpdateType getUpdateType() {
        return updateType;
    }
    /**
     * @param updateType the updateType to set
     */
    public void setUpdateType(PipelineUpdateType updateType) {
        this.updateType = updateType;
    }
}
