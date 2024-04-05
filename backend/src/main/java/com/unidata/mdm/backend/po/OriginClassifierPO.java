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

package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Classifier origin record.
 */
public class OriginClassifierPO extends AbstractPO {

    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins_classifiers";
    /**
     * Id.
     */
    public static final String FIELD_ID = "id";
    /**
     * Etalon ID.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Classifier name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Classifier node id.
     */
    public static final String FIELD_NODE_ID = "node_id";
    /**
     * Origin id record.
     */
    public static final String FIELD_ORIGIN_ID_RECORD = "origin_id_record";
    /**
     * Source system.
     */
    public static final String FIELD_SOURCE_SYSTEM = "source_system";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Version.
     */
    public static final String FIELD_VERSION = "version";
    /**
     * Record id.
     */
    private String id;
    /**
     * Etalon ID.
     */
    private String etalonId;
    /**
     * Classiifer name.
     */
    private String name;
    /**
     * Classifier node ID.
     */
    private String nodeId;
    /**
     * Origin id record.
     */
    private String originIdRecord;
    /**
     * Source system.
     */
    private String sourceSystem;
    /**
     * Status.
     */
    private RecordStatus status;
    /**
     * Constructor.
     */
    public OriginClassifierPO() {
        super();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the originIdRecord
     */
    public String getOriginIdRecord() {
        return originIdRecord;
    }

    /**
     * @param originIdRecord the originIdRecord to set
     */
    public void setOriginIdRecord(String originIdRecord) {
        this.originIdRecord = originIdRecord;
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
    public RecordStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
    }

}