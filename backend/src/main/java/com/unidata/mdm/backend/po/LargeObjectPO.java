/**
 *
 */
package com.unidata.mdm.backend.po;

import java.io.InputStream;

import com.unidata.mdm.backend.common.types.ApprovalState;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class LargeObjectPO extends AbstractPO {

    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Etalon ID.
     */
    public static final String FIELD_ETALON_ID = "etalon_id";
    /**
     * Origin ID.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
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
        FIELD_ETALON_ID,
        FIELD_ORIGIN_ID,
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
    private String etalonId;
    /**
     * Marshaled JAXB object.
     */
    private String originId;
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
