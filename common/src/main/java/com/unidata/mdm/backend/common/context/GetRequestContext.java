/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 *
 */
public class GetRequestContext
    extends CommonRequestContext
    implements RecordIdentityContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -6826317454436803507L;
    /**
     * Etalon key.
     */
    private final String etalonKey;
    /**
     * Preview etalon keys
     */
    private final List<String> previewEtalonKeys;
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
     * Global sequence number.
     */
    private final Long gsn;
    /**
     * For a particular date (as of).
     */
    private final Date forDate;
    /**
     * Has updates (new versions) after this date.
     */
    private final Date updatesAfter;
    /**
     * Last update date to cut off versions.
     */
    private final Date forLastUpdate;
    /**
     * Operation id.
     */
    private final String forOperationId;
    /**
     * Constructor.
     */
    private GetRequestContext(GetRequestContextBuilder b) {

        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.gsn = b.gsn;
        this.forDate = b.forDate;
        this.updatesAfter = b.updatesAfter;
        this.forLastUpdate = b.forLastUpdate;
        this.forOperationId = b.forOperationId;
        this.previewEtalonKeys = b.previewEtalons;

        // Flags
        flags.set(ContextUtils.CTX_FLAG_FETCH_ORIGINS, b.fetchOrigins);
        flags.set(ContextUtils.CTX_FLAG_FETCH_ORIGINS_HISTORY, b.fetchOriginsHistory);
        flags.set(ContextUtils.CTX_FLAG_FETCH_SOFT_DELETED, b.fetchSoftDeleted);
        flags.set(ContextUtils.CTX_FLAG_INCLUDE_MERGED, b.includeMerged);
        flags.set(ContextUtils.CTX_FLAG_FETCH_TASKS, b.tasks);
        flags.set(ContextUtils.CTX_FLAG_FETCH_RELATIONS, b.fetchRelations);
        flags.set(ContextUtils.CTX_FLAG_FETCH_CLASSIFIERS, b.fetchClassifiers);
        flags.set(ContextUtils.CTX_FLAG_INCLUDE_INACTIVE, b.includeInactive);
        flags.set(ContextUtils.CTX_FLAG_INCLUDE_DRAFTS, b.includeDrafts);
        flags.set(ContextUtils.CTX_FLAG_INCLUDE_WINNERS, b.includeWinners);
        flags.set(ContextUtils.CTX_FLAG_DIFF_TO_DRAFT, b.diffToDraft);
        flags.set(ContextUtils.CTX_FLAG_DIFF_TO_PREVIOUS, b.diffToPrevious);
        flags.set(ContextUtils.CTX_FLAG_STRICT_DRAFT, b.strictDraft);
        flags.set(ContextUtils.CTX_FLAG_FETCH_CLUSTERS, b.fetchClusters);
        flags.set(ContextUtils.CTX_FLAG_FETCH_LARGE_OBJECTS, b.fetchLargeObjects);
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
     * @return the gsn
     */
    @Override
    public Long getGsn() {
        return gsn;
    }

    /**
     * @return the forDate
     */
    public Date getForDate() {
        return forDate;
    }

    /**
     * @return the updatesAfter
     */
    public Date getUpdatesAfter() {
        return updatesAfter;
    }

    /**
     * @return the lastUpdate
     */
    public Date getForLastUpdate() {
		return forLastUpdate;
	}

	/**
     * @return the forOperationId
     */
    public String getForOperationId() {
        return forOperationId;
    }

    /**
     * @return the fetchOrigins
     */
    public boolean isFetchOrigins() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_ORIGINS);
    }

    /**
     * @return the fetchOriginsHistory
     */
    public boolean isFetchOriginsHistory() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_ORIGINS_HISTORY);
    }

    /**
     * @return the fetchSoftDeleted
     */
    public boolean isFetchSoftDeleted() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_SOFT_DELETED);
    }

    /**
     * @return the includeMerged
     */
    public boolean isIncludeMerged() {
        return flags.get(ContextUtils.CTX_FLAG_INCLUDE_MERGED);
    }

    /**
     * @return the tasks
     */
    public boolean isTasks() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_TASKS);
    }

    /**
     * @return the fetchRelations
     */
    public boolean isFetchRelations() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_RELATIONS);
    }

    /**
     * @return the fetchClassifiers
     */
    public boolean isFetchClassifiers() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_CLASSIFIERS);
    }

    /**
     * @return the includeInactive
     */
    public boolean isIncludeInactive() {
        return flags.get(ContextUtils.CTX_FLAG_INCLUDE_INACTIVE);
    }
    /**
     * @return the unpublishedState
     */
    public boolean isIncludeDrafts() {
        return flags.get(ContextUtils.CTX_FLAG_INCLUDE_DRAFTS);
    }
    /**
     * @return the strictDraft
     */
    public boolean isIncludeWinners() {
        return flags.get(ContextUtils.CTX_FLAG_INCLUDE_WINNERS);
    }
    /**
     * @return the diffToDraft
     */
    public boolean isDiffToDraft() {
        return flags.get(ContextUtils.CTX_FLAG_DIFF_TO_DRAFT);
    }
    /**
     * @return the diffToPervious
     */
    public boolean isDiffToPervious() {
        return flags.get(ContextUtils.CTX_FLAG_DIFF_TO_PREVIOUS);
    }
    /**
     * @return the isStrictDraft
     */
    public boolean isStrictDraft() {
        return flags.get(ContextUtils.CTX_FLAG_STRICT_DRAFT);
    }
    /**
     * @return the fetchClusters
     */
    public boolean isFetchClusters() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_CLUSTERS);
    }
    /**
     * @return the fetchLargeObjects
     */
    public boolean isFetchLargeObjects() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_LARGE_OBJECTS);
    }

    /**
     * @return collection of etalons for
     */
    public List<String> getPreviewEtalonKeys() {
        return previewEtalonKeys;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(StorageId.DATA_GET_KEYS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.DATA_GET_KEYS;
    }

    /**
     * Builder shorthand.
     * @return builder
     */
    public static GetRequestContextBuilder builder() {
        return new GetRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetRequestContextBuilder {
        /**
         * Etalon ids for merge preview
         */
        private List<String> previewEtalons = Collections.emptyList();
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
         * Global sequence number.
         */
        private Long gsn;
        /**
         * For a particular date (as of).
         */
        private Date forDate;
        /**
         * Has updates (new versions) after this date.
         */
        private Date updatesAfter;
        /**
         * Last update date to cut off versions.
         */
        private Date forLastUpdate;
        /**
         * Operation id.
         */
        private String forOperationId;
        /**
         * Return fetchOrigins or not.
         */
        private boolean fetchOrigins;
        /**
         * Return fetchOrigins history or not.
         */
        private boolean fetchOriginsHistory;
        /**
         * Return soft deleted or not.
         */
        private boolean fetchSoftDeleted;
        /**
         * Return includeMerged or not.
         */
        private boolean includeMerged;
        /**
         * Include workflow tasks state.
         */
        private boolean tasks;
        /**
         * Return relations or not. False for UI, true for SOAP.
         */
        private boolean fetchRelations;
        /**
         * Return classifiers or not. True for both SOAP and UI.
         */
        private boolean fetchClassifiers = true;
        /**
         * Include inactive versions in calculation.
         */
        private boolean includeInactive;
        /**
         * View unpublished state or not.
         */
        private boolean includeDrafts;
        /**
         * Include information about winners
         */
        private boolean includeWinners;
        /**
         * Calculate and return diff to draft (pending) state, if the record is in pending state.
         */
        private boolean diffToDraft;
        /**
         * Calculate and return diff to previous etalon state (one version ago).
         */
        private boolean diffToPrevious;
        /**
         * Use strictDrafts value without check by author record name
         */
        private boolean strictDraft;
        /**
         * Return clusters or not.
         */
        private boolean fetchClusters;
        /**
         * Return large objects data immediately or not.
         */
        private boolean fetchLargeObjects;
        /**
         * Constructor.
         */
        public GetRequestContextBuilder() {
            super();
        }

        /**
         * @param etalonKey the etalonKey to set
         */
        public GetRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param previewEtalons collection of keys for merge preview
         * @return self
         */
        public GetRequestContextBuilder previewKeys(List<String> previewEtalons) {
            this.previewEtalons = previewEtalons;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public GetRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public GetRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public GetRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public GetRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param gsn the gsn to set
         */
        public GetRequestContextBuilder gsn(Long gsn) {
            this.gsn = gsn;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public GetRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * @param forDate the forDate to set
         */
        public GetRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }

        /**
         * @param updatesAfter the updatesAfter to set
         */
        public GetRequestContextBuilder updatesAfter(Date updatesAfter) {
            this.updatesAfter = updatesAfter;
            return this;
        }

        /**
         * @param forOperationId the forOperationId to set
         */
        public GetRequestContextBuilder forOperationId(String forOperationId) {
            this.forOperationId = forOperationId;
            return this;
        }

        /**
         * Sets last update date to the context.
         * @param lastUpdate the date
         * @return self
         */
        public GetRequestContextBuilder forLastUpdate(Date lastUpdate) {
        	this.forLastUpdate = lastUpdate;
        	return this;
        }

        /**
         * @param fetchOrigins the fetchOrigins to set
         */
        public GetRequestContextBuilder fetchOrigins(boolean fetchOrigins) {
            this.fetchOrigins = fetchOrigins;
            return this;
        }

        /**
         * @param fetchOriginsHistory the fetchOriginsHistory to set
         */
        public GetRequestContextBuilder fetchOriginsHistory(boolean fetchOriginsHistory) {
            this.fetchOriginsHistory = fetchOriginsHistory;
            return this;
        }

        /**
         * @param fetchSoftDeleted the fetchSoftDeleted to set
         */
        public GetRequestContextBuilder fetchSoftDeleted(boolean fetchSoftDeleted) {
            this.fetchSoftDeleted = fetchSoftDeleted;
            return this;
        }
        /**
         * @param includeMerged the includeMerged to set
         */
        public GetRequestContextBuilder includeMerged(boolean includeMerged) {
            this.includeMerged = includeMerged;
            return this;
        }
        /**
         * @param tasks the tasks to set
         */
        public GetRequestContextBuilder tasks(boolean tasks) {
            this.tasks = tasks;
            return this;
        }
        /**
         * @param fetchRelations the fetchRelations to set
         */
        public GetRequestContextBuilder fetchRelations(boolean fetchRelations) {
            this.fetchRelations = fetchRelations;
            return this;
        }
        /**
         * @param fetchClassifiers the fetchClassifiers to set
         */
        public GetRequestContextBuilder fetchClassifiers(boolean fetchClassifiers) {
            this.fetchClassifiers = fetchClassifiers;
            return this;
        }
        /**
         * Instructs etalon calculator to include inactive versions into calculation.
         * @param includeInactive the includeInactive to set
         * @return self
         */
        public GetRequestContextBuilder includeInactive(boolean includeInactive) {
            this.includeInactive = includeInactive;
            return this;
        }
        /**
         * Request unpublished state of a record or not.
         * @param includeDrafts requested state
         * @return self
         */
        public GetRequestContextBuilder includeDrafts(boolean includeDrafts) {
            this.includeDrafts = includeDrafts;
            return this;
        }
        /**
         * Request information about winners
         * @param includeWinners requested state
         * @return self
         */
        public GetRequestContextBuilder includeWinners(boolean includeWinners) {
            this.includeWinners = includeWinners;
            return this;
        }
        /**
         * Calculate and return diff to draft (pending) state, if the record is in pending state.
         * @param diffToDraft request state
         * @return self
         */
        public GetRequestContextBuilder diffToDraft(boolean diffToDraft) {
            this.diffToDraft = diffToDraft;
            return this;
        }
        /**
         * Calculate and return diff to previous etalon state (one version ago).
         * @param diffToPrevious request state
         * @return self
         */
        public GetRequestContextBuilder diffToPrevious(boolean diffToPrevious) {
            this.diffToPrevious = diffToPrevious;
            return this;
        }
        /**
         * Use strictDrafts value without check by author record name
         * @param strictDraft requested state
         * @return self
         */
        public GetRequestContextBuilder strictDraft(boolean strictDraft) {
            this.strictDraft = strictDraft;
            return this;
        }
        /**
         * @param fetchClusters the fetchClusters to set
         */
        public GetRequestContextBuilder fetchClusters(boolean fetchClusters) {
            this.fetchClusters = fetchClusters;
            return this;
        }
        /**
         * @param fetchLargeObjects the fetchLargeObjects to set
         */
        public GetRequestContextBuilder fetchLargeObjects(boolean fetchLargeObjects) {
            this.fetchLargeObjects = fetchLargeObjects;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        public GetRequestContext build() {
            return new GetRequestContext(this);
        }
    }
}
