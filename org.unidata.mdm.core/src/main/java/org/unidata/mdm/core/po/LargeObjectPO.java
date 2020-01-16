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
package org.unidata.mdm.core.po;

import java.io.InputStream;

import org.unidata.mdm.core.po.AbstractObjectPO;
import org.unidata.mdm.core.type.data.ApprovalState;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class LargeObjectPO extends AbstractObjectPO {
    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Classifier _ORIGIN_ ID.
     */
    public static final String FIELD_CLASSIFIER_ID = "classifier_id";
    /**
     * Record _ORIGIN_ ID.
     */
    public static final String FIELD_RECORD_ID = "record_id";
    /**
     * Event ID.
     */
    public static final String FIELD_EVENT_ID = "event_id";
    /**
     * Field name / path.
     */
    public static final String FIELD_FIELD = "field";
    /**
     * Data.
     */
    public static final String FIELD_DATA = "data";
    /**
     * File name.
     */
    public static final String FIELD_FILE_NAME = "filename";
    /**
     * Mime type.
     */
    public static final String FIELD_MIME_TYPE = "mime_type";
    /**
     * Size.
     */
    public static final String FIELD_SIZE = "size";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * This PO object's fields.
     */
    public static final String[] FIELDS = {
        FIELD_ID,
        FIELD_CLASSIFIER_ID,
        FIELD_RECORD_ID,
        FIELD_EVENT_ID,
        FIELD_FIELD,
        FIELD_DATA,
        FIELD_FILE_NAME,
        FIELD_MIME_TYPE,
        FIELD_SIZE,
        FIELD_STATUS
    };
    /**
     * Record id.
     */
    private String id;
    /**
     * Type name as set by entity definition.
     */
    private String classifierId;
    /**
     * Marshaled JAXB object.
     */
    private String recordId;
    /**
     * User event ID.
     */
    private String eventId;
    /**
     * Marshaled JAXB object.
     */
    private String field;
    /**
     * IN stream.
     */
    private InputStream data;
    /**
     * Marshaled JAXB object.
     */
    private String fileName;
    /**
     * Marshaled JAXB object.
     */
    private String mimeType;
    /**
     * Size of the object.
     */
    private long size;
    /**
     * Status.
     */
    private ApprovalState state;
    /**
     * Binary data mark.
     * @return true, if binary
     */
    public abstract boolean isBinary();
    /**
     * Character data mark.
     * @return true, if character
     */
    public abstract boolean isCharacter();
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
     * @return the classifierId
     */
    public String getClassifierId() {
        return classifierId;
    }
    /**
     * @param classifierId the classifierId to set
     */
    public void setClassifierId(String etalonId) {
        this.classifierId = etalonId;
    }
    /**
     * @return the recordId
     */
    public String getRecordId() {
        return recordId;
    }
    /**
     * @param recordId the recordId to set
     */
    public void setRecordId(String originId) {
        this.recordId = originId;
    }
    /**
     * @return the eventId
     */
    public String getEventId() {
        return eventId;
    }
    /**
     * @param eventId the eventId to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    /**
     * @return the field
     */
    public String getField() {
        return field;
    }
    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }
    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    /**
     * @return the data
     */
    public InputStream getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(InputStream inputStream) {
        this.data = inputStream;
    }
    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }
    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }
    /**
     * @return the state
     */
    public ApprovalState getState() {
        return state;
    }
    /**
     * @param state the state to set
     */
    public void setState(ApprovalState state) {
        this.state = state;
    }
}
