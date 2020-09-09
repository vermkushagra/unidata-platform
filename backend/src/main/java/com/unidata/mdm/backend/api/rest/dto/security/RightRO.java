/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.api.rest.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class contains rights
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RightRO extends BaseSecurityRO {
    /** The secured resource. */
    private SecuredResourceRO securedResource;

    /** The create. */
    private boolean create;

    /** The update. */
    private boolean update;

    /** The delete. */
    private boolean delete;

    /** The read. */
    private boolean read;
    /**
     * Gets the secured resource.
     *
     * @return the secured resource
     */
    public SecuredResourceRO getSecuredResource() {
        return securedResource;
    }

    /**
     * Sets the secured resource.
     *
     * @param securedResource the new secured resource
     */
    public void setSecuredResource(SecuredResourceRO securedResource) {
        this.securedResource = securedResource;
    }

    /**
     * Checks if is creates the.
     *
     * @return the create
     */
    public boolean isCreate() {
        return create;
    }

    /**
     * Sets the creates the.
     *
     * @param create the create to set
     */
    public void setCreate(boolean create) {
        this.create = create;
    }

    /**
     * Checks if is update.
     *
     * @return the update
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * Sets the update.
     *
     * @param update the update to set
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    /**
     * Checks if is delete.
     *
     * @return the delete
     */
    public boolean isDelete() {
        return delete;
    }

    /**
     * Sets the delete.
     *
     * @param delete the delete to set
     */
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    /**
     * Checks if is read.
     *
     * @return the read
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read.
     *
     * @param read the read to set
     */
    public void setRead(boolean read) {
        this.read = read;
    }



}
