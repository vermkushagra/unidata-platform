package com.unidata.mdm.backend.api.rest.dto.settings;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * @author Dmitry Kopin on 28.08.2017.
 * Custom storage record rest object
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomStorageRecordRO {
    /**
     * Setting owner
     */
    private String user;
    /**
     * Setting key
     */
    private String key;
    /**
     * Setting value
     */
    private String value;
    /**
     * Setting update date
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "Europe/Moscow")
    private Date updateDate;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
