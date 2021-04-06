package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Michael Yashin. Created on 20.05.2015.
 */
// TODO: protected Date createdAt;
// TODO: protected Date updatedAt;
// TODO: protected String createdBy;
// TODO: protected String updatedBy;
@JsonIgnoreProperties(ignoreUnknown = true)
public class CleanseFunctionTreeElement {
    protected String name;
    protected String description;
    protected Date createdAt;
    protected Date updatedAt;
    protected String createdBy;
    protected String updatedBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        if (createdAt == null) {
            return new Date();
        }
        return createdAt;
    }

    /**
     * @param createdAt
     *            the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the updatedAt
     */
    public Date getUpdatedAt() {
        if (updatedAt == null) {
            return new Date();
        }
        return updatedAt;
    }

    /**
     * @param updatedAt
     *            the updatedAt to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        if (createdBy == null) {
            return SecurityUtils.getCurrentUserName();
        }
        return createdBy;
    }

    /**
     * @param createdBy
     *            the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the updatedBy
     */
    public String getUpdatedBy() {
        if (updatedBy == null) {
            return SecurityUtils.getCurrentUserName();
        }
        return updatedBy;
    }

    /**
     * @param updatedBy
     *            the updatedBy to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
