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

package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.CodeAttributeAlias;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 *
 */
public class UpsertRequestContext
    extends CommonDependableContext
    implements ExternalIdResettingContext, MutableValidityRangeContext, ApprovalStateSettingContext, UserExitExecutableContext {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = 6651928422821780602L;
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
    private String externalId;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Origin name.
     */
    private final String sourceSystem;
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
     * Data quality errors.
     */
    private final List<DataQualityError> dqErrors = new ArrayList<>();

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
     * Classifier data records.
     */
    private final UpsertClassifiersDataRequestContext classifierUpserts;
    /**
     * Delete classifier pack.
     */
    private final DeleteClassifiersDataRequestContext classifierDeletes;
    /**
     * Cpde attribute aliases.
     */
    private final Collection<CodeAttributeAlias> codeAttributeAliases;
    /**
     * Audit level.
     */
    private final short auditLevel;
    /**
     * Constructor.
     */
    private UpsertRequestContext(UpsertRequestContextBuilder b) {
        super(b.parentContext);
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.sourceSystem = b.sourceSystem;
        this.entityName = b.entityName;
        this.externalId = b.externalId;
        this.record = b.record;
        this.lastUpdate = b.lastUpdate;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.originStatus = b.originStatus;
        this.approvalState = b.approvalState;
        this.codeAttributeAliases = b.codeAttributeAliases;
        this.auditLevel = b.auditLevel;

        // Flags
        flags.set(ContextUtils.CTX_FLAG_IS_ENRICHMENT, b.enrichment);
        flags.set(ContextUtils.CTX_FLAG_SKIP_DQ, b.skipCleanse);
        flags.set(ContextUtils.CTX_FLAG_BYPASS_EXTENSION_POINTS, b.bypassExtensionPoints);
        flags.set(ContextUtils.CTX_FLAG_RECALCULATE_WHOLE_TIMELINE, b.recalculateWholeTimeline);
        flags.set(ContextUtils.CTX_FLAG_RETURN_ETALON, b.returnEtalon);
        flags.set(ContextUtils.CTX_FLAG_RETURN_INDEX_CONTEXT, b.returnIndexContext);
        flags.set(ContextUtils.CTX_FLAG_IS_RESTORE, b.isRestore);
        flags.set(ContextUtils.CTX_FLAG_IS_PERIOD_RESTORE, b.isPeriodRestore);
        flags.set(ContextUtils.CTX_FLAG_INCLUDE_DRAFT_VERSIONS, b.includeDraftVersions);
        flags.set(ContextUtils.CTX_FLAG_MERGE_WITH_PREVIOUS_VERSION, b.mergeWithPreviousVersion);
        flags.set(ContextUtils.CTX_FLAG_SKIP_INDEX_DROP, b.skipIndexDrop);
        flags.set(ContextUtils.CTX_FLAG_SKIP_MATCHING_PREPROCESSING, b.skipMatchingPreprocessing);
        flags.set(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT, b.suppressAudit);
        flags.set(ContextUtils.CTX_FLAG_BATCH_UPSERT, b.batchUpsert);
        flags.set(ContextUtils.CTX_FLAG_INITIAL_LOAD, b.initialLoad);
        flags.set(ContextUtils.CTX_FLAG_SKIP_CONSISTENCY_CHECKS, b.skipConsistencyChecks);
        flags.set(ContextUtils.CTX_FLAG_SKIP_MATCHING, b.skipMatching);
        flags.set(ContextUtils.CTX_FLAG_RESOLVE_BY_MATCHING, b.resolveByMatching);

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

        this.classifierUpserts = b.classifierUpserts == null || b.classifierUpserts.isEmpty()
                ? null
                : UpsertClassifiersDataRequestContext.builder()
                        .classifiers(b.classifierUpserts)
                        .etalonKey(getEtalonKey())
                        .originKey(getOriginKey())
                        .externalId(getExternalId())
                        .sourceSystem(getSourceSystem())
                        .entityName(getEntityName())
                        .build();
        this.classifierDeletes = b.classifierDeletes == null || b.classifierDeletes.isEmpty()
                ? null
                : DeleteClassifiersDataRequestContext.builder()
                        .classifiers(b.classifierDeletes)
                        .etalonKey(getEtalonKey())
                        .originKey(getOriginKey())
                        .externalId(getExternalId())
                        .sourceSystem(getSourceSystem())
                        .entityName(getEntityName())
                        .build();
    }

    @Override
    public void setOperationId(String operationId) {

        super.setOperationId(operationId);
        if (Objects.nonNull(this.relations)) {
            this.relations.setOperationId(operationId);
        }

        if (Objects.nonNull(this.classifierUpserts)) {
            this.classifierUpserts.setOperationId(operationId);
        }

        if (Objects.nonNull(this.classifierDeletes)) {
            this.classifierDeletes.setOperationId(operationId);
        }
    }

    @Override
    public void setExternalId(String externalId) {
        this.externalId = externalId;
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
     * @return the skipCleanse
     */
    public boolean isSkipCleanse() {
        return flags.get(ContextUtils.CTX_FLAG_SKIP_DQ);
    }

    /**
     * @return the skipConsistencyChecks
     */
    public boolean isSkipConsistencyChecks() {
        return flags.get(ContextUtils.CTX_FLAG_SKIP_CONSISTENCY_CHECKS);
    }

    /**
     * @return the skipIndexDrop
     */
    public boolean isSkipIndexDrop() {
        return flags.get(ContextUtils.CTX_FLAG_SKIP_INDEX_DROP);
    }

    /**
     * @return the skipMatching preprocessing
     */
    public boolean isSkipMatchingPreprocessing() {
        return flags.get(ContextUtils.CTX_FLAG_SKIP_MATCHING_PREPROCESSING);
    }

    /**
     * @return the skipMatching
     */
    public boolean isSkipMatching() {
        return flags.get(ContextUtils.CTX_FLAG_SKIP_MATCHING);
    }

    /**
     * @return suppressAudit
     */
    public boolean isSuppressAudit() {
        return flags.get(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT);
    }
    /**
     * @return the bypassExtensionPoints
     */
    @Override
    public boolean isBypassExtensionPoints() {
        return flags.get(ContextUtils.CTX_FLAG_BYPASS_EXTENSION_POINTS);
    }

    /**
     * @return the returnEtalon
     */
    public boolean isReturnEtalon() {
        return flags.get(ContextUtils.CTX_FLAG_RETURN_ETALON);
    }

    /**
     * @return the returnIndexContext
     */
    public boolean isReturnIndexContext() {
        return flags.get(ContextUtils.CTX_FLAG_RETURN_INDEX_CONTEXT);
    }

    /**
     * @return the recalculateWholeTimeline
     */
    public boolean isRecalculateWholeTimeline() {
        return flags.get(ContextUtils.CTX_FLAG_RECALCULATE_WHOLE_TIMELINE);
    }

    /**
     * @return define that is restore request.
     */
    public boolean isRestore() {
        return flags.get(ContextUtils.CTX_FLAG_IS_RESTORE);
    }

    /**
     * @return define that is period restore request.
     */
    public boolean isPeriodRestore() {
        return flags.get(ContextUtils.CTX_FLAG_IS_PERIOD_RESTORE);
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
     * @return true, if need resolve keys by matching or not
     */
    public boolean isResolveByMatching() {
        return flags.get(ContextUtils.CTX_FLAG_RESOLVE_BY_MATCHING);
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
     * Merge with previous version?
     * @return true if so, false otherwise
     */
    public boolean isMergeWithPreviousVersion() {
        return flags.get(ContextUtils.CTX_FLAG_MERGE_WITH_PREVIOUS_VERSION);
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
    /**
     * Gets classifier data upsert records.
     * @return the classifiers
     */
    public UpsertClassifiersDataRequestContext getClassifierUpserts() {
        return classifierUpserts;
    }

    /**
     * @return the classifierDeletes
     */
    public DeleteClassifiersDataRequestContext getClassifierDeletes() {
        return classifierDeletes;
    }

    /**
     * @return the codeAttributeAliases
     */
    public Collection<CodeAttributeAlias> getCodeAttributeAliases() {
        return codeAttributeAliases == null ? Collections.emptyList() : codeAttributeAliases;
    }

    /**
     * @return the sourceSystem
     */
    @Override
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @return the entityName
     */
    @Override
    public String getEntityName() {
        return entityName;
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
    public boolean isEtalonRecordKey() {
        return !flags.get(ContextUtils.CTX_FLAG_IS_ENRICHMENT) && ExternalIdResettingContext.super.isEtalonRecordKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOriginRecordKey() {
        return !flags.get(ContextUtils.CTX_FLAG_IS_ENRICHMENT) && ExternalIdResettingContext.super.isOriginRecordKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOriginExternalId() {
        return !flags.get(ContextUtils.CTX_FLAG_IS_ENRICHMENT) && ExternalIdResettingContext.super.isOriginExternalId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnrichmentKey() {
        return flags.get(ContextUtils.CTX_FLAG_IS_ENRICHMENT)
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
     * @return the dqErrors
     */
    public List<DataQualityError> getDqErrors() {
        return dqErrors;
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
     * @return the includeDraftVersions
     */
    public boolean isIncludeDraftVersions() {
        return flags.get(ContextUtils.CTX_FLAG_INCLUDE_DRAFT_VERSIONS);
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
        return getFromStorage(StorageId.DATA_UPSERT_KEYS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.DATA_UPSERT_KEYS;
    }

    /**
     * Builder shorthand.
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

        UpsertRequestContextBuilder b = new UpsertRequestContextBuilder();

        b.record = other.record;
        b.etalonKey = other.etalonKey;
        b.originKey = other.originKey;
        b.sourceSystem = other.sourceSystem;
        b.entityName = other.entityName;
        b.externalId = other.externalId;
        b.lastUpdate = other.lastUpdate;
        b.validFrom = other.validFrom;
        b.validTo = other.validTo;
        b.originStatus = other.originStatus;
        b.approvalState = other.approvalState;
        b.codeAttributeAliases = other.codeAttributeAliases;
        b.auditLevel = other.auditLevel;

        // Flags
        b.enrichment = other.flags.get(ContextUtils.CTX_FLAG_IS_ENRICHMENT);
        b.skipCleanse = other.flags.get(ContextUtils.CTX_FLAG_SKIP_DQ);
        b.bypassExtensionPoints = other.flags.get(ContextUtils.CTX_FLAG_BYPASS_EXTENSION_POINTS);
        b.recalculateWholeTimeline = other.flags.get(ContextUtils.CTX_FLAG_RECALCULATE_WHOLE_TIMELINE);
        b.returnEtalon = other.flags.get(ContextUtils.CTX_FLAG_RETURN_ETALON);
        b.returnIndexContext = other.flags.get(ContextUtils.CTX_FLAG_RETURN_INDEX_CONTEXT);
        b.isRestore = other.flags.get(ContextUtils.CTX_FLAG_IS_RESTORE);
        b.includeDraftVersions = other.flags.get(ContextUtils.CTX_FLAG_INCLUDE_DRAFT_VERSIONS);
        b.mergeWithPreviousVersion = other.flags.get(ContextUtils.CTX_FLAG_MERGE_WITH_PREVIOUS_VERSION);
        b.skipIndexDrop = other.flags.get(ContextUtils.CTX_FLAG_SKIP_INDEX_DROP);
        b.skipMatchingPreprocessing = other.flags.get(ContextUtils.CTX_FLAG_SKIP_MATCHING_PREPROCESSING);
        b.suppressAudit = other.flags.get(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT);
        b.batchUpsert = other.flags.get(ContextUtils.CTX_FLAG_BATCH_UPSERT);
        b.initialLoad = other.flags.get(ContextUtils.CTX_FLAG_INITIAL_LOAD);
        b.skipMatching = other.flags.get(ContextUtils.CTX_FLAG_SKIP_MATCHING);
        b.resolveByMatching = other.flags.get(ContextUtils.CTX_FLAG_RESOLVE_BY_MATCHING);

        // Sub contexts
        b.relations = other.relations != null ? other.relations.getRelations() : null;
        b.relationDeletes = other.relationDeletes != null ? other.relationDeletes.getRelations() : null;
        b.classifierDeletes = other.classifierDeletes != null ? other.classifierDeletes.getClassifiers() : null;
        b.classifierUpserts = other.classifierUpserts != null ? other.classifierUpserts.getClassifiers() : null;

        return b;
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class UpsertRequestContextBuilder {
        /**
         * Golden record.
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
         * Source system name.
         */
        private String sourceSystem;
        /**
         * Return etalon record or not (if not, etalon ranges will be calculated asynchronously).
         */
        private boolean returnEtalon;
        /**
         * Return index context instead of indexing in-place.
         */
        private boolean returnIndexContext;
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
        private boolean batchUpsert;
        /**
         * This context is participating in initial load process. Skips relation key resolution.
         */
        private boolean initialLoad;
        /**
         * Relations
         */
        private Map<String, List<UpsertRelationRequestContext>> relations;
        /**
         * Relation deletes
         */
        private Map<String, List<DeleteRelationRequestContext>> relationDeletes;
        /**
         * Classifier data records.
         */
        private Map<String, List<UpsertClassifierDataRequestContext>> classifierUpserts;
        /**
         * Classifier deletes.
         */
        private Map<String, List<DeleteClassifierDataRequestContext>> classifierDeletes;
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
         * Try resolve upserted record by matching keys
         */
        private boolean resolveByMatching;

        private CommonDependableContext parentContext;

        /**
         * Constructor.
         */
        public UpsertRequestContextBuilder() {
            super();
        }

        /**
         * @param etalonKey the etalonKey to set
         */
        public UpsertRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the etalonKey to set
         */
        public UpsertRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public UpsertRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public UpsertRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public UpsertRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public UpsertRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the goldenKey to set
         */
        public UpsertRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
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
         * Sppress audt events emission during upsert.
         * @param suppressAudit flag
         * @return self
         */
        public UpsertRequestContextBuilder suppressAudit(boolean suppressAudit) {
            this.suppressAudit = suppressAudit;
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
         * @param recalculateWholeTimeline the flag
         * @return self
         */
        public UpsertRequestContextBuilder recalculateWholeTimeline(boolean recalculateWholeTimeline) {
            this.recalculateWholeTimeline = recalculateWholeTimeline;
            return this;
        }

        /**
         * @param batchUpsert the flag
         * @return self
         */
        public UpsertRequestContextBuilder batchUpsert(boolean batchUpsert) {
            this.batchUpsert = batchUpsert;
            return this;
        }

        /**
         * @param initialLoad the flag
         * @return self
         */
        public UpsertRequestContextBuilder initialLoad(boolean initialLoad) {
            this.initialLoad = initialLoad;
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
         * @param returnEtalon return etalon or not
         * @return self
         */
        public UpsertRequestContextBuilder returnEtalon(boolean returnEtalon) {
            this.returnEtalon = returnEtalon;
            return this;
        }
        /**
         * @param returnIndexContext return index context or index in-place.
         * @return self
         */
        public UpsertRequestContextBuilder returnIndexContext(boolean returnIndexContext) {
            this.returnIndexContext = returnIndexContext;
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
         * @param restore
         * @return self
         */
        public UpsertRequestContextBuilder restore(boolean restore) {
            isRestore = restore;
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



        /**
         * @param cCtx - classifier data record
         * @return self
         */
        public UpsertRequestContextBuilder addClassifierUpsert(UpsertClassifierDataRequestContext cCtx) {

            if (Objects.isNull(this.classifierUpserts)) {
                this.classifierUpserts = new HashMap<>();
            }

            if (!this.classifierUpserts.containsKey(cCtx.getClassifierName())) {
                this.classifierUpserts.put(cCtx.getClassifierName(), new ArrayList<>());
            }

            this.classifierUpserts.get(cCtx.getClassifierName()).add(cCtx);
            return this;
        }

        /**
         *
         * @param cCtxts - contexts
         * @return self
         */
        public UpsertRequestContextBuilder addClassifierUpserts(Collection<UpsertClassifierDataRequestContext> cCtxts) {
            cCtxts.forEach(this::addClassifierUpsert);
            return this;
        }

        /**
         * @param cCtx - classifier data record
         * @return self
         */
        public UpsertRequestContextBuilder addClassifierDelete(DeleteClassifierDataRequestContext cCtx) {

            if (Objects.isNull(this.classifierDeletes)) {
                this.classifierDeletes = new HashMap<>();
            }

            if (!this.classifierDeletes.containsKey(cCtx.getClassifierName())) {
                this.classifierDeletes.put(cCtx.getClassifierName(), new ArrayList<>());
            }

            this.classifierDeletes.get(cCtx.getClassifierName()).add(cCtx);
            return this;
        }

        /**
         *
         * @param cCtxts - contexts
         * @return self
         */
        public UpsertRequestContextBuilder addClassifierDeletes(Collection<DeleteClassifierDataRequestContext> cCtxts) {
            cCtxts.forEach(this::addClassifierDelete);
            return this;
        }

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
         * @param  resolveByMatching try resolve upserted record by matching rules or not
         */
        public UpsertRequestContextBuilder resolveByMatching(boolean resolveByMatching) {
            this.resolveByMatching = resolveByMatching;
            return this;
        }

        public UpsertRequestContextBuilder parentContext(final CommonDependableContext parentContext) {
            this.parentContext = parentContext;
            return this;
        }

       /**
        * Builds a context.
        * @return a new context
        */
       public UpsertRequestContext build() {
            return new UpsertRequestContext(this);
        }
    }
}
