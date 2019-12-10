package org.unidata.mdm.data.context;

import java.util.Date;

import org.unidata.mdm.core.context.ApprovalStateSettingContext;
import org.unidata.mdm.core.context.MutableValidityRangeContext;
import org.unidata.mdm.core.type.audit.AuditLevel;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.data.type.data.OriginRelation;

/**
 * @author Mikhail Mikhailov
 * Delete relation context.
 */
public class DeleteRelationRequestContext
    extends AbstractRelationToRequestContext
    implements
        ReadWriteTimelineContext<OriginRelation>,
        ContainmentRelationContext<DeleteRequestContext>,
        MutableValidityRangeContext,
        BatchAwareContext,
        ApprovalStateSettingContext,
        AccessRightContext,
        SetupAwareContext,
        ReferenceRelationContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2751466540755521772L;
    /**
     * Valid from for this set.
     */
    private Date validFrom;
    /**
     * Valid to for this set.
     */
    private Date validTo;
    /**
     * Name of the relation.
     */
    private final String relationName;
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
    private DeleteRelationRequestContext(DeleteRelationRequestContextBuilder b) {
        super(b);
        this.relationName = b.relationName;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;

        flags.set(DataContextFlags.FLAG_INACTIVATE_WIPE, b.wipe);
        flags.set(DataContextFlags.FLAG_INACTIVATE_PERIOD, b.inactivatePeriod);
        flags.set(DataContextFlags.FLAG_INACTIVATE_ORIGIN, b.inactivateOrigin);
        flags.set(DataContextFlags.FLAG_INACTIVATE_ETALON, b.inactivateEtalon);
        flags.set(DataContextFlags.FLAG_WORKFLOW_ACTION, b.workflowAction);
        flags.set(DataContextFlags.FLAG_SUPPRESS_AUDIT, b.suppressAudit);
        flags.set(DataContextFlags.FLAG_BATCH_OPERATION, b.batchOperation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTypeId() {
        // TODO Auto-generated method stub
        return null;
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
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * Whether the context has relation name and type set.
     * @return true if so, false otherwise
     */
    public boolean isValid() {
        return relationName != null;
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
     * @return the wipe
     */
    public boolean isWipe() {
        return flags.get(DataContextFlags.FLAG_INACTIVATE_WIPE);
    }
    /**
     * @return suppressAudit
     */
    public boolean isSuppressAudit() {
        return flags.get(DataContextFlags.FLAG_SUPPRESS_AUDIT);
    }
    /**
     * Gets builder.
     * @return builder
     */
    public static DeleteRelationRequestContextBuilder builder() {
        return new DeleteRelationRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Delete relation request bulder class.
     */
    public static class DeleteRelationRequestContextBuilder
        extends AbstractRelationToRequestContext.AbstractRelationToRequestContextBuilder<DeleteRelationRequestContextBuilder> {
        /**
         * Valid from for this set.
         */
        private Date validFrom;
        /**
         * Valid to for this set.
         */
        private Date validTo;
        /**
         * Name of the relation.
         */
        private String relationName;
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
        private boolean batchOperation;
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
        public DeleteRelationRequestContextBuilder() {
           super();
        }
        /**
         * @param relationName the relationName to set
         * @return self
         */
        public DeleteRelationRequestContextBuilder relationName(String relationName) {
            this.relationName = relationName;
            return this;
        }

        /**
         * @param validFrom the validFrom to set
         */
        public DeleteRelationRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }
        /**
         * @param validTo the validTo to set
         */
        public DeleteRelationRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }
        /**
         * Inactivate period.
         * @param inactivatePeriod
         * @return
         */
        public DeleteRelationRequestContextBuilder inactivatePeriod(boolean inactivatePeriod) {
            this.inactivatePeriod = inactivatePeriod;
            return this;
        }
        /**
         * Inactivate origin flag.
         * @param inactivateOrigin
         * @return self
         */
        public DeleteRelationRequestContextBuilder inactivateOrigin(boolean inactivateOrigin) {
            this.inactivateOrigin = inactivateOrigin;
            return this;
        }
        /**
         * Inactivate etalon flag.
         * @param inactivateEtalon
         * return self
         */
        public DeleteRelationRequestContextBuilder inactivateEtalon(boolean inactivateEtalon) {
            this.inactivateEtalon = inactivateEtalon;
            return this;
        }
        /**
         * @param approvalState
         * @return
         */
        public DeleteRelationRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }
        /**
         * Wipe flag.
         * @param wipe the physical delete flag
         * @return self
         */
        public DeleteRelationRequestContextBuilder wipe(boolean wipe) {
            this.wipe = wipe;
            return this;
        }
        /**
         * @param workflowAction workflow action/rollback state signal
         * @return
         */
        public DeleteRelationRequestContextBuilder workflowAction(boolean workflowAction) {
            this.workflowAction = workflowAction;
            return this;
        }
       /**
        *
        * @param auditLevel - sets the audit level for this context
        * @return self
        */
        public DeleteRelationRequestContextBuilder auditLevel(short auditLevel) {
            this.auditLevel = auditLevel;
            return this;
        }
        /**
        *
        * @param suppressAudit - sets audit suppressed
        * @return self
        */
        public DeleteRelationRequestContextBuilder suppressAudit(boolean suppressAudit) {
            this.suppressAudit = suppressAudit;
            return this;
        }
        /**
         * @param batchUpsert the flag
         * @return self
         */
        public DeleteRelationRequestContextBuilder batchOperation(boolean batchUpsert) {
            this.batchOperation = batchUpsert;
            return this;
        }
        /**
         * Builder method.
         * @return context
         */
        @Override
        public DeleteRelationRequestContext build() {
            return new DeleteRelationRequestContext(this);
        }
    }
}
