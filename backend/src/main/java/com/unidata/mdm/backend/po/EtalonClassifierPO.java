/**
 *
 */
package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Classifier etalon record.
 */
public class EtalonClassifierPO extends AbstractPO {

    /**
     * Table name.
     */
    public static final String TABLE_NAME = "etalons_classifiers";
    /**
     * Id.
     */
    public static final String FIELD_ID = "id";
    /**
     * Classifier name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Etalon id record.
     */
    public static final String FIELD_ETALON_ID_RECORD = "etalon_id_record";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Approval.
     */
    public static final String FIELD_APPROVAL = "approval";
    /**
     * Version.
     */
    public static final String FIELD_VERSION = "version";
    /**
     * Forward sequence number.
     */
    public static final String FIELD_GSN = "gsn";
    /**
     * Operation ID.
     */
    public static final String FIELD_OPERATION_ID = "operation_id";
    /**
     * Record id.
     */
    private String id;
    /**
     * Classifier name.
     */
    private String name;
    /**
     * Etalon id record.
     */
    private String etalonIdRecord;
    /**
     * Status.
     */
    private RecordStatus status;
    /**
     * Approval state of the record.
     */
    private ApprovalState approval;
    /**
     * Global sequence number.
     */
    private long gsn;
    /**
     * The operation id.
     */
    private String operationId;
    /**
     * Constructor.
     */
    public EtalonClassifierPO() {
        super();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the originIdFrom
     */
    public String getEtalonIdRecord() {
        return etalonIdRecord;
    }

    /**
     * @param originIdFrom the originIdFrom to set
     */
    public void setEtalonIdRecord(String originIdFrom) {
        this.etalonIdRecord = originIdFrom;
    }

    /**
     * @return the status
     */
    public RecordStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    /**
     * @return the approval
     */
    public ApprovalState getApproval() {
        return approval;
    }

    /**
     * @param approval the approval to set
     */
    public void setApproval(ApprovalState approval) {
        this.approval = approval;
    }

    /**
     * @return the gsn
     */
    public long getGsn() {
        return gsn;
    }

    /**
     * @param gsn the gsn to set
     */
    public void setGsn(long gsn) {
        this.gsn = gsn;
    }

    /**
     * @return the operationId
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * @param operationId the operationId to set
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}
