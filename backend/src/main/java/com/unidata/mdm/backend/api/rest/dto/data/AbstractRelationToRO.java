/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Mikhail Mikhailov
 * Base class for RelationTo mapping.
 */
public abstract class AbstractRelationToRO
    extends NestedRecordRO
    implements BaseRelationRO {

    /**
     * Relation name.
     */
    private String relName;
    /**
     * Status string.
     */
    private String status;
    /**
     * Range boundary from.
     */
    private LocalDateTime validFrom;
    /**
     * Range boundary to.
     */
    private LocalDateTime validTo;
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
    public AbstractRelationToRO() {
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
     * @return the validFrom
     */
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public LocalDateTime getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
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
