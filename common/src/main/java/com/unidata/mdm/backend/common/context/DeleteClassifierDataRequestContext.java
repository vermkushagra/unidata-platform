package com.unidata.mdm.backend.common.context;

import java.util.Date;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;

/**
 * @author Mikhail Mikhailov
 * Delete relation context.
 */
public class DeleteClassifierDataRequestContext
    extends CommonSendableContext implements ClassifierIdentityContext, ValidityRange {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2751466540755521772L;
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
     * Valid from for this set.
     */
    private final Date validFrom;
    /**
     * Valid to for this set.
     */
    private final Date validTo;
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
    private DeleteClassifierDataRequestContext(DeleteClassifierDataRequestContextBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.classifierName = b.classifierName;
        this.classifierNodeId = b.classifierNodeId;
        this.classifierNodeCode = b.classifierNodeCode;
        this.classifierNodeName = b.classifierNodeName;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.classifierEtalonKey = b.classifierEtalonKey;
        this.classifierOriginKey = b.classifierOriginKey;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;

        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_WIPE, b.wipe);
        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_PERIOD, b.inactivatePeriod);
        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_ORIGIN, b.inactivateOrigin);
        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_ETALON, b.inactivateEtalon);
        flags.set(ContextUtils.CTX_FLAG_WORKFLOW_ACTION, b.workflowAction);
        flags.set(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT, b.suppressAudit);
        flags.set(ContextUtils.CTX_FLAG_BATCH_UPSERT, b.batchUpsert);
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
     * @return the validFrom
     */
    @Override
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @return the validTo
     */
    @Override
    public Date getValidTo() {
        return validTo;
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
     * @return the approvalState
     */
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
     * @return the inactivatePeriod
     */
    public boolean isInactivatePeriod() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_PERIOD);
    }

    /**
     * @return the inactivateOrigin
     */
    public boolean isInactivateOrigin() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_ORIGIN);
    }

    /**
     * @return the inactivateEtalon
     */
    public boolean isInactivateEtalon() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_ETALON);
    }

    /**
     * @return the skipSuspendWorkflow
     */
    public boolean isWorkflowAction() {
        return flags.get(ContextUtils.CTX_FLAG_WORKFLOW_ACTION);
    }

    /**
     * @return the wipe
     */
    public boolean isWipe() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_WIPE);
    }
    /**
     * @return true, if this context is a part of a batch upsert
     */
    public boolean isBatchUpsert() {
        return flags.get(ContextUtils.CTX_FLAG_BATCH_UPSERT);
    }

    /**
     * @return suppressAudit
     */
    public boolean isSuppressAudit() {
        return flags.get(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(ClassifierIdentityContext.super.keysId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeys classifierKeys() {
        return getFromStorage(ClassifierIdentityContext.super.classifierKeysId());
    }

    /**
     * Builder.
     * @return
     */
    public static DeleteClassifierDataRequestContextBuilder builder() {
        return new DeleteClassifierDataRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Delete relation request bulder class.
     */
    public static class DeleteClassifierDataRequestContextBuilder {
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
         * A version for inactive period should be put above all.
         */
        private boolean inactivatePeriod;
        /**
         * Inactivate origin flag.
         */
        private boolean inactivateOrigin;
        /**
         * Inactivate etalon flag.
         */
        private boolean inactivateEtalon;
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
         * Force approval state.
         */
        private ApprovalState approvalState;
        /**
         * Wipe flag.
         */
        private boolean wipe;
        /**
         * Skips process and tasks suspending, if set to true.
         */
        private boolean workflowAction;
        /**
         * Constructor.
         */
        private DeleteClassifierDataRequestContextBuilder() {
           super();
        }

        /**
         * @param classifierEtalonKey the classifierEtalonKey to set
         */
        public DeleteClassifierDataRequestContextBuilder classifierEtalonKey(String classifierEtalonKey) {
            this.classifierEtalonKey = classifierEtalonKey;
            return this;
        }
        /**
         * @param classifierOriginKey the classifierOriginKey to set
         */
        public DeleteClassifierDataRequestContextBuilder classifierOriginKey(String classifierOriginKey) {
            this.classifierOriginKey = classifierOriginKey;
            return this;
        }
        /**
         * Sets classifier name.
         * @param classifierName the classifier name
         * @return self
         */
        public DeleteClassifierDataRequestContextBuilder classifierName(String classifierName) {
            this.classifierName = classifierName;
            return this;
        }
        /**
         * Set classifier node id.
         * @param classifierNodeId -
         * @return self
         */
        public DeleteClassifierDataRequestContextBuilder classifierNodeId(String classifierNodeId) {
            this.classifierNodeId = classifierNodeId;
            return this;
        }
        /**
         * Classifier node name.
         */
        public DeleteClassifierDataRequestContextBuilder classifierNodeName(String classifierNodeName) {
            this.classifierNodeName = classifierNodeName;
            return this;
        }
        /**
         * Classifier node code.
         */
        public DeleteClassifierDataRequestContextBuilder classifierNodeCode(String classifierNodeCode) {
            this.classifierNodeCode = classifierNodeCode;
            return this;
        }
        /**
         * @param validFrom the validFrom to set
         */
        public DeleteClassifierDataRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }


        /**
         * @param validTo the validTo to set
         */
        public DeleteClassifierDataRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteClassifierDataRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteClassifierDataRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteClassifierDataRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteClassifierDataRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public DeleteClassifierDataRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public DeleteClassifierDataRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public DeleteClassifierDataRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }
        /**
         * Inactivate period.
         * @param inactivatePeriod
         * @return
         */
        public DeleteClassifierDataRequestContextBuilder inactivatePeriod(boolean inactivatePeriod) {
            this.inactivatePeriod = inactivatePeriod;
            return this;
        }
        /**
         * Inactivate origin flag.
         * @param inactivateOrigin
         * @return self
         */
        public DeleteClassifierDataRequestContextBuilder inactivateOrigin(boolean inactivateOrigin) {
            this.inactivateOrigin = inactivateOrigin;
            return this;
        }
        /**
         * Inactivate etalon flag.
         * @param inactivateEtalon
         * return self
         */
        public DeleteClassifierDataRequestContextBuilder inactivateEtalon(boolean inactivateEtalon) {
            this.inactivateEtalon = inactivateEtalon;
            return this;
        }
        /**
         * @param approvalState
         * @return
         */
        public DeleteClassifierDataRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }
        /**
         * Wipe flag.
         * @param wipe the physical delete flag
         * @return self
         */
        public DeleteClassifierDataRequestContextBuilder wipe(boolean wipe) {
            this.wipe = wipe;
            return this;
        }
        /**
         * @param workflowAction workflow action/rollback state signal
         * @return
         */
        public DeleteClassifierDataRequestContextBuilder workflowAction(boolean workflowAction) {
            this.workflowAction = workflowAction;
            return this;
        }
       /**
        *
        * @param auditLevel - sets the audit level for this context
        * @return self
        */
        public DeleteClassifierDataRequestContextBuilder auditLevel(short auditLevel) {
            this.auditLevel = auditLevel;
            return this;
        }
        /**
        *
        * @param suppressAudit - sets audit suppressed
        * @return self
        */
        public DeleteClassifierDataRequestContextBuilder suppressAudit(boolean suppressAudit) {
            this.suppressAudit = suppressAudit;
            return this;
        }
        /**
         * @param batchUpsert the flag
         * @return self
         */
        public DeleteClassifierDataRequestContextBuilder batchUpsert(boolean batchUpsert) {
            this.batchUpsert = batchUpsert;
            return this;
        }
        /**
         * Builder method.
         * @return context
         */
        public DeleteClassifierDataRequestContext build() {
            return new DeleteClassifierDataRequestContext(this);
        }
    }
}
