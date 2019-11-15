/**
 *
 */
package org.unidata.mdm.data.context;

import java.util.Date;

import org.unidata.mdm.data.type.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Fetch interval for a record.
 */
public class GetRecordIntervalRequestContext
    extends AbstractRecordIdentityContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -6826317454436803507L;
    /**
     * For a particular date (as of).
     */
    private final Date forDate;
    /**
     * Has updates (new versions) after this date.
     */
    private final Date forUpdatesAfter;
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
    private GetRecordIntervalRequestContext(GetRecordIntervalRequestContextBuilder b) {
        super(b);
        this.forDate = b.forDate;
        this.forUpdatesAfter = b.forUpdatesAfter;
        this.forLastUpdate = b.forLastUpdate;
        this.forOperationId = b.forOperationId;

        // Flags
        flags.set(DataContextFlags.FLAG_FETCH_KEYS, b.fetchKeys);
        flags.set(DataContextFlags.FLAG_FETCH_TIMELINE_DATA, b.fetchData);
        flags.set(DataContextFlags.FLAG_SKIP_TIMELINE_CALCULATIONS, b.skipCalculations);
        flags.set(DataContextFlags.FLAG_INCLUDE_INACTIVE, b.includeInactive);
        flags.set(DataContextFlags.FLAG_INCLUDE_DRAFTS, b.includeDrafts);
        flags.set(DataContextFlags.FLAG_INCLUDE_WINNERS, b.includeWinners);
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
    public Date getForUpdatesAfter() {
        return forUpdatesAfter;
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
     * @return the tasks
     */
    public boolean isFetchKeys() {
        return flags.get(DataContextFlags.FLAG_FETCH_KEYS);
    }
    /**
     * @return the includeInactive
     */
    public boolean isIncludeInactive() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_INACTIVE);
    }
    /**
     * @return the unpublishedState
     */
    public boolean isIncludeDrafts() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_DRAFTS);
    }
    /**
     * @return the strictDraft
     */
    public boolean isIncludeWinners() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_WINNERS);
    }
    /**
     * @return the fetchTimelineData
     */
    public boolean isFetchData() {
        return flags.get(DataContextFlags.FLAG_FETCH_TIMELINE_DATA);
    }
    /**
     * @return the skipCalculations
     */
    public boolean isSkipCalculations() {
        return flags.get(DataContextFlags.FLAG_SKIP_TIMELINE_CALCULATIONS);
    }
    /**
     * Builder shorthand.
     * @return builder
     */
    public static GetRecordIntervalRequestContextBuilder builder() {
        return new GetRecordIntervalRequestContextBuilder();
    }
    /**
     * Builder shorthand.
     * @return builder
     */
    public static GetRecordIntervalRequestContextBuilder builder(AbstractRecordIdentityContext other) {

        RecordKeys keys = other.keys();
        GetRecordIntervalRequestContextBuilder b = new GetRecordIntervalRequestContextBuilder();
        b.etalonKey = keys != null && keys.getEtalonKey() != null ? keys.getEtalonKey().getId() : other.getEtalonKey();
        b.originKey = keys != null && keys.getOriginKey() != null ? keys.getOriginKey().getId() : other.getOriginKey();
        b.externalId = keys != null && keys.getOriginKey() != null ? keys.getOriginKey().getExternalId() : other.getExternalId();
        b.entityName = keys != null ? keys.getEntityName() : other.getEntityName();
        b.sourceSystem = keys != null && keys.getOriginKey() != null ? keys.getOriginKey().getSourceSystem() : other.getSourceSystem();
        b.lsn = keys != null && keys.getEtalonKey() != null ? (Long) keys.getEtalonKey().getLsn() : (Long) other.getLsn();
        b.shard = keys != null ? (Integer) keys.getShard() : (Integer) other.getShard();

        if (other instanceof GetRequestContext) {
            GetRequestContext gOther = (GetRequestContext) other;
            b.fetchData = gOther.isFetchTimelineData();
            b.forDate = gOther.getForDate();
            b.forLastUpdate = gOther.getForLastUpdate();
            b.forUpdatesAfter = gOther.getUpdatesAfter();
            b.forOperationId = gOther.getForOperationId();
            b.includeDrafts = gOther.isIncludeDrafts();
            b.includeInactive = gOther.isIncludeInactive();
            b.includeWinners = gOther.isIncludeWinners();
        } else if (other instanceof UpsertRequestContext) {
            UpsertRequestContext gOther = (UpsertRequestContext) other;
            b.fetchData = true;
            b.includeDrafts = gOther.isIncludeDraftVersions();
        }

        return b;
    }
    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetRecordIntervalRequestContextBuilder
        extends AbstractRecordIdentityContextBuilder<GetRecordIntervalRequestContextBuilder> {
        /**
         * For a particular date (as of).
         */
        private Date forDate;
        /**
         * Has updates (new versions) after this date.
         */
        private Date forUpdatesAfter;
        /**
         * Last update date to cut off versions.
         */
        private Date forLastUpdate;
        /**
         * Operation id.
         */
        private String forOperationId;
        /**
         * View unpublished state or not.
         */
        private boolean includeDrafts;
        /**
         * Include inactive versions in calculation.
         */
        private boolean includeInactive;
        /**
         * Include information about winners
         */
        private boolean includeWinners;
        /**
         * Return keys.
         */
        private boolean fetchKeys = true;
        /**
         * Return timeline data.
         */
        private boolean fetchData;
        /**
         * Skip etalon, activity, operation type calculations.
         * Return raw timeline.
         */
        private boolean skipCalculations;
        /**
         * Constructor.
         */
        private GetRecordIntervalRequestContextBuilder() {
            super();
        }
        /**
         * @param forDate the forDate to set
         */
        public GetRecordIntervalRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }
        /**
         * @param updatesAfter the updatesAfter to set
         */
        public GetRecordIntervalRequestContextBuilder updatesAfter(Date updatesAfter) {
            this.forUpdatesAfter = updatesAfter;
            return this;
        }

        /**
         * @param forOperationId the forOperationId to set
         */
        public GetRecordIntervalRequestContextBuilder forOperationId(String forOperationId) {
            this.forOperationId = forOperationId;
            return this;
        }

        /**
         * Sets last update date to the context.
         * @param lastUpdate the date
         * @return self
         */
        public GetRecordIntervalRequestContextBuilder forLastUpdate(Date lastUpdate) {
            this.forLastUpdate = lastUpdate;
            return this;
        }

        /**
         * @param keys the keys to set
         */
        public GetRecordIntervalRequestContextBuilder fetchKeys(boolean keys) {
            this.fetchKeys = keys;
            return this;
        }
        /**
         * Instructs etalon calculator to include inactive versions into calculation.
         * @param includeInactive the includeInactive to set
         * @return self
         */
        public GetRecordIntervalRequestContextBuilder includeInactive(boolean includeInactive) {
            this.includeInactive = includeInactive;
            return this;
        }
        /**
         * Request unpublished state of a record or not.
         * @param includeDrafts requested state
         * @return self
         */
        public GetRecordIntervalRequestContextBuilder includeDrafts(boolean includeDrafts) {
            this.includeDrafts = includeDrafts;
            return this;
        }
        /**
         * Request information about winners
         * @param includeWinners requested state
         * @return self
         */
        public GetRecordIntervalRequestContextBuilder includeWinners(boolean includeWinners) {
            this.includeWinners = includeWinners;
            return this;
        }
        /**
         * @param fetchTimelineData the fetchTimelineData to set
         */
        public GetRecordIntervalRequestContextBuilder fetchData(boolean fetchTimelineData) {
            this.fetchData = fetchTimelineData;
            return this;
        }
        /**
         * @param skipCalculations the skipCalculations to set
         */
        public GetRecordIntervalRequestContextBuilder skipCalculations(boolean skipCalculations) {
            this.skipCalculations = skipCalculations;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public GetRecordIntervalRequestContext build() {
            return new GetRecordIntervalRequestContext(this);
        }
    }
}
