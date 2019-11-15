package org.unidata.mdm.core.context;

import org.unidata.mdm.system.context.CommonRequestContext;

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
        super(b);
        this.recordKey = b.recordKey;
        this.goldenKey = b.classifierKey;
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
     * @return the classifierKey
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
     * Convenience builder method.
     * @return builder
     */
    public static FetchLargeObjectRequestContextBuilder builder() {
        return new FetchLargeObjectRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class FetchLargeObjectRequestContextBuilder extends CommonRequestContextBuilder<FetchLargeObjectRequestContextBuilder> {
        /**
         * ID of the BLOB/CLOB record.
         */
        private String recordKey;
        /**
         * Classifier origin key.
         */
        private String classifierKey;
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
         * Sets classifier data origin key.
         * @param classifierKey the key
         * @return self
         */
        public FetchLargeObjectRequestContextBuilder classifierKey(String classifierKey) {
            this.classifierKey = classifierKey;
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
