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
