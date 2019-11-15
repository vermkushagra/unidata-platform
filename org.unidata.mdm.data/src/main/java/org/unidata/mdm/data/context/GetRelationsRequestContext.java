/**
 *
 */
package org.unidata.mdm.data.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Mikhail Mikhailov
 * Gets relations of the left side record, denoted by fields for relation name 'name'.
 */
public class GetRelationsRequestContext
    extends AbstractRelationsFromRequestContext<GetRelationRequestContext> {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8833494823028426839L;
    /**
     * The relations to upsert.
     */
    private final Map<String, List<GetRelationRequestContext>> relations;
    /**
     * 'Load all for names' support.
     */
    private final List<String> relationNames;
    /**
     * For a particular date (as of).
     */
    private final Date forDate;
    /**
     * Last update date to cut off versions.
     */
    private final Date forLastUpdate;
    /**
     * For a particular date range (left &lt;-&gt; right).
     */
    private final Pair<Date, Date> forDatesFrame;
    /**
     * Operation id.
     */
    private final String forOperationId;
    /**
     * Constructor.
     */
    private GetRelationsRequestContext(GetRelationsRequestContextBuilder b) {
        super(b);
        this.relations = b.relations;
        this.relationNames = b.relationNames;
        this.forDate = b.forDate;
        this.forDatesFrame = b.forDatesFrame;
        this.forOperationId = b.forOperationId;
        this.forLastUpdate = b.forLastUpdate;

        flags.set(DataContextFlags.FLAG_INCLUDE_DRAFTS, b.includeDrafts);
        flags.set(DataContextFlags.FLAG_FETCH_TIMELINE_DATA, b.fetchTimelineData);
        flags.set(DataContextFlags.FLAG_REDUCE_REFERENCE_RELATIONS, b.reduceReferences);
    }

    /**
     * @return the relations
     */
    @Override
    public Map<String, List<GetRelationRequestContext>> getRelations() {
        return relations == null ? Collections.emptyMap() : this.relations;
    }

    /**
     * @return the relationNames
     */
    public List<String> getRelationNames() {
        return relationNames == null ? Collections.emptyList() : this.relationNames;
    }

    /**
     * @return the forDate
     */
    public Date getForDate() {
        return forDate;
    }

    /**
     * @return the lastUpdate
     */
    public Date getForLastUpdate() {
        return forLastUpdate;
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
    public boolean isFetchTimelineData() {
        return flags.get(DataContextFlags.FLAG_FETCH_TIMELINE_DATA);
    }

    /**
     * @return the reduce references
     */
    public boolean isReduceReferences() {
        return flags.get(DataContextFlags.FLAG_REDUCE_REFERENCE_RELATIONS);
    }

    /**
     * Gets new builder.
     * @return builder
     */
    public static GetRelationsRequestContextBuilder builder() {
        return new GetRelationsRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetRelationsRequestContextBuilder
        extends AbstractRelationsFromRequestContextBuilder<GetRelationsRequestContextBuilder> {
        /**
         * The relations to upsert.
         */
        private Map<String, List<GetRelationRequestContext>> relations;
        /**
         * 'Load all for names' support.
         */
        private List<String> relationNames;
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
         * Reduce reference timeline
         */
        private boolean reduceReferences;
        /**
         * Constructor.
         */
        public GetRelationsRequestContextBuilder() {
            super();
        }

        /**
         * @param relations the relations to set
         * @return self
         */
        public GetRelationsRequestContextBuilder relations(Map<String, List<GetRelationRequestContext>> relations) {
            this.relations = relations;
            return this;
        }

        /**
         * @param relationNames the relations to set
         * @return self
         */
        public GetRelationsRequestContextBuilder relationNames(List<String> relationNames) {
            this.relationNames = relationNames;
            return this;
        }

        /**
         * @param relationNames the relations to set
         * @return self
         */
        public GetRelationsRequestContextBuilder relationNames(String... relationNames) {
            this.relationNames = Arrays.asList(relationNames);
            return this;
        }

        /**
         * @param forDate the forDate to set
         */
        public GetRelationsRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }

        /**
         * @param forDatesFrame the forDate to set
         */
        public GetRelationsRequestContextBuilder forDatesFrame(Pair<Date, Date> forDatesFrame) {
            this.forDatesFrame = forDatesFrame;
            return this;
        }

        /**
         * @param forOperationId the forOperationId to set
         */
        public GetRelationsRequestContextBuilder forOperationId(String forOperationId) {
            this.forOperationId = forOperationId;
            return this;
        }

        /**
         * Sets last update date to the context.
         * @param lastUpdate the date
         * @return self
         */
        public GetRelationsRequestContextBuilder forLastUpdate(Date lastUpdate) {
            this.forLastUpdate = lastUpdate;
            return this;
        }
        /**
         * Request tasks additionally. Show draft version.
         */
        public GetRelationsRequestContextBuilder includeDrafts(boolean includeDrafts) {
            this.includeDrafts = includeDrafts;
            return this;
        }
        /**
         * @param fetchTimelineData the fetchTimelineData to set
         */
        public GetRelationsRequestContextBuilder fetchTimelineData(boolean fetchTimelineData) {
            this.fetchTimelineData = fetchTimelineData;
            return this;
        }

        /**
         * @param reduceReferences the reduceReferences to set
         */
        public GetRelationsRequestContextBuilder reduceReferences(boolean reduceReferences) {
            this.reduceReferences = reduceReferences;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public GetRelationsRequestContext build() {
            return new GetRelationsRequestContext(this);
        }
    }
}
