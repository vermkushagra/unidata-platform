package com.unidata.mdm.backend.common.context;

import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.ReferenceAliasKey;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * @author Mikhail Mikhailov
 * Single relation upsert request context.
 */
public class UpsertRelationRequestContext
    extends AbstractRelationToRequestContext
    implements MutableValidityRangeContext, ApprovalStateSettingContext, UserExitExecutableContext {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -2042264082583817129L;
    /**
     * Relation etalon key.
     */
    private final String relationEtalonKey;
    /**
     * Relation Origin key.
     */
    private final String relationOriginKey;
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
     * List with data quality errors.
     */
    private List<DataQualityError> dqErrors;
    /**
     * Audit level.
     */
    private final short auditLevel;
    /**
     * Constructor.
     */
    private UpsertRelationRequestContext(UpsertRelationRequestContextBuilder b) {
        super(b.parentContext);
        this.relationEtalonKey = b.relationEtalonKey;
        this.relationOriginKey = b.relationOriginKey;
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.sourceSystem = b.sourceSystem;
        this.entityName = b.entityName;
        this.externalId = b.externalId;
        this.relationName = b.relationName;
        this.relation = b.relation;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.referenceAliasKey = b.referenceAliasKey;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;

        flags.set(ContextUtils.CTX_FLAG_INCLUDE_DRAFT_VERSIONS, b.includeDraftVersions);
        flags.set(ContextUtils.CTX_FLAG_BATCH_UPSERT, b.batchUpsert);
        flags.set(ContextUtils.CTX_FLAG_INITIAL_LOAD, b.initialLoad);
        flags.set(ContextUtils.CTX_FLAG_BYPASS_EXTENSION_POINTS, b.bypassExtensionPoints);
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
     * @return the relationEtalonKey
     */
    @Override
    public String getRelationEtalonKey() {
        return relationEtalonKey;
    }

    /**
     * @return the relationOriginKey
     */
    @Override
    public String getRelationOriginKey() {
        return relationOriginKey;
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
        return flags.get(ContextUtils.CTX_FLAG_INCLUDE_DRAFT_VERSIONS);
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
     * @return the bypassExtensionPoints
     */
    @Override
    public boolean isBypassExtensionPoints() {
        return flags.get(ContextUtils.CTX_FLAG_BYPASS_EXTENSION_POINTS);
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
     * {@inheritDoc}
     */
    @Override
    public RelationKeys relationKeys() {
        return getFromStorage(relationKeysId());
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
    public static class UpsertRelationRequestContextBuilder {
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
         * Relation etalon id.
         */
        private String relationEtalonKey;
        /**
         * Relation origin id.
         */
        private String relationOriginKey;
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
        private boolean batchUpsert;
        /**
         * This context is participating in initial load process. Skips relation key resolution.
         */
        private boolean initialLoad;
        /**
         * Bypass extension points during upsert relation.
         */
        private boolean bypassExtensionPoints;
        /**
         * Audit level.
         */
        private short auditLevel = AuditLevel.AUDIT_SUCCESS;

        private CommonDependableContext parentContext;

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
         * @param relationEtalonKey the relationEtalonKey to set
         */
        public UpsertRelationRequestContextBuilder relationEtalonKey(String relationEtalonKey) {
            this.relationEtalonKey = relationEtalonKey;
            return this;
        }

        /**
         * @param relationOriginKey the relationOriginKey to set
         */
        public UpsertRelationRequestContextBuilder relationOriginKey(String relationOriginKey) {
            this.relationOriginKey = relationOriginKey;
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
         * @param etalonKey the etalonKey to set
         */
        public UpsertRelationRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public UpsertRelationRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public UpsertRelationRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public UpsertRelationRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public UpsertRelationRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public UpsertRelationRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the origin key to set
         */
        public UpsertRelationRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
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
        public UpsertRelationRequestContextBuilder batchUpsert(boolean batchUpsert) {
            this.batchUpsert = batchUpsert;
            return this;
        }

        /**
         * @param initialLoad the flag
         * @return self
         */
        public UpsertRelationRequestContextBuilder initialLoad(boolean initialLoad) {
            this.initialLoad = initialLoad;
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

        public UpsertRelationRequestContextBuilder parentContext(final CommonDependableContext parentContext) {
            this.parentContext = parentContext;
            return this;
        }

        /**
         * Builder method.
         * @return context
         */
        public UpsertRelationRequestContext build() {
            return new UpsertRelationRequestContext(this);
        }
    }

	/**
	 * @return the dqErrors
	 */
	public List<DataQualityError> getDqErrors() {
		return dqErrors;
	}

	/**
	 * @param dqErrors the dqErrors to set
	 */
	public void setDqErrors(List<DataQualityError> dqErrors) {
		this.dqErrors = dqErrors;
	}
}
