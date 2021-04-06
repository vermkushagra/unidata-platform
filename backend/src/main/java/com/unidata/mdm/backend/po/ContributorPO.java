/**
 *
 */
package com.unidata.mdm.backend.po;

import java.util.Date;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ContributorPO {

    /**
     * Origin id.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
    /**
     * Revision number.
     */
    public static final String FIELD_REVISION = "revision";
    /**
     * Origin name.
     */
    public static final String FIELD_SOURCE_SYSTEM = "source_system";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Approval.
     */
    public static final String FIELD_APPROVAL = "approval";
    /**
     * Owner.
     */
    public static final String FIELD_OWNER = "owner";
    /**
     * Create date.
     */
    public static final String FIELD_LAST_UPDATE = "last_update";
    /**
     * Origin id field.
     */
    private String originId;
    /**
     * Revision number.
     */
    private int revision;
    /**
     * Status of the record.
     */
    private RecordStatus status;
    /**
     * Approval state.
     */
    private ApprovalState approval;
    /**
     * Name of the source system.
     */
    private String sourceSystem;
    /**
     * Owner string.
     */
    private String owner;
    /**
     * Last update (create date of the version record).
     */
    private Date lastUpdate;
    /**
     * Constructor.
     */
    public ContributorPO() {
        super();
    }


    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }


    /**
     * @param originId the originId to set
     */
    public void setOriginId(String originId) {
        this.originId = originId;
    }


    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }


    /**
     * @param revision the revision to set
     */
    public void setRevision(int revision) {
        this.revision = revision;
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
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }


    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }



    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }



    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }


	/**
	 * @return the createDate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}


	/**
	 * @param createDate the createDate to set
	 */
	public void setLastUpdate(Date createDate) {
		this.lastUpdate = createDate;
	}

}
