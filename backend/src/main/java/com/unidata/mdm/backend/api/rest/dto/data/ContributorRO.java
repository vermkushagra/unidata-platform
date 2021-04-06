/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

/**
 * @author Mikhail Mikhailov
 * Contributor REST object.
 */
public class ContributorRO {
    /**
     * Origin ID.
     */
    private String originId;
    /**
     * Version.
     */
    private int version;
    /**
     * Source system.
     */
    private String sourceSystem;
    /**
     * Status.
     */
    private String status;
    /**
     * Approval state.
     */
    private String approval;
    /**
     * Owner string.
     */
    private String owner;
    /**
     * Date from.
     */
    // private Date dateFrom;
    /**
     * Date to.
     */
    // private Date dateTo;

    /**
     * Constructor.
     */
    public ContributorRO() {
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
     * @return the version
     */
    public int getVersion() {
        return version;
    }


    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
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
     * @return the status
     */
    public String getStatus() {
        return status;
    }


    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
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

}
