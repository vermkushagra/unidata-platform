package org.unidata.mdm.data.context;

import java.util.Date;

import org.unidata.mdm.core.context.ApprovalStateSettingContext;
import org.unidata.mdm.core.context.MutableValidityRangeContext;
import org.unidata.mdm.core.type.audit.AuditLevel;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.data.service.segments.RecordDeleteStartExecutor;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author Mikhail Mikhailov
 * Delete context.
 */
public class DeleteRequestContext
    extends AbstractRecordIdentityContext
    implements
        PipelineInput,
        MutableValidityRangeContext,
        ApprovalStateSettingContext,
        OperationTypeContext,
        AccessRightContext,
        BatchAwareContext,
        ReadWriteTimelineContext<OriginRecord> {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -5350865249523055692L;
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
    protected DeleteRequestContext(DeleteRequestContextBuilder b) {
        super(b);

        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.previousStatus = b.previousStatus;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;

        flags.set(DataContextFlags.FLAG_INACTIVATE_CASCADE, b.cascade);
        flags.set(DataContextFlags.FLAG_INACTIVATE_WIPE, b.wipe);
        flags.set(DataContextFlags.FLAG_INACTIVATE_PERIOD, b.inactivatePeriod);
        flags.set(DataContextFlags.FLAG_INACTIVATE_ORIGIN, b.inactivateOrigin);
        flags.set(DataContextFlags.FLAG_INACTIVATE_ETALON, b.inactivateEtalon);
        flags.set(DataContextFlags.FLAG_WORKFLOW_ACTION, b.workflowAction);
        flags.set(DataContextFlags.FLAG_SUPPRESS_AUDIT, b.suppressAudit);
        flags.set(DataContextFlags.FLAG_BATCH_OPERATION, b.batchOperation);
    }

    @Override
    public String getStartTypeId() {
        return RecordDeleteStartExecutor.SEGMENT_ID;
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
        return flags.get(DataContextFlags.FLAG_INACTIVATE_CASCADE);
    }

    /**
     * @return the wipe
     */
    public boolean isWipe() {
        return flags.get(DataContextFlags.FLAG_INACTIVATE_WIPE);
    }

    /**
     * @return the inactivatePeriod
     */
    public boolean isInactivatePeriod() {
        return flags.get(DataContextFlags.FLAG_INACTIVATE_PERIOD);
    }

    /**
     * @return the inactivateOrigin
     */
    public boolean isInactivateOrigin() {
        return flags.get(DataContextFlags.FLAG_INACTIVATE_ORIGIN);
    }

    /**
     * @return the inactivateEtalon
     */
    public boolean isInactivateEtalon() {
        return flags.get(DataContextFlags.FLAG_INACTIVATE_ETALON);
    }

    /**
     * @return the skipSuspendWorkflow
     */
    public boolean isWorkflowAction() {
        return flags.get(DataContextFlags.FLAG_WORKFLOW_ACTION);
    }

    /**
     * @return suppressAudit
     */
    public boolean isSuppressAudit() {
        return flags.get(DataContextFlags.FLAG_SUPPRESS_AUDIT);
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
    public static class DeleteRequestContextBuilder extends AbstractRecordIdentityContextBuilder<DeleteRequestContextBuilder> {
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
        private boolean batchOperation;
        /**
         * Constructor.
         */
        protected DeleteRequestContextBuilder() {
            super();
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
        public DeleteRequestContextBuilder batchOperation(boolean batchUpsert) {
            this.batchOperation = batchUpsert;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public DeleteRequestContext build() {
            return new DeleteRequestContext(this);
        }
    }
}
