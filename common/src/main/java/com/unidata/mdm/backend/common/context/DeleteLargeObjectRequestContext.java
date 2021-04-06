package com.unidata.mdm.backend.common.context;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DeleteLargeObjectRequestContext extends CommonRequestContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 5088228957783645486L;
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
    private DeleteLargeObjectRequestContext(DeleteLargeObjectRequestContextBuilder b) {
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

    public static class DeleteLargeObjectRequestContextBuilder {
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
        public DeleteLargeObjectRequestContextBuilder recordKey(String recordKey) {
            this.recordKey = recordKey;
            return this;
        }
        /**
         * Sets golden key.
         * @param goldenKey the key
         * @return self
         */
        public DeleteLargeObjectRequestContextBuilder goldenKey(String goldenKey) {
            this.goldenKey = goldenKey;
            return this;
        }
        /**
         * Sets origin key.
         * @param originKey the key
         * @return self
         */
        public DeleteLargeObjectRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }
        /**
         * Sets attribute name.
         * @param attribute the attribute name
         * @return self
         */
        public DeleteLargeObjectRequestContextBuilder attribute(String attribute) {
            this.attribute = attribute;
            return this;
        }
        /**
         * Sets flag to return binary (or character) data.
         * @param binary the flag
         * @return self
         */
        public DeleteLargeObjectRequestContextBuilder binary(boolean binary) {
            this.binary = binary;
            return this;
        }
        /**
         * Builds the context.
         * @return new context
         */
        public DeleteLargeObjectRequestContext build() {
            return new DeleteLargeObjectRequestContext(this);
        }
    }
}
