package com.unidata.mdm.backend.common.context;

import java.util.Date;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Delete context.
 */
public class DeleteRequestContext
    extends CommonSendableContext
    implements RecordIdentityContext, MutableValidityRangeContext, ApprovalStateSettingContext {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -5350865249523055692L;
    /**
     * Golden key.
     */
    private final String etalonKey;
    /**
     * Origin key.
     */
    private final String originKey;
    /**
     * Version (de)activation attribute validFrom.
     */
    private Date validFrom;
    /**
     * Version (de)activation attribute validTo.
     */
    private Date validTo;
    /**
     * Origin foreign system key.
     */
    private final String externalId;
    /**
     * Origin source system.
     */
    private final String sourceSystem;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Status that record had before delete.
     */
    private  RecordStatus previousStatus;
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
    private DeleteRequestContext(DeleteRequestContextBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.externalId = b.externalId;
        this.sourceSystem = b.sourceSystem;
        this.entityName = b.entityName;
        this.previousStatus = b.previousStatus;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;

        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_CASCADE, b.cascade);
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
    public void setValidFrom(Date from) {
        this.validFrom = from;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValidTo(Date to) {
        this.validTo = to;
    }

    /**
     * @return the entityName
     */
    @Override
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the externalId
     */
    @Override
    public String getExternalId() {
        return externalId;
    }

    /**
     * @return the sourceSystem
     */
    @Override
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
	 * @return the previousStatus
	 */
	public RecordStatus getPreviousStatus() {
		return previousStatus;
	}

	/**
	 * @param previousStatus the previousStatus validTo set
	 */
	public void setPreviousStatus(RecordStatus previousStatus) {
		this.previousStatus = previousStatus;
	}

	/**
     * @return the cascade
     */
    public boolean isCascade() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_CASCADE);
    }

    /**
     * @return the wipe
     */
    public boolean isWipe() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_WIPE);
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
     * @return suppressAudit
     */
    public boolean isSuppressAudit() {
        return flags.get(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT);
    }

    /**
     * @return true, if this context is a part of a batch upsert
     */
    public boolean isBatchUpsert() {
        return flags.get(ContextUtils.CTX_FLAG_BATCH_UPSERT);
    }

    /**
     * Force specific approval state upon upsert.
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
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(StorageId.DATA_DELETE_KEYS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.DATA_DELETE_KEYS;
    }

    /**
     * Builder shorthand.
     * @return builder
     */
    public static DeleteRequestContextBuilder builder() {
        return new DeleteRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Builder
     */
    public static class DeleteRequestContextBuilder {
        /**
         * Etalon key.
         */
        private String etalonKey;
        /**
         * Origin key.
         */
        private String originKey;
        /**
         * Version (de)activation attribute validFrom.
         */
        private Date validFrom;
        /**
         * Version (de)activation attribute validTo.
         */
        private Date validTo;
        /**
         * Status that record had before delete.
         */
        private RecordStatus previousStatus;
        /**
         * Origin foreign system key.
         */
        private String externalId;
        /**
         * Origin source system.
         */
        private String sourceSystem;
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * (Soft) deletes golden and origin records.
         */
        private boolean cascade;
        /**
         * Physically remove data validFrom the storage.
         */
        private boolean wipe;
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
         * Skips process and tasks suspending, if set to true.
         */
        private boolean workflowAction;
        /**
         * Force approval state.
         */
        private ApprovalState approvalState;
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
         * Constructor.
         */
        public DeleteRequestContextBuilder() {
            super();
        }

        /**
         * @param etalonKey the etalonKey validTo set
         */
        public DeleteRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the originKey validTo set
         */
        public DeleteRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * @param from the validFrom validTo set
         */
        public DeleteRequestContextBuilder validFrom(Date from) {
            this.validFrom = from;
            return this;
        }

        /**
         * @param to the validTo validTo set
         */
        public DeleteRequestContextBuilder validTo(Date to) {
            this.validTo = to;
            return this;
        }

        /**
         * @param externalId the externalId validTo set
         */
        public DeleteRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem validTo set
         */
        public DeleteRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param entityName the entityName validTo set
         */
        public DeleteRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }
        /**
         * @param cascade the cascade validTo set
         */
        public DeleteRequestContextBuilder cascade(boolean cascade) {
            this.cascade = cascade;
            return this;
        }
        /**
         * @param previousStatus the previousStatus validTo set
         */
        public DeleteRequestContextBuilder previousStatus(RecordStatus previousStatus) {
            this.previousStatus = previousStatus;
            return this;
        }
        /**
         * Wipe flag.
         * @param wipe the physical delete flag
         * @return self
         */
        public DeleteRequestContextBuilder wipe(boolean wipe) {
            this.wipe = wipe;
            return this;
        }
        /**
         * Inactivate period.
         * @param inactivatePeriod
         * @return
         */
        public DeleteRequestContextBuilder inactivatePeriod(boolean inactivatePeriod) {
            this.inactivatePeriod = inactivatePeriod;
            return this;
        }
        /**
         * Inactivate origin flag.
         * @param inactivateOrigin
         * @return self
         */
        public DeleteRequestContextBuilder inactivateOrigin(boolean inactivateOrigin) {
            this.inactivateOrigin = inactivateOrigin;
            return this;
        }
        /**
         * Inactivate etalon flag.
         * @param inactivateEtalon
         * return self
         */
        public DeleteRequestContextBuilder inactivateEtalon(boolean inactivateEtalon) {
            this.inactivateEtalon = inactivateEtalon;
            return this;
        }
        /**
         * @param workflowAction workflow action/rollback state signal
         * @return
         */
        public DeleteRequestContextBuilder workflowAction(boolean workflowAction) {
            this.workflowAction = workflowAction;
            return this;
        }
        /**
         * Sppress audt events emission during upsert.
         * @param suppressAudit flag
         * @return self
         */
        public DeleteRequestContextBuilder suppressAudit(boolean suppressAudit) {
            this.suppressAudit = suppressAudit;
            return this;
        }
        /**
         * Force specific approval state.
         * @param approvalState the state
         * @return self
         */
        public DeleteRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }
       /**
        *
        * @param auditLevel - sets the audit level for this context
        * @return self
        */
        public DeleteRequestContextBuilder auditLevel(short auditLevel) {
            this.auditLevel = auditLevel;
            return this;
        }
        /**
         * @param batchUpsert the flag
         * @return self
         */
        public DeleteRequestContextBuilder batchUpsert(boolean batchUpsert) {
            this.batchUpsert = batchUpsert;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        public DeleteRequestContext build() {
            return new DeleteRequestContext(this);
        }
    }
}
