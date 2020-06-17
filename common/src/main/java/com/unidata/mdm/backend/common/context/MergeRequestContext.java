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
package com.unidata.mdm.backend.common.context;

import java.util.List;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;

/**
 * @author Mikhail Mikhailov
 * Merge request context.
 */
public class MergeRequestContext
    extends CommonSendableContext
    implements RecordIdentityContext, ApprovalStateSettingContext {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -1140437951331151093L;
    /**
     * Duplicates list.
     */
    private final List<RecordIdentityContext> duplicates;
    /**
     * Is this merge an auto merge or a manual one.
     */
    private final boolean manual;
    /**
     * Force approval state.
     */
    private ApprovalState approvalState;
    /**
     * Etalon key.
     */
    private final String etalonKey;
    /**
     * Origin key.
     */
    private final String originKey;
    /**
     * Origin foreign id.
     */
    private final String externalId;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Origin name.
     */
    private final String sourceSystem;
    /**
     * Audit level.
     */
    private final short auditLevel;

    private final boolean batchUpsert;
    /**
     * Constructor.
     */
    private MergeRequestContext(MergeRequestContextBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.duplicates = b.duplicates;
        this.manual = b.manual;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;
        this.batchUpsert = b.batchUpsert;
    }

    /**
     * @return the etalonKey
     */
    @Override
    public String getEtalonKey() {
        return etalonKey;
    }

    /**
     * @return the originKey
     */
    @Override
    public String getOriginKey() {
        return originKey;
    }

    /**
     * @return the externalId
     */
    @Override
    public String getExternalId() {
        return externalId;
    }

    /**
     * @return the entityName
     */
    @Override
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the sourceSystem
     */
    @Override
    public String getSourceSystem() {
        return sourceSystem;
    }


    /**
     * @return the duplicates
     */
    public List<RecordIdentityContext> getDuplicates() {
        return duplicates;
    }

    /**
     * @return the manual
     */
    public boolean isManual() {
        return manual;
    }

    /**
     * @return the batch upsert flag
     */
    public boolean isBatchUpsert() {
        return batchUpsert;
    }

    /**
     * Force specific approval state upon upsert.
     * @return the approvalState
     */
    @Override
    public ApprovalState getApprovalState() {
        return approvalState;
    }


    public short getAuditLevel() {
        return auditLevel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(StorageId.DATA_MERGE_KEYS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.DATA_MERGE_KEYS;
    }

    /**
     * Builder shorthand.
     * @return builder
     */
    public static MergeRequestContextBuilder builder() {
        return new MergeRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Builder for the context.
     */
    public static class MergeRequestContextBuilder {

        /**
         * Golden key.
         */
        private String etalonKey;
        /**
         * Origin key.
         */
        private String originKey;
        /**
         * Origin foreign id.
         */
        private String externalId;
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * Origin name.
         */
        private String sourceSystem;
        /**
         * Duplicates list.
         */
        private List<RecordIdentityContext> duplicates;
        /**
         * Audit level.
         */
        private short auditLevel;

        /**
         * Is this merge an auto merge or a manual one.
         */
        private boolean manual;
        /**
         * Force approval state.
         */
        private ApprovalState approvalState;

        private boolean batchUpsert;
        /**
         * @param etalonKey the goldenKey to set
         */
        public MergeRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public MergeRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public MergeRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public MergeRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public MergeRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public MergeRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public MergeRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }
        /**
         * Sets the duplicates list.
         * @param duplicates the list to set
         * @return self
         */
        public MergeRequestContextBuilder duplicates(List<RecordIdentityContext> duplicates) {
            this.duplicates = duplicates;
            return this;
        }
        /**
         * Is this merge an auto merge or a manual one.
         * @param manual merge type
         * @return self
         */
        public MergeRequestContextBuilder manual(boolean manual) {
            this.manual= manual;
            return this;
        }
        /**
         * Force specific approval state.
         * @param approvalState the state
         * @return self
         */
        public MergeRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }
        /**
         * set audit level.
         * @param auditLevel the state
         * @return self
         */
        public MergeRequestContextBuilder auditLevel(short auditLevel) {
            this.auditLevel = auditLevel;
            return this;
        }
        /**
         * set batch upsert flag.
         * @param batchUpsert the batch upsert flag
         * @return self
         */
        public MergeRequestContextBuilder batchUpsert(boolean batchUpsert) {
            this.batchUpsert = batchUpsert;
            return this;
        }
        /**
         * The build method.
         * @return new merge context
         */
        public MergeRequestContext build() {
            return new MergeRequestContext(this);
        }
    }
}
