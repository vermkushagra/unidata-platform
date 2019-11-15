/**
 *
 */
package org.unidata.mdm.data.po.data;

import org.unidata.mdm.core.po.AbstractDistributedUpdateablePO;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.type.data.RelationType;

/**
 * @author Mikhail Mikhailov
 * Relation etalon record.
 */
public class RelationEtalonPO extends AbstractDistributedUpdateablePO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "relation_etalons";
    /**
     * Local sequence number.
     */
    public static final String FIELD_LSN = "lsn";
    /**
     * Id.
     */
    public static final String FIELD_ID = "id";
    /**
     * Relation name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Etalon id from.
     */
    public static final String FIELD_ETALON_ID_FROM = "etalon_id_from";
    /**
     * Etalon ID to.
     */
    public static final String FIELD_ETALON_ID_TO = "etalon_id_to";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Approval.
     */
    public static final String FIELD_APPROVAL = "approval";
    /**
     * Operation ID.
     */
    public static final String FIELD_OPERATION_ID = "operation_id";
    /**
     * Relation type hint.
     */
    public static final String FIELD_RELTYPE = "reltype";
    /**
     * Record id.
     */
    private String id;
    /**
     * Relation name.
     */
    private String name;
    /**
     * Origin id from.
     */
    private String fromEtalonId;
    /**
     * Origin ID to.
     */
    private String toEtalonId;
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
    private long lsn;
    /**
     * The operation id.
     */
    private String operationId;
    /**
     * Relation type.
     */
    private RelationType relationType;
    /**
     * Constructor.
     */
    public RelationEtalonPO() {
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
    public String getFromEtalonId() {
        return fromEtalonId;
    }
    /**
     * @param originIdFrom the originIdFrom to set
     */
    public void setFromEtalonId(String originIdFrom) {
        this.fromEtalonId = originIdFrom;
    }
    /**
     * @return the originIdTo
     */
    public String getToEtalonId() {
        return toEtalonId;
    }
    /**
     * @param originIdTo the originIdTo to set
     */
    public void setToEtalonId(String originIdTo) {
        this.toEtalonId = originIdTo;
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
     * @param lsn the lsn to set
     */
    public void setLsn(long gsn) {
        this.lsn = gsn;
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
    /**
     * @return the relationType
     */
    public RelationType getRelationType() {
        return relationType;
    }
    /**
     * @param relationType the relationType to set
     */
    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }
}
