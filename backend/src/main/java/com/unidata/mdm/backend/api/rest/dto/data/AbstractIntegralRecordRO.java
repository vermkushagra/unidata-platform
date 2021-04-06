/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * @author Mikhail Mikhailov
 * Base REST class for relation integral records.
 */
public class AbstractIntegralRecordRO implements BaseRelationRO {

    /**
     * Relation name.
     */
    private String relName;
    /**
     * Status string.
     */
    private String status;
    /**
     * Create date of the origin (source definition) record.
     */
    @JsonFormat(timezone = "DEFAULT_TIMEZONE")
    private Date createDate;
    /**
     * Created by.
     */
    private String createdBy;
    /**
     * Update date of the origin version record.
     */
    @JsonFormat(timezone = "DEFAULT_TIMEZONE")
    private Date updateDate;
    /**
     * Updated by.
     */
    private String updatedBy;

    /**
     * Constructor.
     */
    public AbstractIntegralRecordRO() {
        super();
    }

    /**
     * @return the relName
     */
    @Override
    public String getRelName() {
        return relName;
    }

    /**
     * @param relName the relName to set
     */
    @Override
    public void setRelName(String relName) {
        this.relName = relName;
    }

    /**
     * @return the status
     */
    @Override
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the createDate
     */
    @Override
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    @Override
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the createdBy
     */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the updateDate
     */
    @Override
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate the updateDate to set
     */
    @Override
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return the updatedBy
     */
    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
