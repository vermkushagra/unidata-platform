package com.unidata.mdm.backend.common.context;

import java.util.Date;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Classifier data upsert context.
 */
public class UpsertClassifierDataRequestContext extends AbstractClassifierDataRequestContext implements ApprovalStateSettingContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 208438926009686159L;
    /**
     * Data record.
     */
    private final DataRecord classifier;
    /**
     * Valid from for this set.
     */
    private final Date validFrom;
    /**
     * Valid to for this set.
     */
    private final Date validTo;
    /**
     * Name of the classifier.
     */
    private final String classifierName;
    /**
     * Classifier node id.
     */
    private final String classifierNodeId;
    /**
     * Classifier node name.
     */
    private final String classifierNodeName;
    /**
     * Classifier node code.
     */
    private final String classifierNodeCode;
    /**
     * Classifier etalon id.
     */
    private final String classifierEtalonKey;
    /**
     * Classifier origin id.
     */
    private final String classifierOriginKey;
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
     * Record status.
     */
    private final RecordStatus status;
    /**
     * Force approval state.
     */
    private final ApprovalState approvalState;
    /**
     * Audit level.
     */
    private final short auditLevel;
    /**
     * Constructor.
     */
    private UpsertClassifierDataRequestContext(UpsertClassifierDataRequestContextBuilder b) {

        super();
        this.approvalState = b.approvalState;
        this.status = b.status;
        this.classifier = b.classifier;
        this.classifierEtalonKey = b.classifierEtalonKey;
        this.classifierName = b.classifierName;
        this.classifierNodeId = b.classifierNodeId;
        this.classifierNodeCode = b.classifierNodeCode;
        this.classifierNodeName = b.classifierNodeName;
        this.classifierOriginKey = b.classifierOriginKey;
        this.entityName = b.entityName;
        this.etalonKey = b.etalonKey;
        this.externalId = b.externalId;
        this.originKey = b.originKey;
        this.sourceSystem = b.sourceSystem;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.auditLevel = b.auditLevel;

        flags.set(ContextUtils.CTX_FLAG_INCLUDE_DRAFT_VERSIONS, b.includeDraftVersions);
        flags.set(ContextUtils.CTX_FLAG_MERGE_WITH_PREVIOUS_VERSION, b.mergeWithPreviousVersion);
        flags.set(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT, b.suppressAudit);
        flags.set(ContextUtils.CTX_FLAG_BATCH_UPSERT, b.batchUpsert);
        flags.set(ContextUtils.CTX_FLAG_INITIAL_LOAD, b.initialLoad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(super.keysId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEtalonKey() {
        return etalonKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOriginKey() {
        return originKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return externalId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityName() {
        return entityName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassifierEtalonKey() {
        return classifierEtalonKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassifierOriginKey() {
        return classifierOriginKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassifierName() {
        return classifierName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassifierNodeId() {
        return classifierNodeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassifierNodeName() {
        return classifierNodeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassifierNodeCode() {
        return classifierNodeCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeys classifierKeys() {
        return getFromStorage(super.classifierKeysId());
    }

    /**
     * @return the classifier
     */
    public DataRecord getClassifier() {
        return classifier;
    }

    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @return the validTo
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * @return the status
     */
    public RecordStatus getStatus() {
        return status;
    }

    /**
     * @return the approvalState
     */
    @Override
    public ApprovalState getApprovalState() {
        return approvalState;
    }

    /**
     * @return the auditLevel
     */
    public short getAuditLevel() {
        return auditLevel;
    }

    /**
     * @return the includeDraftVersions
     */
    public boolean isIncludeDraftVersions() {
        return flags.get(ContextUtils.CTX_FLAG_INCLUDE_DRAFT_VERSIONS);
    }

    /**
     * Merge with previous version?
     * @return true if so, false otherwise
     */
    public boolean isMergeWithPreviousVersion() {
        return flags.get(ContextUtils.CTX_FLAG_MERGE_WITH_PREVIOUS_VERSION);
    }

    /**
     * @return true, if this context is a part of a batch upsert
     */
    public boolean isBatchUpsert() {
        return flags.get(ContextUtils.CTX_FLAG_BATCH_UPSERT);
    }

    /**
     * @return true, if this context is a part of initial load process
     */
    public boolean isInitialLoad() {
        return flags.get(ContextUtils.CTX_FLAG_INITIAL_LOAD);
    }

    /**
     * @return suppressAudit
     */
    public boolean isSuppressAudit() {
        return flags.get(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT);
    }

    /**
     * Creates new builder.
     * @return builder
     */
    public static UpsertClassifierDataRequestContextBuilder builder() {
        return new UpsertClassifierDataRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder.
     */
    public static class UpsertClassifierDataRequestContextBuilder {
        /**
         * Data record.
         */
        private DataRecord classifier;
        /**
         * Valid from for this set.
         */
        private Date validFrom;
        /**
         * Valid to for this set.
         */
        private Date validTo;
        /**
         * Name of the classifier.
         */
        private String classifierName;
        /**
         * Classifier node id.
         */
        private String classifierNodeId;
        /**
         * Classifier node name.
         */
        private String classifierNodeName;
        /**
         * Classifier node code.
         */
        private String classifierNodeCode;
        /**
         * Classifier etalon id.
         */
        private String classifierEtalonKey;
        /**
         * Classifier origin id.
         */
        private String classifierOriginKey;
        /**
         * Etalon key.
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
         * Include draft versions into various calculations or not (approver view).
         */
        private boolean includeDraftVersions;
        /**
         * Record status.
         */
        private RecordStatus status;
        /**
         * Force approval state.
         */
        private ApprovalState approvalState;
        /**
         * merge with previous version
         */
        private boolean mergeWithPreviousVersion = false;
        /**
         * Audit level.
         */
        private short auditLevel = AuditLevel.AUDIT_SUCCESS;
        /**
         * Suppress audit upon upsert.
         */
        private boolean suppressAudit;
        /**
         * This context is participating in a batch upsert. Collect artifacts instead of upserting immediately.
         */
        private boolean batchUpsert;
        /**
         * This context is participating in initial load process. Skips relation key resolution.
         */
        private boolean initialLoad;
        /**
         * Constructor.
         */
        private UpsertClassifierDataRequestContextBuilder() {
            super();
        }
        /**
         * @return the classifier
         */
        public UpsertClassifierDataRequestContextBuilder classifier(DataRecord classifier) {
            this.classifier = classifier;
            return this;
        }
        /**
         * @param validFrom the validFrom to set
         */
        public UpsertClassifierDataRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }
        /**
         * @param validTo the validTo to set
         */
        public UpsertClassifierDataRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }
        /**
         * @param classifierEtalonKey the classifierEtalonKey to set
         */
        public UpsertClassifierDataRequestContextBuilder classifierEtalonKey(String classifierEtalonKey) {
            this.classifierEtalonKey = classifierEtalonKey;
            return this;
        }
        /**
         * @param classifierOriginKey the classifierOriginKey to set
         */
        public UpsertClassifierDataRequestContextBuilder classifierOriginKey(String classifierOriginKey) {
            this.classifierOriginKey = classifierOriginKey;
            return this;
        }
        /**
         * Sets classifier name.
         * @param classifierName the classifier name
         * @return self
         */
        public UpsertClassifierDataRequestContextBuilder classifierName(String classifierName) {
            this.classifierName = classifierName;
            return this;
        }
        /**
         * Set classifier node id.
         * @param classifierNodeId -
         * @return self
         */
        public UpsertClassifierDataRequestContextBuilder classifierNodeId(String classifierNodeId) {
            this.classifierNodeId = classifierNodeId;
            return this;
        }
        /**
         * Classifier node name.
         */
        public UpsertClassifierDataRequestContextBuilder classifierNodeName(String classifierNodeName) {
            this.classifierNodeName = classifierNodeName;
            return this;
        }
        /**
         * Classifier node code.
         */
        public UpsertClassifierDataRequestContextBuilder classifierNodeCode(String classifierNodeCode) {
            this.classifierNodeCode = classifierNodeCode;
            return this;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public UpsertClassifierDataRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }
        /**
         * @param originKey the etalonKey to set
         */
        public UpsertClassifierDataRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }
        /**
         * @param externalId the externalId to set
         */
        public UpsertClassifierDataRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }
        /**
         * @param entityName the entityName to set
         */
        public UpsertClassifierDataRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }
        /**
         * @param sourceSystem the sourceSystem to set
         */
        public UpsertClassifierDataRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }
        /**
         * @param etalonKey the goldenKey to set
         */
        public UpsertClassifierDataRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }
        /**
         * @param originKey the goldenKey to set
         */
        public UpsertClassifierDataRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }
        /**
         * @param includeDraftVersions include draft versions or not
         * @return self
         */
        public UpsertClassifierDataRequestContextBuilder includeDraftVersions(boolean includeDraftVersions) {
            this.includeDraftVersions = includeDraftVersions;
            return this;
        }
        /**
         * @param status the status to set
         */
        public UpsertClassifierDataRequestContextBuilder status(RecordStatus status) {
            this.status = status;
            return this;
        }
        /**
         * @param approvalState
         * @return
         */
        public UpsertClassifierDataRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }
        /**
        *
        * @param enrichByPreviousVersion - enrich By Previous Version
        * @return self
        */
        public UpsertClassifierDataRequestContextBuilder mergeWithPrevVersion(boolean enrichByPreviousVersion) {
            this.mergeWithPreviousVersion = enrichByPreviousVersion;
            return this;
        }
       /**
        *
        * @param auditLevel - sets the audit level for this context
        * @return self
        */
        public UpsertClassifierDataRequestContextBuilder auditLevel(short auditLevel) {
            this.auditLevel = auditLevel;
            return this;
        }
       /**
        *
        * @param suppressAudit - sets audit suppressed
        * @return self
        */
        public UpsertClassifierDataRequestContextBuilder suppressAudit(boolean suppressAudit) {
            this.suppressAudit = suppressAudit;
            return this;
        }
        /**
         * @param batchUpsert the flag
         * @return self
         */
        public UpsertClassifierDataRequestContextBuilder batchUpsert(boolean batchUpsert) {
            this.batchUpsert = batchUpsert;
            return this;
        }
        /**
         * @param initialLoad the flag
         * @return self
         */
        public UpsertClassifierDataRequestContextBuilder initialLoad(boolean initialLoad) {
            this.initialLoad = initialLoad;
            return this;
        }
        /**
         * Builder method.
         * @return context
         */
        public UpsertClassifierDataRequestContext build() {
            return new UpsertClassifierDataRequestContext(this);
        }
    }
}
