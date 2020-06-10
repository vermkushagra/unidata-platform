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

package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Common part for all classifier info sections.
 */
public abstract class AbstractClassifierInfoSection extends InfoSection {
    /**
     * The classifier name.
     */
    protected String classifierName;
    /**
     * Node id.
     */
    protected String nodeId;
    /**
     * Record entity name.
     */
    protected String recordEntityName;
    /**
     * Gets the entity name.
     * @return name
     */
    public String getClassifierName() {
        return classifierName;
    }
    /**
     * Sets entity name field.
     * @param classifierName value to set
     */
    public void setClassifierName(String classifierName) {
        this.classifierName = classifierName;
    }
    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
    }
    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    /**
     * @return the recordEntityName
     */
    public String getRecordEntityName() {
        return recordEntityName;
    }
    /**
     * @param recordEntityName the recordEntityName to set
     */
    public void setRecordEntityName(String recordEntityName) {
        this.recordEntityName = recordEntityName;
    }
}
