package org.unidata.mdm.data.po.data;

import org.unidata.mdm.core.po.AbstractDistributedUpdateablePO;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;


/**
 * @author Mikhail Mikhailov
 * Etalon record persistent object.
 */
public class RecordEtalonPO extends AbstractDistributedUpdateablePO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "record_etalons";
    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Approval.
     */
    public static final String FIELD_APPROVAL = "approval";
    /**
     * Forward sequence number.
     */
    public static final String FIELD_LSN = "lsn";
    /**
     * Operation ID.
     */
    public static final String FIELD_OPERATION_ID = "operation_id";
    /**
     * Record id.
     */
    private String id;
    /**
     * Type name as set by entity definition.
     */
    private String name;
    /**
     * Status of the record.
     */
    private RecordStatus status;
    /**
     * Approval state of the record.
     */
    private ApprovalState approval;
    /**
     * Global sequence number.
     */
    private long lsn;
    /**
     * The operation id.
     */
    private String operationId;
    /**
     * Constructor.
     */
    public RecordEtalonPO() {
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
     * @return the lsn
     */
    public long getLsn() {
        return lsn;
    }

    /**
     * @param lsn the gsn to set
     */
    public void setLsn(long lsn) {
        this.lsn = lsn;
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
