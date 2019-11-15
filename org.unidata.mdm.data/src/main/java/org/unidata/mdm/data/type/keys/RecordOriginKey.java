package org.unidata.mdm.data.type.keys;

import java.io.Serializable;

import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.core.type.keys.OriginKey;

/**
 * Origin / vistory record identifier.
 * Consists of either origin id or a combination of source system, entity name and external id.
 */
public class RecordOriginKey extends OriginKey implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4323907649095688707L;
    /**
     * External id.
     */
    private final String externalId;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Saved box key.
     */
    private final String boxKey;
    /**
     * Constructor.
     * @param b builder
     */
    private RecordOriginKey(RecordOriginKeyBuilder b) {
        super(b);
        this.entityName = b.entityName;
        this.externalId = b.externalId;
        this.boxKey = String.join("|", getSourceSystem(), getExternalId());
    }
    /**
     * Gets the value of the externalId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExternalId() {
        return externalId;
    }
    /**
     * Gets the value of the entityName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEntityName() {
        return entityName;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toBoxKey() {
        return boxKey;
    }
    /**
     * Returns origins key as external id view.
     * @return
     */
    public ExternalId toExternalId() {
        return ExternalId.of(externalId, entityName, sourceSystem);
    }
    /**
     * Creates new builder object.
     * @param other object to copy
     * @return builder
     */
    public static RecordOriginKeyBuilder builder(RecordOriginKey other) {
        return new RecordOriginKeyBuilder(other);
    }
    /**
     * Creates new builder object.
     * @return builder
     */
    public static RecordOriginKeyBuilder builder() {
        return new RecordOriginKeyBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class RecordOriginKeyBuilder extends OriginKeyBuilder<RecordOriginKeyBuilder> {
        /**
         * External id.
         */
        private String externalId;
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * Constructor.
         */
        private RecordOriginKeyBuilder() {
            super();
        }
        /**
         * Copy constructor.
         */
        private RecordOriginKeyBuilder(RecordOriginKey other) {
            super(other);
            this.entityName = other.entityName;
            this.externalId = other.externalId;
        }
        /**
         * Sets the value of the externalId property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         */
        public RecordOriginKeyBuilder externalId(String value) {
            this.externalId = value;
            return self();
        }
        /**
         * Sets the value of the entityName property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         */
        public RecordOriginKeyBuilder entityName(String value) {
            this.entityName = value;
            return self();
        }
        /**
         * Build the object.
         * @return object
         */
        @Override
        public RecordOriginKey build() {
            return new RecordOriginKey(this);
        }
    }
}
