package com.unidata.mdm.backend.common.keys;

import com.unidata.mdm.backend.common.types.RecordStatus;

import java.io.Serializable;

/**
 * Etalon record id.
 */
public class EtalonKey implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6495921977514956243L;
    /**
     * The id.
     */
    private final String id;
    /**
     * Global sequence number.
     */
    private final Long gsn;
    /**
     * Etalon status.
     */
    private RecordStatus status;
    /**
     * Constructor.
     * @param b builder.
     */
    private EtalonKey(EtalonKeyBuilder b) {
        super();
        this.id = b.id;
        this.gsn = b.gsn;
        this.status = b.status;
    }
    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     */
    public String getId() {
        return id;
    }
    /**
     * Gets the value of the gsn property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     */
    public Long getGsn() {
        return gsn;
    }
    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link RecordStatus }
     */
    public RecordStatus getStatus() {
        return status;
    }
    /**
     * Builder.
     * @return
     */
    public static EtalonKeyBuilder builder() {
        return new EtalonKeyBuilder();
    }
    /**
     * Copy.
     * @return
     */
    public static EtalonKeyBuilder builder(EtalonKey other) {
        return new EtalonKeyBuilder(other);
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class EtalonKeyBuilder {
        /**
         * The id.
         */
        private String id;
        /**
         * Global sequence number.
         */
        private Long gsn;
        /**
         * Etalon status.
         */
        private RecordStatus status;
        /**
         * Constructor.
         */
        private EtalonKeyBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param other
         */
        private EtalonKeyBuilder(EtalonKey other) {
            super();
            this.id = other.id;
            this.gsn = other.gsn;
            this.status = other.status;
        }
        /**
         * Sets the value of the id property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         * @return builder
         */
        public EtalonKeyBuilder id(String value) {
            this.id = value;
            return this;
        }
        /**
         * Sets the value of the gsn property.
         *
         * @param value
         *     allowed object is
         *     {@link Long }
         *
         * @return builder
         */
        public EtalonKeyBuilder gsn(Long value) {
            this.gsn = value;
            return this;
        }
        /**
         * Sets the value of the status property.
         *
         * @param status
         *     allowed object is
         *     {@link RecordStatus }
         *
         * @return builder
         */
        public EtalonKeyBuilder status(RecordStatus status) {
            this.status = status;
            return this;
        }
        /**
         * Build.
         * @return key
         */
        public EtalonKey build() {
            return new EtalonKey(this);
        }
    }
}
