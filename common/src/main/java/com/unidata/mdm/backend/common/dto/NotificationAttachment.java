package com.unidata.mdm.backend.common.dto;

/**
 * @author Michael Yashin. Created on 25.03.2015.
 */
public class NotificationAttachment {
    private String filename;
    private byte[] data;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
