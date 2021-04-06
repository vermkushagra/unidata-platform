package com.unidata.mdm.backend.common.context;

/**
 * @author Mikhail Mikhailov
 * Fetch large objects request context.
 */
public class FetchLargeObjectRequestContext extends CommonRequestContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -4782136881603082178L;
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
     * Attribute name.
     */
    private final String attribute;
    /**
     * Binary or character data.
     */
    private final boolean binary;
    /**
     * Constructor.
     */
    private FetchLargeObjectRequestContext(FetchLargeObjectRequestContextBuilder b) {
        super();
        this.recordKey = b.recordKey;
        this.goldenKey = b.goldenKey;
        this.originKey = b.originKey;
        this.attribute = b.attribute;
        this.binary = b.binary;
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
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class FetchLargeObjectRequestContextBuilder {
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
         * Attribute name.
         */
        private String attribute;
        /**
         * Binary or character data.
         */
        private boolean binary;
        /**
         * Sets record key.
         * @param recordKey the key
         * @return self
         */
        public FetchLargeObjectRequestContextBuilder recordKey(String recordKey) {
            this.recordKey = recordKey;
            return this;
        }
        /**
         * Sets golden key.
         * @param goldenKey the key
         * @return self
         */
        public FetchLargeObjectRequestContextBuilder goldenKey(String goldenKey) {
            this.goldenKey = goldenKey;
            return this;
        }
        /**
         * Sets origin key.
         * @param originKey the key
         * @return self
         */
        public FetchLargeObjectRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }
        /**
         * Sets attribute name.
         * @param attribute the attribute name
         * @return self
         */
        public FetchLargeObjectRequestContextBuilder attribute(String attribute) {
            this.attribute = attribute;
            return this;
        }
        /**
         * Sets flag to return binary (or character) data.
         * @param binary the flag
         * @return self
         */
        public FetchLargeObjectRequestContextBuilder binary(boolean binary) {
            this.binary = binary;
            return this;
        }
        /**
         * Builds the context.
         * @return new context
         */
        public FetchLargeObjectRequestContext build() {
            return new FetchLargeObjectRequestContext(this);
        }
    }
}
