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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 * REST origin type for relation to.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OriginRelationToRO extends AbstractRelationToRO {

    /**
     * Origin id of the RELATION.
     */
    private String originId;
    /**
     * Origin id of the to record.
     */
    private String originIdTo;
    /**
     * External id of the to record.
     */
    private String externalIdTo;
    /**
     * Sxource system of the to record.
     */
    private String sourceSystemTo;
    /**
     * Entity name of the to record.
     */
    private String entityNameTo;
    /**
     * Revision of the to record.
     */
    private int revision;

    /**
     * Constructor.
     */
    public OriginRelationToRO() {
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
     * @return the originIdTo
     */
    public String getOriginIdTo() {
        return originIdTo;
    }

    /**
     * @param originIdTo the originIdTo to set
     */
    public void setOriginIdTo(String originIdTo) {
        this.originIdTo = originIdTo;
    }

    /**
     * @return the externalIdTo
     */
    public String getExternalIdTo() {
        return externalIdTo;
    }

    /**
     * @param externalIdTo the externalIdTo to set
     */
    public void setExternalIdTo(String externalIdTo) {
        this.externalIdTo = externalIdTo;
    }

    /**
     * @return the sourceSystemTo
     */
    public String getSourceSystemTo() {
        return sourceSystemTo;
    }

    /**
     * @param sourceSystemTo the sourceSystemTo to set
     */
    public void setSourceSystemTo(String sourceSystemTo) {
        this.sourceSystemTo = sourceSystemTo;
    }

    /**
     * @return the entityNameTo
     */
    public String getEntityNameTo() {
        return entityNameTo;
    }

    /**
     * @param entityNameTo the entityNameTo to set
     */
    public void setEntityNameTo(String entityNameTo) {
        this.entityNameTo = entityNameTo;
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
}
