package com.unidata.mdm.backend.service.security.po;

import com.unidata.mdm.backend.po.AbstractPO;


/**
 * @author Mikhail Mikhailov
 * User event objects.
 */
public class UserEventPO extends AbstractPO {

    /**
     * Table name.
     */
    public static final String TABLE_NAME = "user_event";
    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * User ID.
     */
    public static final String FIELD_USER_ID = "user_id";
    /**
     * Binary data id.
     */
    public static final String FIELD_BINARY_DATA_ID = "binary_data_id";
    /**
     * Character data id.
     */
    public static final String FIELD_CHARACTER_DATA_ID = "character_data_id";
    /**
     * Type.
     */
    public static final String FIELD_TYPE = "type";
    /**
     * Content.
     */
    public static final String FIELD_CONTENT = "content";
    /**
     * Status.
     */
    // public static final String FIELD_STATUS = "status";
    /**
     * This PO object's fields.
     */
    public static final String[] FIELDS = {
        FIELD_ID,
        FIELD_USER_ID,
        FIELD_BINARY_DATA_ID,
        FIELD_CHARACTER_DATA_ID,
        FIELD_TYPE,
        FIELD_CONTENT,
        // FIELD_STATUS,
        FIELD_CREATE_DATE,
        FIELD_UPDATE_DATE,
        FIELD_CREATED_BY,
        FIELD_UPDATED_BY
    };

    /**
     * Event id.
     */
    private String id;
    /**
     * User id.
     */
    private Integer userId;
    /**
     * Binary data ID. Read only.
     */
    private String binaryDataId;
    /**
     * Binary data ID. Read only.
     */
    private String characterDataId;
    /**
     * Type.
     */
    private String type;
    /**
     * Content.
     */
    private String content;
    /**
     * Status of the record.
     */
    // private RecordStatus status;
    /**
     * Constructor.
     */
    public UserEventPO() {
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
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }


    /**
     * @return the binaryDataId
     */
    public String getBinaryDataId() {
        return binaryDataId;
    }


    /**
     * @param binaryDataId the binaryDataId to set
     */
    public void setBinaryDataId(String binaryDataId) {
        this.binaryDataId = binaryDataId;
    }


    /**
     * @return the characterDataId
     */
    public String getCharacterDataId() {
        return characterDataId;
    }


    /**
     * @param characterDataId the characterDataId to set
     */
    public void setCharacterDataId(String characterDataId) {
        this.characterDataId = characterDataId;
    }

}
