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

package org.unidata.mdm.data.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.unidata.mdm.core.context.ApprovalStateSettingContext;
import org.unidata.mdm.core.context.MutableValidityRangeContext;
import org.unidata.mdm.core.context.UserExitExecutableContext;
import org.unidata.mdm.core.type.audit.AuditLevel;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.data.service.segments.records.RecordUpsertStartExecutor;
import org.unidata.mdm.data.type.data.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * Record upsert.
 */
public class UpsertRequestContext
    extends AbstractRecordIdentityContext
    implements
        ExternalIdResettingContext,
        MutableValidityRangeContext,
        ApprovalStateSettingContext,
        UserExitExecutableContext,
        ReadWriteTimelineContext<OriginRecord>,
        UpsertIndicatorContext,
        OperationTypeContext,
        AccessRightContext,
        BatchAwareContext,
        SetupAwareContext {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = 6651928422821780602L;
    /**
     * Golden record.
     */
    private final DataRecord record;
    /**
     * Last update date to use (optional).
     */
    private final Date lastUpdate;
    /**
     * Set range from.
     */
    private Date validFrom;
    /**
     * Set range to.
     */
    private Date validTo;
    /**
     * Origin status to put.
     */
    private final RecordStatus originStatus;
    /**
     * Force specific approval state upon upsert.
     */
    private final ApprovalState approvalState;
    /**
     * Relations
     */
    private  UpsertRelationsRequestContext relations;
    /**
     * Relations
     */
    private  DeleteRelationsRequestContext relationDeletes;
    /**
     * Cpde attribute aliases.
     */
    private final Collection<CodeAttributeAlias> codeAttributeAliases;
    /**
     * Audit level.
     */
    private final short auditLevel;
    /**
     * The box key.
     */
    protected String boxKey;
    /**
     * Constructor.
     */
    protected UpsertRequestContext(UpsertRequestContextBuilder b) {
        super(b);

        this.record = b.record;
        this.lastUpdate = b.lastUpdate;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.originStatus = b.originStatus;
        this.approvalState = b.approvalState;
        this.codeAttributeAliases = b.codeAttributeAliases;
        this.auditLevel = b.auditLevel;

        flags.set(DataContextFlags.FLAG_IS_ENRICHMENT, b.enrichment);
        flags.set(DataContextFlags.FLAG_SKIP_DQ, b.skipCleanse);
        flags.set(DataContextFlags.FLAG_BYPASS_EXTENSION_POINTS, b.bypassExtensionPoints);
        flags.set(DataContextFlags.FLAG_RECALCULATE_WHOLE_TIMELINE, b.recalculateWholeTimeline);
        flags.set(DataContextFlags.FLAG_IS_RECORD_RESTORE, b.isRestore);
        flags.set(DataContextFlags.FLAG_IS_PERIOD_RESTORE, b.isPeriodRestore);
        flags.set(DataContextFlags.FLAG_INCLUDE_DRAFTS, b.includeDraftVersions);
        flags.set(DataContextFlags.FLAG_MERGE_WITH_PREVIOUS_VERSION, b.mergeWithPreviousVersion);
        flags.set(DataContextFlags.FLAG_SKIP_INDEX_DROP, b.skipIndexDrop);
        flags.set(DataContextFlags.FLAG_SKIP_MATCHING_PREPROCESSING, b.skipMatchingPreprocessing);
        flags.set(DataContextFlags.FLAG_SUPPRESS_AUDIT, b.suppressAudit);
        flags.set(DataContextFlags.FLAG_BATCH_OPERATION, b.batchOperation);
        flags.set(DataContextFlags.FLAG_EMPTY_STORAGE, b.emptyStorage);
        flags.set(DataContextFlags.FLAG_SKIP_CONSISTENCY_CHECKS, b.skipConsistencyChecks);
        flags.set(DataContextFlags.FLAG_SKIP_MATCHING, b.skipMatching);
        flags.set(DataContextFlags.FLAG_SUPPRESS_WORKFLOW, b.suppressWorkflow);
        flags.set(DataContextFlags.FLAG_IS_APPLY_DRAFT, b.isApplyDraft);

        this.relations = b.relations == null || b.relations.isEmpty()
                ? null
                : UpsertRelationsRequestContext.builder()
                        .relations(b.relations)
                        .etalonKey(getEtalonKey())
                        .originKey(getOriginKey())
                        .externalId(getExternalId())
                        .sourceSystem(getSourceSystem())
                        .entityName(getEntityName())
                        .build();

        this.relationDeletes = b.relationDeletes == null || b.relationDeletes.isEmpty()
                ? null
                : DeleteRelationsRequestContext.builder()
                .relations(b.relationDeletes)
                .etalonKey(getEtalonKey())
                .originKey(getOriginKey())
                .externalId(getExternalId())
                .sourceSystem(getSourceSystem())
                .entityName(getEntityName())
                .build();

        // TODO: @Modules
//        this.classifierUpserts = b.classifierUpserts == null || b.classifierUpserts.isEmpty()
//                ? null
//                : UpsertClassifiersDataRequestContext.builder()
//                        .classifiers(b.classifierUpserts)
//                        .etalonKey(getEtalonKey())
//                        .originKey(getOriginKey())
//                        .externalId(getExternalId())
//                        .sourceSystem(getSourceSystem())
//                        .entityName(getEntityName())
//                        .build();
//        this.classifierDeletes = b.classifierDeletes == null || b.classifierDeletes.isEmpty()
//                ? null
//                : DeleteClassifiersDataRequestContext.builder()
//                        .classifiers(b.classifierDeletes)
//                        .etalonKey(getEtalonKey())
//                        .originKey(getOriginKey())
//                        .externalId(getExternalId())
//                        .sourceSystem(getSourceSystem())
//                        .entityName(getEntityName())
//                        .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTypeId() {
        return RecordUpsertStartExecutor.SEGMENT_ID;
    }

    /**
     * @return the record
     */
    public DataRecord getRecord() {
        return record;
    }

    /**
     * @return the lastUpdate
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExternalId(String externalId) {
        this.externalId = this.externalId != null
                ? ExternalId.of(externalId, this.externalId.getEntityName(), this.externalId.getSourceSystem())
                : null;
    }

    /**
     * @return the skipCleanse
     */
    public boolean isSkipCleanse() {
        return flags.get(DataContextFlags.FLAG_SKIP_DQ);
    }

    /**
     * @return the skipConsistencyChecks
     */
    public boolean isSkipConsistencyChecks() {
        return flags.get(DataContextFlags.FLAG_SKIP_CONSISTENCY_CHECKS);
    }

    /**
     * @return the skipMatching preprocessing
     */
    public boolean isSkipMatchingPreprocessing() {
        return flags.get(DataContextFlags.FLAG_SKIP_MATCHING_PREPROCESSING);
    }

    /**
     * @return the skipMatching
     */
    public boolean isSkipMatching() {
        return flags.get(DataContextFlags.FLAG_SKIP_MATCHING);
    }

    /**
     * @return suppressAudit
     */
    public boolean isSuppressAudit() {
        return flags.get(DataContextFlags.FLAG_SUPPRESS_AUDIT);
    }
    /**
     * @return the bypassExtensionPoints
     */
    @Override
    public boolean isBypassExtensionPoints() {
        return flags.get(DataContextFlags.FLAG_BYPASS_EXTENSION_POINTS);
    }

    /**
     * @return the recalculateWholeTimeline
     */
    public boolean isRecalculateWholeTimeline() {
        return flags.get(DataContextFlags.FLAG_RECALCULATE_WHOLE_TIMELINE);
    }

    /**
     * @return define that is restore request.
     */
    public boolean isRecordRestore() {
        return flags.get(DataContextFlags.FLAG_IS_RECORD_RESTORE);
    }

    /**
     * @return define that is period restore request.
     */
    public boolean isPeriodRestore() {
        return flags.get(DataContextFlags.FLAG_IS_PERIOD_RESTORE);
    }
    /**
     * @return true, if this context is a part of initial load process
     */
    public boolean isEmptyStorage() {
        return flags.get(DataContextFlags.FLAG_EMPTY_STORAGE);
    }

    /**
     * @return true, if need force suppress workflow, else false
     */
    public boolean isSuppressWorkflow() {
        return flags.get(DataContextFlags.FLAG_SUPPRESS_WORKFLOW);
    }

    /**
     * @return true, if is publishing context, else not
     */
    public boolean isApplyDraft() {
        return flags.get(DataContextFlags.FLAG_IS_APPLY_DRAFT);
    }

    /**
     * Merge with previous version?
     * @return true if so, false otherwise
     */
    public boolean isMergeWithPreviousVersion() {
        return flags.get(DataContextFlags.FLAG_MERGE_WITH_PREVIOUS_VERSION);
    }

    /**
     * @return the includeDraftVersions
     */
    public boolean isIncludeDraftVersions() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_DRAFTS);
    }

    /**
     * Skips drop operation upon index info creation.
     * @return
     */
    public boolean isSkipIndexDrop() {
        return flags.get(DataContextFlags.FLAG_SKIP_INDEX_DROP);
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
     * Gets relation upsert records.
     * @return the relations
     */
    public UpsertRelationsRequestContext getRelations() {
        return relations;
    }

    /**
     * Gets relation delete records.
     * @return the relations deletes
     */
    public DeleteRelationsRequestContext getRelationDeletes() {
        return relationDeletes;
    }
    /**
     * Set relations.
     * @param relations relations
     */
    public void setRelations(UpsertRelationsRequestContext relations) {
        this.relations = relations;
   }

    // TODO: @Modules
//    /**
//     * Gets classifier data upsert records.
//     * @return the classifiers
//     */
//    public UpsertClassifiersDataRequestContext getClassifierUpserts() {
//        return classifierUpserts;
//    }
//
//    /**
//     * @return the classifierDeletes
//     */
//    public DeleteClassifiersDataRequestContext getClassifierDeletes() {
//        return classifierDeletes;
//    }

    /**
     * @return the codeAttributeAliases
     */
    public Collection<CodeAttributeAlias> getCodeAttributeAliases() {
        return codeAttributeAliases == null ? Collections.emptyList() : codeAttributeAliases;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEtalonRecordKey() {
        return !flags.get(DataContextFlags.FLAG_IS_ENRICHMENT) && ExternalIdResettingContext.super.isEtalonRecordKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOriginRecordKey() {
        return !flags.get(DataContextFlags.FLAG_IS_ENRICHMENT) && ExternalIdResettingContext.super.isOriginRecordKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOriginExternalId() {
        return !flags.get(DataContextFlags.FLAG_IS_ENRICHMENT) && ExternalIdResettingContext.super.isOriginExternalId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnrichmentKey() {
        return flags.get(DataContextFlags.FLAG_IS_ENRICHMENT)
                && ExternalIdResettingContext.super.isEtalonRecordKey()
                && ExternalIdResettingContext.super.isOriginExternalId();
    }

    /**
     * Is the context a valid upsert golden record context.
     * @return true, if the context is a valid upsert golden record context
     */
    public boolean isEtalon() {
        return hasData() && isEtalonRecordKey();
    }

    /**
     * Is the context a valid upsert origin record context.
     * @return true, if the context is a valid upsert origin record context
     */
    public boolean isOrigin() {
        return hasData() && (isOriginExternalId() || isOriginRecordKey());
    }

    /**
     * Is the context a valid upsert origin record context.
     * @return true, if the context is a valid upsert origin record context
     */
    public boolean isEnrichment() {
        return hasData() && isEnrichmentKey();
    }

    /**
     * Is the context a valid upsert golden record context.
     * @return true, if the context is a valid upsert golden record context
     */
    public boolean hasData() {
        return record != null;
    }

    /**
     * @return the originStatus
     */
    public RecordStatus getOriginStatus() {
        return originStatus;
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
     * Builder shortcut.
     * @return builder
     */
    public static UpsertRequestContextBuilder builder() {
        return new UpsertRequestContextBuilder();
    }

    /**
     * Re-packaging builder shorthand.
     * @return builder
     */
    public static UpsertRequestContextBuilder builder(UpsertRequestContext other) {
        return new UpsertRequestContextBuilder(other);
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class UpsertRequestContextBuilder extends AbstractRecordIdentityContext.AbstractRecordIdentityContextBuilder<UpsertRequestContextBuilder> {
        /**
         * The data record.
         */
        private DataRecord record;
        /**
         * Enrichment record.
         */
        private boolean enrichment;
        /**
         * Last update date to use (optional).
         */
        private Date lastUpdate;
        /**
         * Skip cleanse functions.
         */
        private boolean skipCleanse;
        /**
         * Skip consistency checks, performed by DQ.
         */
        private boolean skipConsistencyChecks;
        /**
         * Skip matching preprocessing part of the upsert.
         */
        private boolean skipMatchingPreprocessing;
        /**
         * Suppress audit upon upsert.
         */
        private boolean suppressAudit;
        /**
         * Bypass extension points during upsert.
         */
        private boolean bypassExtensionPoints;
        /**
         * Tells the etalon calculation routine,
         * that the whole time line must be completely recalculated.
         */
        private boolean recalculateWholeTimeline;
        /**
         * Set range from.
         */
        private Date validFrom;
        /**
         * Set range to.
         */
        private Date validTo;
        /**
         * Origin status to put.
         */
        private RecordStatus originStatus;
        /**
         * define that is restore request.
         */
        private boolean isRestore;
        /**
         * define that is restore period request.
         */
        private boolean isPeriodRestore;
        /**
         * Include draft versions into various calculations or not (approver view).
         */
        private boolean includeDraftVersions;
        /**
         * Force approval state.
         */
        private ApprovalState approvalState;
        /**
         * merge with previous version
         */
        private boolean mergeWithPreviousVersion;
        /**
         * Skip or perform index drop. This might be true for reindex job, which did explicit cleanup before run.
         */
        private boolean skipIndexDrop;
        /**
         * This context is participating in a batch upsert. Collect artifacts instead of upserting immediately.
         */
        private boolean batchOperation;
        /**
         * This context is participating in initial load process. Skips relation key resolution.
         */
        private boolean emptyStorage;

        /**
         * Relations
         */
        private Map<String, List<UpsertRelationRequestContext>> relations;
        /**
         * Relation deletes
         */
        private Map<String, List<DeleteRelationRequestContext>> relationDeletes;

        // TODO: @Modules
//        /**
//         * Classifier data records.
//         */
//        private Map<String, List<UpsertClassifierDataRequestContext>> classifierUpserts;
//        /**
//         * Classifier deletes.
//         */
//        private Map<String, List<DeleteClassifierDataRequestContext>> classifierDeletes;
        /**
         * Cpde attribute aliases.
         */
        private Collection<CodeAttributeAlias> codeAttributeAliases;
        /**
         * Audit level.
         */
        private short auditLevel = AuditLevel.AUDIT_SUCCESS;
        /**
         * Skip matching main phase part of the upsert.
         */
        private boolean skipMatching;
        /**
         * force Skip workflow or not
         */
        private boolean suppressWorkflow;
        /**
         * is apply draft context indication
         */
        private boolean isApplyDraft;
        /**
         * Constructor.
         */
        protected UpsertRequestContextBuilder() {
            super();
        }

        /**
         * Constructor.
         */
        protected UpsertRequestContextBuilder(UpsertRequestContext other) {
            super(other);
            this.record = other.record;
            this.lastUpdate = other.lastUpdate;
            this.validFrom = other.validFrom;
            this.validTo = other.validTo;
            this.originStatus = other.originStatus;
            this.approvalState = other.approvalState;
            this.codeAttributeAliases = other.codeAttributeAliases;
            this.auditLevel = other.auditLevel;

            this.enrichment = other.flags.get(DataContextFlags.FLAG_IS_ENRICHMENT);
            this.skipCleanse = other.flags.get(DataContextFlags.FLAG_SKIP_DQ);
            this.skipConsistencyChecks = other.flags.get(DataContextFlags.FLAG_SKIP_CONSISTENCY_CHECKS);
            this.bypassExtensionPoints = other.flags.get(DataContextFlags.FLAG_BYPASS_EXTENSION_POINTS);
            this.recalculateWholeTimeline = other.flags.get(DataContextFlags.FLAG_RECALCULATE_WHOLE_TIMELINE);
            this.isRestore = other.flags.get(DataContextFlags.FLAG_IS_RECORD_RESTORE);
            this.isPeriodRestore = other.flags.get(DataContextFlags.FLAG_IS_PERIOD_RESTORE);
            this.includeDraftVersions = other.flags.get(DataContextFlags.FLAG_INCLUDE_DRAFTS);
            this.mergeWithPreviousVersion = other.flags.get(DataContextFlags.FLAG_MERGE_WITH_PREVIOUS_VERSION);
            this.skipMatchingPreprocessing = other.flags.get(DataContextFlags.FLAG_SKIP_MATCHING_PREPROCESSING);
            this.suppressAudit = other.flags.get(DataContextFlags.FLAG_SUPPRESS_AUDIT);
            this.batchOperation = other.flags.get(DataContextFlags.FLAG_BATCH_OPERATION);
            this.emptyStorage = other.flags.get(DataContextFlags.FLAG_EMPTY_STORAGE);
            this.skipMatching = other.flags.get(DataContextFlags.FLAG_SKIP_MATCHING);
            this.suppressWorkflow = other.flags.get(DataContextFlags.FLAG_SUPPRESS_WORKFLOW);
            this.isApplyDraft = other.flags.get(DataContextFlags.FLAG_IS_APPLY_DRAFT);

            // Sub contexts
            this.relations = other.relations != null ? other.relations.getRelations() : null;
            this.relationDeletes = other.relationDeletes != null ? other.relationDeletes.getRelations() : null;
            // TODO: @Modules
//            this.classifierDeletes = other.classifierDeletes != null ? other.classifierDeletes.getClassifiers() : null;
//            this.classifierUpserts = other.classifierUpserts != null ? other.classifierUpserts.getClassifiers() : null;
        }

        /**
         * @param record the golden record to set
         */
        public UpsertRequestContextBuilder record(DataRecord record) {
            this.record = record;
            return this;
        }

        /**
         * @param enrichment the enrichment
         */
        public UpsertRequestContextBuilder enrichment(boolean enrichment) {
            this.enrichment = enrichment;
            return this;
        }
        /**
         * @param lastUpdate the last update to set
         */
        public UpsertRequestContextBuilder lastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        /**
         * @param skipCleanse skip cleanse or not
         */
        public UpsertRequestContextBuilder skipCleanse(boolean skipCleanse) {
            this.skipCleanse = skipCleanse;
            return this;
        }

        /**
         * @param skipConsistencyChecks skip consistency checks or not
         */
        public UpsertRequestContextBuilder skipConsistencyChecks(boolean skipConsistencyChecks) {
            this.skipConsistencyChecks = skipConsistencyChecks;
            return this;
        }


        /**
         * @param skipMatchingPreprocessing skip matching preprocessing or not
         */
        public UpsertRequestContextBuilder skipMatchingPreprocessing(boolean skipMatchingPreprocessing) {
            this.skipMatchingPreprocessing = skipMatchingPreprocessing;
            return this;
        }

        /**
         * @param bypassExtensionPoints bypass extension points or not
         */
        public UpsertRequestContextBuilder bypassExtensionPoints(boolean bypassExtensionPoints) {
            this.bypassExtensionPoints = bypassExtensionPoints;
            return this;
        }

        /**
         * Re-index whole timeline _WITHOUT_ any save actions.
         * @param recalculateWholeTimeline the flag
         * @return self
         */
        public UpsertRequestContextBuilder recalculateWholeTimeline(boolean recalculateWholeTimeline) {
            this.recalculateWholeTimeline = recalculateWholeTimeline;
            return this;
        }

        /**
         * Sppress audt events emission during upsert.
         * @param suppressAudit flag
         * @return self
         */
        public UpsertRequestContextBuilder suppressAudit(boolean suppressAudit) {
            this.suppressAudit = suppressAudit;
            return this;
        }

        /**
         * @param batchUpsert the flag
         * @return self
         */
        public UpsertRequestContextBuilder batchOperation(boolean batchUpsert) {
            this.batchOperation = batchUpsert;
            return this;
        }

        /**
         * @param emptyStorage the flag
         * @return self
         */
        public UpsertRequestContextBuilder emptyStorage(boolean emptyStorage) {
            this.emptyStorage = emptyStorage;
            return this;
        }

        /**
         * @param validFrom the range from to set
         */
        public UpsertRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }
        /**
         * @param validTo the range to to set
         */
        public UpsertRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }
        /**
         * @param originStatus the originStatus to to set
         */
        public UpsertRequestContextBuilder originStatus(RecordStatus originStatus) {
            this.originStatus = originStatus;
            return this;
        }
        /**
         * @param includeDraftVersions include draft versions or not
         * @return self
         */
        public UpsertRequestContextBuilder includeDraftVersions(boolean includeDraftVersions) {
            this.includeDraftVersions = includeDraftVersions;
            return this;
        }

        /**
         * define that is restore request.
         * @param recordRestore
         * @return self
         */
        public UpsertRequestContextBuilder recordRestore(boolean recordRestore) {
            isRestore = recordRestore;
            return this;
        }

        /**
         * define that that is a period restore request.
         * @param periodRestore
         * @return self
         */
        public UpsertRequestContextBuilder periodRestore(boolean periodRestore) {
            isPeriodRestore = periodRestore;
            return this;
        }

        /**
         * Force specific approval state.
         * @param approvalState the state
         * @return self
         */
        public UpsertRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }

        /**
         * @param relation - record relation
         * @return self
         */
        public UpsertRequestContextBuilder addRelation(UpsertRelationRequestContext relation) {

            if (Objects.isNull(this.relations)) {
                this.relations = new HashMap<>();
            }

            if (!this.relations.containsKey(relation.getRelationName())) {
                this.relations.put(relation.getRelationName(), new ArrayList<>());
            }

            this.relations.get(relation.getRelationName()).add(relation);
            return this;
        }

        /**
         *
         * @param relations - record relations
         * @return self
         */
        public UpsertRequestContextBuilder addRelations(Collection<UpsertRelationRequestContext> relations) {
            relations.stream().forEach(this::addRelation);
            return this;
        }

        /**
         * @param relation - relation context to delete
         * @return self
         */
        public UpsertRequestContextBuilder addRelationDelete(DeleteRelationRequestContext relation) {

            if (Objects.isNull(this.relationDeletes)) {
                this.relationDeletes = new HashMap<>();
            }

            if (!this.relationDeletes.containsKey(relation.getRelationName())) {
                this.relationDeletes.put(relation.getRelationName(), new ArrayList<>());
            }

            this.relationDeletes.get(relation.getRelationName()).add(relation);
            return this;
        }

        /**
         *
         * @param relations - relation contexts to delete
         * @return self
         */
        public UpsertRequestContextBuilder addRelationDeletes(Collection<DeleteRelationRequestContext> relations) {
            relations.stream().forEach(this::addRelationDelete);
            return this;
        }


// TODO: @Modules
//        /**
//         * @param cCtx - classifier data record
//         * @return self
//         */
//        public UpsertRequestContextBuilder addClassifierUpsert(UpsertClassifierDataRequestContext cCtx) {
//
//            if (Objects.isNull(this.classifierUpserts)) {
//                this.classifierUpserts = new HashMap<>();
//            }
//
//            if (!this.classifierUpserts.containsKey(cCtx.getClassifierName())) {
//                this.classifierUpserts.put(cCtx.getClassifierName(), new ArrayList<>());
//            }
//
//            this.classifierUpserts.get(cCtx.getClassifierName()).add(cCtx);
//            return this;
//        }
//
//        /**
//         *
//         * @param cCtxts - contexts
//         * @return self
//         */
//        public UpsertRequestContextBuilder addClassifierUpserts(Collection<UpsertClassifierDataRequestContext> cCtxts) {
//            cCtxts.forEach(this::addClassifierUpsert);
//            return this;
//        }
//
//        /**
//         * @param cCtx - classifier data record
//         * @return self
//         */
//        public UpsertRequestContextBuilder addClassifierDelete(DeleteClassifierDataRequestContext cCtx) {
//
//            if (Objects.isNull(this.classifierDeletes)) {
//                this.classifierDeletes = new HashMap<>();
//            }
//
//            if (!this.classifierDeletes.containsKey(cCtx.getClassifierName())) {
//                this.classifierDeletes.put(cCtx.getClassifierName(), new ArrayList<>());
//            }
//
//            this.classifierDeletes.get(cCtx.getClassifierName()).add(cCtx);
//            return this;
//        }
//
//        /**
//         *
//         * @param cCtxts - contexts
//         * @return self
//         */
//        public UpsertRequestContextBuilder addClassifierDeletes(Collection<DeleteClassifierDataRequestContext> cCtxts) {
//            cCtxts.forEach(this::addClassifierDelete);
//            return this;
//        }

        /**
         * Sets code attribute aliases.
         * @param codeAttributeAliases
         * @return
         */
        public UpsertRequestContextBuilder codeAttributeAliases(Collection<CodeAttributeAlias> codeAttributeAliases) {
            this.codeAttributeAliases = codeAttributeAliases;
            return this;
        }
        /**
         *
         * @param enrichByPreviousVersion - enrich By Previous Version
         * @return self
         */
        public UpsertRequestContextBuilder mergeWithPrevVersion(boolean enrichByPreviousVersion) {
            this.mergeWithPreviousVersion = enrichByPreviousVersion;
            return this;
        }
       /**
        *
        * @param skipIndexDrop - skip index drop or not
        * @return self
        */
       public UpsertRequestContextBuilder skipIndexDrop(boolean skipIndexDrop) {
           this.skipIndexDrop = skipIndexDrop;
           return this;
       }
      /**
       *
       * @param auditLevel - sets the audit level for this context
       * @return self
       */
       public UpsertRequestContextBuilder auditLevel(short auditLevel) {
           this.auditLevel = auditLevel;
           return this;
       }
        /**
         * @param skipMatching skip matching main phase or not
         */
        public UpsertRequestContextBuilder skipMatching(boolean skipMatching) {
            this.skipMatching = skipMatching;
            return this;
        }

        /**
         * @param suppressWorkflow force suppress workflow or not
         */
        public UpsertRequestContextBuilder suppressWorkflow(boolean suppressWorkflow) {
            this.suppressWorkflow = suppressWorkflow;
            return this;
        }

        /**
         * @param applyDraft is apply draft context or not
         */
        public UpsertRequestContextBuilder applyDraft(boolean applyDraft) {
            this.isApplyDraft = applyDraft;
            return this;
        }

        /**
         * @return  get valid from
         */
        public Date getValidFrom() {
            return validFrom;
        }

        /**
         * @return  get valid to
         */
        public Date getValidTo() {
            return validTo;
        }

        /**
         * @return  get record
         */
        public DataRecord getRecord() {
            return record;
        }

       /**
        * Builds a context.
        * @return a new context
        */
        @Override
       public UpsertRequestContext build() {
            return new UpsertRequestContext(this);
        }
    }
}
