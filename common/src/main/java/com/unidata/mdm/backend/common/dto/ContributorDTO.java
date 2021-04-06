/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.Date;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Timeline contributor DTO.
 */
public class ContributorDTO {

    /**
     * Origin ID.
     */
    private final String originId;
    /**
     * Revision.
     */
    private final int revision;
    /**
     * Source system.
     */
    private final String sourceSystem;
    /**
     * Status.
     */
    private final String status;
    /**
     * Approval.
     */
    private final String approval;
    /**
     * Owner string.
     */
    private final String owner;
    /**
     * Last update date.
     */
    private final Date lastUpdate;
    /**
     * Type name of the contributor.
     */
    private final String typeName;

    /**
     * Constructor.
     * @param originId contributor's origin id
     * @param revision contributor's revision
     * @param sourceSystem contributor's source system
     * @param status contributor's status
     * @param approval contributor's approval state
     * @param owner records creator/owner
     * @param lastUpdate date of the last update
     * @param typeName the name of the type of this contributor
     */
    public ContributorDTO(String originId, int revision, String sourceSystem, String status, String approval, String owner, Date lastUpdate, String typeName) {
        super();
        this.originId = originId;
        this.revision = revision;
        this.sourceSystem = sourceSystem;
        this.status = status;
        this.approval = approval;
        this.owner = owner;
        this.lastUpdate = lastUpdate;
        this.typeName = typeName;
    }


    /**
     * @return the status
     */
    public RecordStatus getStatus() {
        return status == null ? null : RecordStatus.valueOf(status);
    }


    /**
     * @return the approval
     */
    public String getApproval() {
        return approval;
    }


    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }


    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }


    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }



    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }


	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}


    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

}
