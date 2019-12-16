package org.unidata.mdm.data.context;

import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.data.service.segments.relations.RelationGetStartExecutor;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.system.type.pipeline.PipelineInput;

/**
 * @author Mikhail Mikhailov
 * Gets relations
 */
public class GetRelationRequestContext
    extends AbstractRelationToRequestContext
    implements PipelineInput, AccessRightContext, ReadOnlyTimelineContext<OriginRelation>, SetupAwareContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -5874979941996090899L;
    /**
     * For a particular date (as of).
     */
    private final Date forDate;
    /**
     * For a particular date range (left &lt;-&gt; right).
     */
    private final Pair<Date, Date> forDatesFrame;
    /**
     * Operation id.
     */
    private final String forOperationId;
    /**
     * Last update date to cut off versions.
     */
    private final Date forLastUpdate;
    /**
     * Constructor.
     */
    protected GetRelationRequestContext(GetRelationRequestContextBuilder b) {
        super(b);
        this.forDate = b.forDate;
        this.forDatesFrame = b.forDatesFrame;
        this.forOperationId = b.forOperationId;
        this.forLastUpdate = b.forLastUpdate;

        flags.set(DataContextFlags.FLAG_INCLUDE_DRAFTS, b.includeDrafts);
        flags.set(DataContextFlags.FLAG_FETCH_TIMELINE_DATA, b.fetchTimelineData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStartTypeId() {
        return RelationGetStartExecutor.SEGMENT_ID;
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
     * @return the unpublishedState
     */
    public boolean isIncludeDrafts() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_DRAFTS);
    }
    /**
     * @return the fetchTimelineData
     */
    public boolean isFetchTimelineData() {
        return flags.get(DataContextFlags.FLAG_FETCH_TIMELINE_DATA);
    }
    /**
     *
     * @return builder
     */
    public static GetRelationRequestContextBuilder builder(){
        return new GetRelationRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetRelationRequestContextBuilder extends AbstractRelationToRequestContextBuilder<GetRelationRequestContextBuilder> {
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
         * Last update date to cut off versions.
         */
        private Date forLastUpdate;
        /**
         * Show draft version.
         */
        private boolean includeDrafts;
        /**
         * Return timeline with data.
         */
        private boolean fetchTimelineData;
        /**
         * Constructor.
         */
        protected GetRelationRequestContextBuilder() {
            super();
        }
        /**
         * @param forDate the forDate to set
         */
        public GetRelationRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }
        /**
         * @param forDatesFrame the forDate to set
         */
        public GetRelationRequestContextBuilder forDatesFrame(Pair<Date, Date> forDatesFrame) {
            this.forDatesFrame = forDatesFrame;
            return this;
        }
        /**
         * @param forOperationId the forOperationId to set
         */
        public GetRelationRequestContextBuilder forOperationId(String forOperationId) {
            this.forOperationId = forOperationId;
            return this;
        }
        /**
         * Sets last update date to the context.
         * @param lastUpdate the date
         * @return self
         */
        public GetRelationRequestContextBuilder forLastUpdate(Date lastUpdate) {
            this.forLastUpdate = lastUpdate;
            return this;
        }
        /**
         * Request tasks additionally. Show draft version.
         */
        public GetRelationRequestContextBuilder includeDrafts(boolean includeDrafts) {
            this.includeDrafts = includeDrafts;
            return this;
        }
        /**
         * @param fetchTimelineData the fetchTimelineData to set
         */
        public GetRelationRequestContextBuilder fetchTimelineData(boolean fetchTimelineData) {
            this.fetchTimelineData = fetchTimelineData;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public GetRelationRequestContext build() {
            return new GetRelationRequestContext(this);
        }
    }
}
