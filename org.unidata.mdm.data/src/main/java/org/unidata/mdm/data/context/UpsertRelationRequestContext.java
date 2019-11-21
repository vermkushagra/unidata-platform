package org.unidata.mdm.data.context;

import java.util.Date;

import org.unidata.mdm.core.context.ApprovalStateSettingContext;
import org.unidata.mdm.core.context.MutableValidityRangeContext;
import org.unidata.mdm.core.context.UserExitExecutableContext;
import org.unidata.mdm.core.type.audit.AuditLevel;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.keys.ReferenceAliasKey;
import org.unidata.mdm.data.type.data.OriginRelation;

/**
 * @author Mikhail Mikhailov
 * Single relation upsert request context.
 */
public class UpsertRelationRequestContext
    extends AbstractRelationToRequestContext
    implements
        ReadWriteTimelineContext<OriginRelation>,
        ContainmentRelationContext<UpsertRequestContext>,
        MutableValidityRangeContext,
        ApprovalStateSettingContext,
        UpsertIndicatorContext,
        BatchAwareContext,
        UserExitExecutableContext,
        OperationTypeContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -2042264082583817129L;
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
     * Relations to.
     */
    private final DataRecord relation;
    /**
     * Reference alias key
     */
    private final ReferenceAliasKey referenceAliasKey;
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
    private UpsertRelationRequestContext(UpsertRelationRequestContextBuilder b) {
        super(b);
        this.relationName = b.relationName;
        this.relation = b.relation;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.referenceAliasKey = b.referenceAliasKey;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;

        flags.set(DataContextFlags.FLAG_INCLUDE_DRAFT_VERSIONS, b.includeDraftVersions);
        flags.set(DataContextFlags.FLAG_BATCH_OPERATION, b.batchOperation);
        flags.set(DataContextFlags.FLAG_EMPTY_STORAGE, b.emptyStorage);
        flags.set(DataContextFlags.FLAG_BYPASS_EXTENSION_POINTS, b.bypassExtensionPoints);
        flags.set(DataContextFlags.FLAG_SUPPRESS_WORKFLOW, b.suppressWorkflow);
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
        this.validFrom =from;
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
     * @return the relation
     */
    public DataRecord getRelation() {
        return relation;
    }

    /**
     *
     * @return reference alias key
     */
    public ReferenceAliasKey getReferenceAliasKey() {
        return referenceAliasKey;
    }

    /**
     * @return the includeDraftVersions
     */
    public boolean isIncludeDraftVersions() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_DRAFT_VERSIONS);
    }

    /**
     * @return true, if this record not saved in storage before
     */
    public boolean isEmptyStorage() {
        return flags.get(DataContextFlags.FLAG_EMPTY_STORAGE);
    }

    /**
     * @return the bypassExtensionPoints
     */
    @Override
    public boolean isBypassExtensionPoints() {
        return flags.get(DataContextFlags.FLAG_BYPASS_EXTENSION_POINTS);
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
     * @return suppressWorkflow
     */
    public boolean isSuppressWorkflow() {
        return flags.get(DataContextFlags.FLAG_SUPPRESS_WORKFLOW);
    }
    /**
     * Creates new builder instance.
     * @return new builder instance.
     */
    public static UpsertRelationRequestContextBuilder builder() {
        return new UpsertRelationRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Upsert relation request bulder class.
     */
    public static class UpsertRelationRequestContextBuilder
        extends AbstractRelationToRequestContextBuilder<UpsertRelationRequestContextBuilder> {
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
         * Relations to.
         */
        private DataRecord relation;
        /**
         * Reference alias key
         */
        private ReferenceAliasKey referenceAliasKey;
        /**
         * Include draft versions into various calculations or not (approver view).
         */
        private boolean includeDraftVersions;
        /**
         * Force approval state.
         */
        private ApprovalState approvalState;
        /**
         * This context is participating in a batch upsert. Collect artifacts instead of upserting immediately.
         */
        private boolean batchOperation;
        /**
         * This context is participating in initial load process. Skips relation key resolution.
         */
        private boolean emptyStorage;
        /**
         * Bypass extension points during upsert relation.
         */
        private boolean bypassExtensionPoints;
        /**
         * suppress Workflow
         */
        private boolean suppressWorkflow;
        /**
         * Audit level.
         */
        private short auditLevel = AuditLevel.AUDIT_SUCCESS;
        /**
         * Constructor.
         */
        public UpsertRelationRequestContextBuilder() {
           super();
        }

        /**
         * @return the relation
         */
        public UpsertRelationRequestContextBuilder relation(DataRecord relation) {
            this.relation = relation;
            return this;
        }

        /**
         * @param validFrom the validFrom to set
         */
        public UpsertRelationRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        /**
         * @param validTo the validTo to set
         */
        public UpsertRelationRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }

        /**
         * Sets relation name.
         * @param relationName the relation name
         * @return self
         */
        public UpsertRelationRequestContextBuilder relationName(String relationName) {
            this.relationName = relationName;
            return this;
        }

        /**
         * Set reference alias key
         * @param referenceAliasKey -
         * @return self
         */
        public UpsertRelationRequestContextBuilder referenceAliasKey(ReferenceAliasKey referenceAliasKey) {
            this.referenceAliasKey = referenceAliasKey;
            return this;
        }

        /**
         * @param includeDraftVersions include draft versions or not
         * @return self
         */
        public UpsertRelationRequestContextBuilder includeDraftVersions(boolean includeDraftVersions) {
            this.includeDraftVersions = includeDraftVersions;
            return this;
        }

        /**
         * @param approvalState
         * @return
         */
        public UpsertRelationRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }

        /**
         * @param batchUpsert the flag
         * @return self
         */
        public UpsertRelationRequestContextBuilder batchOperation(boolean batchUpsert) {
            this.batchOperation = batchUpsert;
            return this;
        }

        /**
         * @param emptyStorage the flag
         * @return self
         */
        public UpsertRelationRequestContextBuilder emptyStorage(boolean emptyStorage) {
            this.emptyStorage = emptyStorage;
            return this;
        }

        /**
         * @param bypassExtensionPoints bypass extension points or not
         */
        public UpsertRelationRequestContextBuilder bypassExtensionPoints(boolean bypassExtensionPoints) {
            this.bypassExtensionPoints = bypassExtensionPoints;
            return this;
        }

        /**
         *
         * @param auditLevel - sets the audit level for this context
         * @return self
         */
        public UpsertRelationRequestContextBuilder auditLevel(short auditLevel) {
            this.auditLevel = auditLevel;
            return this;
        }

        public UpsertRelationRequestContextBuilder suppressWorkflow(boolean suppressWorkflow){
            this.suppressWorkflow = suppressWorkflow;
            return this;
        }
        /**
         * Builder method.
         * @return context
         */
        @Override
        public UpsertRelationRequestContext build() {
            return new UpsertRelationRequestContext(this);
        }
    }
}
