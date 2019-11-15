package org.unidata.mdm.data.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Fetch timeline for a record.
 */
public class GetRecordsTimelinesRequestContext extends CommonRequestContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -6826317454436803507L;
    /**
     * Etalon key.
     */
    private final List<String> etalonKeys;
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
    private GetRecordsTimelinesRequestContext(GetRecordTimelineRequestContextBuilder b) {

        super(b);
        this.etalonKeys = new ArrayList<>(b.etalonKeys);
        this.forDate = b.forDate;
        this.forDatesFrame = b.forDatesFrame;

        // Flags
        flags.set(DataContextFlags.FLAG_INCLUDE_DRAFTS, b.includeDrafts);
        flags.set(DataContextFlags.FLAG_FETCH_TIMELINE_DATA, b.fetchData);
        flags.set(DataContextFlags.FLAG_SKIP_TIMELINE_CALCULATIONS, b.skipCalculations);
    }
    /**
     * @return the etalonKey
     */

    public List<String> getEtalonKeys() {
        return etalonKeys;
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
     * {@inheritDoc}
     */
    public Collection<RecordKeys> keys() {
        return getFromStorage(RecordIdentityContext.SID_RECORD_KEYS);
    }
    /**
     * {@inheritDoc}
     */
    public void keys(Collection<RecordKeys> keys) {
        putToStorage(RecordIdentityContext.SID_RECORD_KEYS, keys);
    }
    /**
     * Builder shorthand.
     * @return builder
     */
    public static GetRecordTimelineRequestContextBuilder builder() {
        return new GetRecordTimelineRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetRecordTimelineRequestContextBuilder
        extends CommonRequestContextBuilder<GetRecordTimelineRequestContextBuilder> {
        /**
         * Keys.
         */
        private Set<String> etalonKeys = new HashSet<>();
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
        public GetRecordTimelineRequestContextBuilder() {
            super();
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public GetRecordTimelineRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKeys.add(etalonKey);
            return this;
        }
        /**
         * @param etalonKeys the etalonKeys to set
         */
        public GetRecordTimelineRequestContextBuilder etalonKeys(Collection<String> etalonKeys) {
            this.etalonKeys.addAll(etalonKeys);
            return this;
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
        public GetRecordsTimelinesRequestContext build() {
            return new GetRecordsTimelinesRequestContext(this);
        }
    }
}
