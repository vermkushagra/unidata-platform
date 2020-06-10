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

package com.unidata.mdm.backend.common.keys;

import java.io.Serializable;

import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * Origin / vistory record identifier.
 * Consists of either origin id or a combination of source system, entity name and external id.
 */
public class OriginKey implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4323907649095688707L;
    /**
     * Origin id.
     */
    private final String id;
    /**
     * External id.
     */
    private final String externalId;
    /**
     * Source system.
     */
    private final String sourceSystem;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Enrichment
     */
    private final boolean enriched;
    /**
     * Global sequence number.
     */
    private final Long gsn;
    /**
     * This origin's current revision.
     */
    private final int revision;
    /**
     * Has approved versions or not.
     */
    private final boolean approvedRevisions;
    /**
     * Origin status.
     */
    private final RecordStatus status;
    /**
     * Constructor.
     * @param b builder
     */
    private OriginKey(OriginKeyBuilder b) {
        super();
        this.id = b.id;
        this.entityName = b.entityName;
        this.externalId = b.externalId;
        this.sourceSystem = b.sourceSystem;
        this.enriched = b.enrichment;
        this.gsn = b.gsn;
        this.revision = b.revision;
        this.approvedRevisions = b.approvedRevisions;
        this.status = b.status;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
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
     * Gets the value of the sourceSystem property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSourceSystem() {
        return sourceSystem;
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
     * Gets the value of the enrichment property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isEnriched() {
        return enriched;
    }
    /**
     * @return the approvedRevisions
     */
    public boolean hasApprovedRevisions() {
        return approvedRevisions;
    }

    /**
     * Gets the value of the gsn property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getGsn() {
        return gsn;
    }
    /**
     * Gets the value of the revision property.
     *
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }
    /**
     * Gets the value of the status property.
     *
     * @return the status
     */
    public RecordStatus getStatus() {
        return status;
    }

    /**
     * Creates new builder object.
     * @param other object to copy
     * @return builder
     */
    public static OriginKeyBuilder builder(OriginKey other) {
        return new OriginKeyBuilder(other);
    }
    /**
     * Creates new builder object.
     * @return builder
     */
    public static OriginKeyBuilder builder() {
        return new OriginKeyBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class OriginKeyBuilder {
        /**
         * Origin id.
         */
        private String id;
        /**
         * External id.
         */
        private String externalId;
        /**
         * Source system.
         */
        private String sourceSystem;
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * enrichment
         */
        private boolean enrichment;
        /**
         * Global sequence number.
         */
        private Long gsn;
        /**
         * Has approved versions or not.
         */
        private boolean approvedRevisions;
        /**
         * This origin's current revision.
         */
        private int revision = 0;
        /**
         * Origin status.
         */
        private RecordStatus status;
        /**
         * Constructor.
         */
        public OriginKeyBuilder() {
            super();
        }
        /**
         * Copy constructor.
         */
        public OriginKeyBuilder(OriginKey other) {
            super();
            this.entityName = other.entityName;
            this.externalId = other.externalId;
            this.id = other.id;
            this.sourceSystem = other.sourceSystem;
            this.enrichment = other.enriched;
            this.gsn = other.gsn;
            this.revision = other.revision;
            this.approvedRevisions = other.approvedRevisions;
            this.status = other.status;
        }
        /**
         * Sets the value of the id property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         */
        public OriginKeyBuilder id(String value) {
            this.id = value;
            return this;
        }
        /**
         * Sets the value of the externalId property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         */
        public OriginKeyBuilder externalId(String value) {
            this.externalId = value;
            return this;
        }
        /**
         * Sets the value of the sourceSystem property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         */
        public OriginKeyBuilder sourceSystem(String value) {
            this.sourceSystem = value;
            return this;
        }
        /**
         * Sets the value of the entityName property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         */
        public OriginKeyBuilder entityName(String value) {
            this.entityName = value;
            return this;
        }
        /**
         * Sets the value of the entityName property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         */
        public OriginKeyBuilder enrichment(boolean value) {
            this.enrichment = value;
            return this;
        }
        /**
         * Sets the value of the gsn property.
         *
         * @param value
         *     allowed object is
         *     {@link Long }
         */
        public OriginKeyBuilder gsn(Long value) {
            this.gsn = value;
            return this;
        }
        /**
         * Sets the value of the revision property.
         *
         * @param value
         *     allowed object is
         *     {@link Integer }
         */
        public OriginKeyBuilder revision(int value) {
            this.revision = value;
            return this;
        }
        /**
         * Sets the value of the approvedRevisions property.
         *
         * @param approvedRevisions
         *     allowed object is
         *     {@link Boolean }
         */
        public OriginKeyBuilder approvedRevisions(boolean approvedRevisions) {
            this.approvedRevisions = approvedRevisions;
            return this;
        }
        /**
         * Sets the value of the status property.
         *
         * @param status
         *     allowed object is
         *     {@link RecordStatus }
         */
        public OriginKeyBuilder status(RecordStatus status) {
            this.status = status;
            return this;
        }
        /**
         * Build the object.
         * @return object
         */
        public OriginKey build() {
            return new OriginKey(this);
        }
    }
}
