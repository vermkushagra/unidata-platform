/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.Date;

/**
 * @author Mikhail Mikhailov
 * Base REST interface for all types of relations.
 */
public interface BaseRelationRO {

    /**
     * @return the relName
     */
    public String getRelName();

    /**
     * @param relName the relName to set
     */
    public void setRelName(String relName);

    /**
     * @return the status
     */
    public String getStatus();

    /**
     * @param status the status to set
     */
    public void setStatus(String status);
    /**
     * @return the createDate
     */
    public Date getCreateDate();

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate);

    /**
     * @return the createdBy
     */
    public String getCreatedBy();

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy);

    /**
     * @return the updateDate
     */
    public Date getUpdateDate();

    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate);

    /**
     * @return the updatedBy
     */
    public String getUpdatedBy();

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(String updatedBy);
}
