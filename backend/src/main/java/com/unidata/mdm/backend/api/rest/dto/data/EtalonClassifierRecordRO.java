package com.unidata.mdm.backend.api.rest.dto.data;

public class EtalonClassifierRecordRO extends NestedRecordRO {

    /**
     * Classifier etalon id.
     */
    private String etalonId;
    /**
     * Classifier Name
     */
    private String classifierName;
    /**
     * Classifier Node id
     */
    private String classifierNodeId;
    /**
     * Status {PENDING|APPROVED|DECLINED}.
     */
    protected String approval;
    /**
     * Status {ACTIVE|INACTIVE|PENDING|MERGED}.
     */
    private String status;

    public String getClassifierName() {
        return classifierName;
    }

    public void setClassifierName(String classifierName) {
        this.classifierName = classifierName;
    }

    public String getClassifierNodeId() {
        return classifierNodeId;
    }

    public void setClassifierNodeId(String classifierNodeId) {
        this.classifierNodeId = classifierNodeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the approval
     */
    public String getApproval() {
        return approval;
    }

    /**
     * @param approval the approval to set
     */
    public void setApproval(String approval) {
        this.approval = approval;
    }
}
