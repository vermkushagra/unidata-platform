package org.unidata.mdm.data.context;

import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Mikhail Mikhailov
 * Gets relations
 */
public class GetRelationTimelineRequestContext extends AbstractRelationToRequestContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -5874979941996090899L;
    /**
     * For a particular date (as of).
     */
    private final Date forDate;
    /**
     * For a particular date range.
     */
    private final Pair<Date, Date> forDatesFrame;
    /**
     * Operation id.
     */
    private final String forOperationId;
    /**
     * Constructor.
     */
    protected GetRelationTimelineRequestContext(GetRelationTimelineRequestContextBuilder b) {
        super(b);
        this.forDate = b.forDate;
        this.forDatesFrame = b.forDatesFrame;
        this.forOperationId = b.forOperationId;

        flags.set(DataContextFlags.FLAG_INCLUDE_DRAFTS, b.includeDrafts);
        flags.set(DataContextFlags.FLAG_FETCH_TIMELINE_DATA, b.fetchData);
        flags.set(DataContextFlags.FLAG_SKIP_TIMELINE_CALCULATIONS, b.skipCalculations);
    }
    /**
     * @return the forDate
     */
    public Date getForDate() {
        return forDate;
    }

    /**
     * @return the dates frame
     */
    public Pair<Date, Date> getForDatesFrame() {
        return forDatesFrame;
    }
    /**
     * @return the forOperationId
     */
    public String getForOperationId() {
        return forOperationId;
    }
    /**
     * @return the unpublishedState
     */
    public boolean isIncludeDrafts() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_DRAFTS);
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
     * Default empty builder.
     * @return builder
     */
    public static GetRelationTimelineRequestContextBuilder builder(){
        return new GetRelationTimelineRequestContextBuilder();
    }
    /**
    * Copy builder.
    * @return builder
    */
   public static GetRelationTimelineRequestContextBuilder builder(GetRelationRequestContext other) {

       GetRelationTimelineRequestContextBuilder b = new GetRelationTimelineRequestContextBuilder();
       b.relationEtalonKey = other.getRelationEtalonKey();
       b.relationOriginKey = other.getRelationOriginKey();
       b.entityName = other.getEntityName();
       b.etalonKey = other.getEtalonKey();
       b.externalId = other.getExternalId();
       b.sourceSystem = other.getSourceSystem();
       b.originKey = other.getOriginKey();
       b.shard = other.getShard();
       b.relationLsn = other.getLsn();
       b.fetchData = other.isFetchTimelineData();
       b.forDate = other.getForDate();
       b.forDatesFrame = other.getForDatesFrame();
       b.forOperationId = other.getOperationId();
       b.includeDrafts = other.isIncludeDrafts();

       return b;
   }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetRelationTimelineRequestContextBuilder
        extends AbstractRelationToRequestContextBuilder<GetRelationTimelineRequestContextBuilder> {
        /**
         * For a particular date (as of).
         */
        private Date forDate;
        /**
         * For a particular date range (left <-> right).
         */
        private Pair<Date, Date> forDatesFrame;
        /**
         * Operation id.
         */
        private String forOperationId;
        /**
         * Show draft version.
         */
        private boolean includeDrafts;
        /**
         * Return timeline with data.
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
        protected GetRelationTimelineRequestContextBuilder() {
            super();
        }
        /**
         * @param forDate the forDate to set
         */
        public GetRelationTimelineRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }
        /**
         * @param forDatesFrame the date frame to set
         */
        public GetRelationTimelineRequestContextBuilder forDatesFrame(Pair<Date, Date> forDatesFrame) {
            this.forDatesFrame = forDatesFrame;
            return this;
        }
        /**
         * @param forOperationId the forOperationId to set
         */
        public GetRelationTimelineRequestContextBuilder forOperationId(String forOperationId) {
            this.forOperationId = forOperationId;
            return this;
        }
        /**
         * Request tasks additionally. Show draft version.
         */
        public GetRelationTimelineRequestContextBuilder includeDrafts(boolean includeDrafts) {
            this.includeDrafts = includeDrafts;
            return this;
        }
        /**
         * @param fetchTimelineData the fetchTimelineData to set
         */
        public GetRelationTimelineRequestContextBuilder fetchData(boolean fetchTimelineData) {
            this.fetchData = fetchTimelineData;
            return this;
        }
        /**
         * @param skipCalculations the skipCalculations to set
         */
        public GetRelationTimelineRequestContextBuilder skipCalculations(boolean skipCalculations) {
            this.skipCalculations = skipCalculations;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public GetRelationTimelineRequestContext build() {
            return new GetRelationTimelineRequestContext(this);
        }
    }
}
