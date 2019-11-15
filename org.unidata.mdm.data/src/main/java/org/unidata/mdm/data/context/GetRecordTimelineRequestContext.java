/**
 *
 */
package org.unidata.mdm.data.context;

import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Mikhail Mikhailov
 * Fetch timeline for a record.
 */
public class GetRecordTimelineRequestContext
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
     * For a particular date range.
     */
    private final Pair<Date, Date> forDatesFrame;
    /**
     * Constructor.
     */
    private GetRecordTimelineRequestContext(GetRecordTimelineRequestContextBuilder b) {
        super(b);
        this.forDate = b.forDate;
        this.forDatesFrame = b.forDatesFrame;

        // Flags
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
     * Builder shorthand.
     * @return builder
     */
    public static GetRecordTimelineRequestContextBuilder builder() {
        return new GetRecordTimelineRequestContextBuilder();
    }
    /**
     * Builder shorthand.
     * @return builder
     */
    public static GetRecordTimelineRequestContextBuilder builder(AbstractRecordIdentityContext other) {

        GetRecordTimelineRequestContextBuilder b = new GetRecordTimelineRequestContextBuilder(other);
        if (other instanceof GetRequestContext) {
            GetRequestContext gOther = (GetRequestContext) other;
            b.forDate = gOther.getForDate();
            b.fetchData = gOther.isFetchTimelineData();
            b.includeDrafts = gOther.isIncludeDrafts();
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
    public static class GetRecordTimelineRequestContextBuilder
        extends AbstractRecordIdentityContextBuilder<GetRecordTimelineRequestContextBuilder> {
        /**
         * For a particular date (as of).
         */
        private Date forDate;
        /**
         * For a particular date range (left <-> right).
         */
        private Pair<Date, Date> forDatesFrame;
        /**
         * View unpublished state or not.
         */
        private boolean includeDrafts;
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
        private GetRecordTimelineRequestContextBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param other the other identity
         */
        private GetRecordTimelineRequestContextBuilder(AbstractRecordIdentityContext other) {
            super(other);
        }
        /**
         * @param forDate the forDate to set
         */
        public GetRecordTimelineRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }
        /**
         * @param forDatesFrame the date frame to set
         */
        public GetRecordTimelineRequestContextBuilder forDatesFrame(Pair<Date, Date> forDatesFrame) {
            this.forDatesFrame = forDatesFrame;
            return this;
        }
        /**
         * Request unpublished state of a record or not.
         * @param includeDrafts requested state
         * @return self
         */
        public GetRecordTimelineRequestContextBuilder includeDrafts(boolean includeDrafts) {
            this.includeDrafts = includeDrafts;
            return this;
        }
        /**
         * @param fetchTimelineData the fetchTimelineData to set
         */
        public GetRecordTimelineRequestContextBuilder fetchData(boolean fetchTimelineData) {
            this.fetchData = fetchTimelineData;
            return this;
        }
        /**
         * @param skipCalculations the skipCalculations to set
         */
        public GetRecordTimelineRequestContextBuilder skipCalculations(boolean skipCalculations) {
            this.skipCalculations = skipCalculations;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public GetRecordTimelineRequestContext build() {
            return new GetRecordTimelineRequestContext(this);
        }
    }
}
