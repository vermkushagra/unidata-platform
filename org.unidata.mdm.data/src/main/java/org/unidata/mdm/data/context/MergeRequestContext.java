/**
 *
 */
package org.unidata.mdm.data.context;

import java.util.List;

import org.unidata.mdm.core.context.ApprovalStateSettingContext;
import org.unidata.mdm.core.type.audit.AuditLevel;
import org.unidata.mdm.core.type.data.ApprovalState;

/**
 * @author Mikhail Mikhailov
 * Merge request context.
 */
public class MergeRequestContext
    extends AbstractRecordIdentityContext
    implements MergeCapableContext, BatchAwareContext, ApprovalStateSettingContext {

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
     * Rule id.
     */
    private final Integer ruleId;
    /**
     * Audit level.
     */
    private final short auditLevel;

    private final boolean upRecordsToContext;

    private final Integer shardNumber;

    private final boolean skipRelations;

    private final boolean skipClassifiers;

    private final boolean clearPreprocessing;

    private final boolean withTranstition;

    /**
     * Constructor.
     */
    protected MergeRequestContext(MergeRequestContextBuilder b) {
        super(b);
        this.ruleId = b.ruleId;
        this.duplicates = b.duplicates;
        this.manual = b.manual;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;
        this.upRecordsToContext = b.upRecordsToContext;
        this.shardNumber = b.shardNumber;
        this.skipRelations = b.skipRelations;
        this.skipClassifiers = b.skipClassifiers;
        this.clearPreprocessing = b.clearPreprocessing;
        this.withTranstition = b.withTransition;

        flags.set(DataContextFlags.FLAG_BATCH_OPERATION, b.batchOperation);
    }
    /**
     * @return the duplicates
     */
    public List<RecordIdentityContext> getDuplicates() {
        return duplicates;
    }

    /**
     * @return the ruleId
     */
    public Integer getRuleId() {
        return ruleId;
    }

    /**
     * @return the manual
     */
    public boolean isManual() {
        return manual;
    }

    /**
     * @return the with transition flag
     */
    public boolean isWithTranstition() {
        return withTranstition;
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
     * @return the bypassExtensionPoints
     */
    public boolean isUpRecordsToContext() {
        return upRecordsToContext;
    }

    /**
     * @return the shardNumber
     */
    public Integer getShardNumber() {
        return shardNumber;
    }

    /**
     * @return the skip relations flag
     */
    public boolean isSkipRelations() {
        return skipRelations;
    }

    /**
     * @return the skip classifiers flag
     */
    public boolean isSkipClassifiers() {
        return skipClassifiers;
    }
    /**
     * @return the clear preprocessing flag
     */
    public boolean isClearPreprocessing() {
        return clearPreprocessing;
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
    public static class MergeRequestContextBuilder extends AbstractRecordIdentityContextBuilder<MergeRequestContextBuilder> {
        /**
         * Duplicates list.
         */
        private List<RecordIdentityContext> duplicates;
        /**
         * Audit level.
         */
        private short auditLevel = AuditLevel.AUDIT_SUCCESS;

        /**
         * Is this merge an auto merge or a manual one.
         */
        private boolean manual;
        /**
         * Force approval state.
         */
        private ApprovalState approvalState;
        /**
         * Batch upsert flag
         */
        private boolean batchOperation;

        /**
         * Bypass extension points during merge.
         */
        private boolean upRecordsToContext = true;
        /**
         * Shard number
         */
        private Integer shardNumber;

        private boolean skipRelations = false;

        private boolean skipClassifiers = false;

        private boolean clearPreprocessing = true;

        private boolean withTransition = true;

        private Integer ruleId;

        protected MergeRequestContextBuilder() {
            super();
        }

        /**
         * @param ruleId the ruleId to set
         */
        public MergeRequestContextBuilder ruleId(Integer ruleId) {
            this.ruleId = ruleId;
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
        public MergeRequestContextBuilder batchOperation(boolean batchUpsert) {
            this.batchOperation = batchUpsert;
            return this;
        }

        /**
         * @param upRecordsToContext up records to context or note
         */
        public MergeRequestContextBuilder upRecordsToContext(boolean upRecordsToContext) {
            this.upRecordsToContext = upRecordsToContext;
            return this;
        }


        /**
         * @param shardNumber shard Number
         */
        public MergeRequestContextBuilder shardNumber(Integer shardNumber) {
            this.shardNumber = shardNumber;
            return this;
        }

        /**
         * @param skipRelations skip relation on merge
         */
        public MergeRequestContextBuilder skipRelations(boolean skipRelations) {
            this.skipRelations = skipRelations;
            return this;
        }

        /**
         * @param skipClassifiers skip classifiers on merge
         */
        public MergeRequestContextBuilder skipClassifiers(boolean skipClassifiers) {
            this.skipClassifiers = skipClassifiers;
            return this;
        }

        /**
         * @param clearPreprocessing clear preprocessing
         */
        public MergeRequestContextBuilder clearPreprocessing(boolean clearPreprocessing) {
            this.clearPreprocessing = clearPreprocessing;
            return this;
        }

        /**
         * @param withTransition with transtition
         */
        public MergeRequestContextBuilder withTransition(boolean withTransition) {
            this.withTransition = withTransition;
            return this;
        }
        /**
         * The build method.
         * @return new merge context
         */
        @Override
        public MergeRequestContext build() {
            return new MergeRequestContext(this);
        }
    }
}
