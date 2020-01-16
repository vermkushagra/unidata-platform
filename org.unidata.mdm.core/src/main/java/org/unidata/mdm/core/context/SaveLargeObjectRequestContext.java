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
package org.unidata.mdm.core.context;

import java.io.InputStream;

import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 *
 */
public class SaveLargeObjectRequestContext extends CommonRequestContext {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -6915756538080064206L;
    /**
     * ID of the BLOB/CLOB record.
     */
    private final String recordKey;
    /**
     * Golden key.
     */
    private final String goldenKey;
    /**
     * Origin key.
     */
    private final String originKey;
    /**
     * Event key.
     */
    private final String eventKey;
    /**
     * Attribute name.
     */
    private final String attribute;
    /**
     * Binary or character data.
     */
    private final boolean binary;
    /**
     * Input stream.
     */
    private final InputStream inputStream;
    /**
     * File name.
     */
    private final String filename;
    /**
     * MIME type.
     */
    private final String mimeType;
    /**
     * Constructor.
     */
    private SaveLargeObjectRequestContext(SaveLargeObjectRequestContextBuilder b) {
        super(b);
        this.recordKey = b.recordKey;
        this.goldenKey = b.goldenKey;
        this.originKey = b.originKey;
        this.eventKey = b.eventKey;
        this.attribute = b.attribute;
        this.binary = b.binary;
        this.inputStream = b.inputStream;
        this.filename = b.filename;
        this.mimeType = b.mimeType;
    }

    /**
     * @return the recordKey
     */
    public String getRecordKey() {
        return recordKey;
    }

    /**
     * @return the goldenKey
     */
    public String getGoldenKey() {
        return goldenKey;
    }

    /**
     * @return the originKey
     */
    public String getOriginKey() {
        return originKey;
    }


    /**
     * @return the eventKey
     */
    public String getEventKey() {
        return eventKey;
    }

    /**
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @return the binary
     */
    public boolean isBinary() {
        return binary;
    }

    /**
     * @return the inputStream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Is a origin key or not
     * @return true if so, false otherwise
     */
    public boolean isOrigin() {
        return goldenKey == null && originKey != null;
    }
    /**
     * Is a golden key or not
     * @return true if so, false otherwise
     */
    public boolean isGolden() {
        return goldenKey != null && originKey == null;
    }
    /**
     * Symmetrically to all other contexts.
     * @return builder instance
     */
    public static SaveLargeObjectRequestContextBuilder builder() {
        return new SaveLargeObjectRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class SaveLargeObjectRequestContextBuilder extends CommonRequestContextBuilder<SaveLargeObjectRequestContextBuilder> {
        /**
         * ID of the BLOB/CLOB record.
         */
        private String recordKey;
        /**
         * Golden key.
         */
        private String goldenKey;
        /**
         * Origin key.
         */
        private String originKey;
        /**
         * Event key.
         */
        private String eventKey;
        /**
         * Attribute name.
         */
        private String attribute;
        /**
         * Binary or character data.
         */
        private boolean binary;
        /**
         * Input stream.
         */
        private InputStream inputStream;
        /**
         * File name.
         */
        private String filename;
        /**
         * Mime type.
         */
        private String mimeType;

        /**
         * Sets record origin key.
         * @param recordKey the key
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder recordKey(String recordKey) {
            this.recordKey = recordKey;
            return this;
        }
        /**
         * Sets classifier origin key.
         * @param classifierKey the key
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder classifierKey(String classifierKey) {
            this.goldenKey = classifierKey;
            return this;
        }
        /**
         * Sets origin key.
         * @param originKey the key
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }
        /**
         * Sets event key.
         * @param eventKey the key
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder eventKey(String eventKey) {
            this.eventKey = eventKey;
            return this;
        }
        /**
         * Sets attribute name.
         * @param attribute the attribute name
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder attribute(String attribute) {
            this.attribute = attribute;
            return this;
        }
        /**
         * Sets flag to return binary (or character) data.
         * @param binary the flag
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder binary(boolean binary) {
            this.binary = binary;
            return this;
        }
        /**
         * Sets the input stream.
         * @param inputStream the input stream
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }
        /**
         * Sets the file name.
         * @param filename the file name
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }
        /**
         * Sets the MIME type.
         * @param mimeType the mime type
         * @return self
         */
        public SaveLargeObjectRequestContextBuilder mimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }
        /**
         * Builds the context.
         * @return new context
         */
        public SaveLargeObjectRequestContext build() {
            return new SaveLargeObjectRequestContext(this);
        }
    }

}
