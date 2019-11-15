/**
 *
 */
package org.unidata.mdm.core.dto;

import java.io.InputStream;

/**
 * @author Mikhail Mikhailov
 *
 */
public class LargeObjectDTO {

    /**
     * 'Delete on close' input stream.
     */
    private final InputStream inputStream;
    /**
     * Object id.
     */
    private final String id;
    /**
     * File name.
     */
    private final String fileName;
    /**
     * MIME type.
     */
    private final String mimeType;
    /**
     * Size.
     */
    private final long size;

    /**
     * Constructor.
     */
    public LargeObjectDTO(InputStream is, String id, String fileName, String mimeType, long size) {
        this.inputStream = is;
        this.id = id;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.size = size;
    }

    /**
     * @return the inputStream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

}
