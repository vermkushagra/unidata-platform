/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

/**
 * @author Mikhail Mikhailov
 * Rest large object description.
 */
public class LargeObjectRO {

    /**
     * ID.
     */
    private String id;
    /**
     * File name.
     */
    private String fileName;
    /**
     * Object's mime type.
     */
    private String mimeType;
    /**
     * Size in bytes.
     */
    private long size;
    /**
     * Constructor.
     */
    public LargeObjectRO() {
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

}
