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
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragment;

/**
 * @author Mikhail Mikhailov
 * Gets relations of the left side record, denoted by fields for relation name 'name'.
 */
public class GetRelationsRequestContext
    extends AbstractRelationsFromRequestContext<GetRelationRequestContext>
    implements InputFragment<GetRelationsRequestContext> {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8833494823028426839L;
    /**
     * This context fragment id.
     */
    public static final FragmentId<GetRelationsRequestContext> FRAGMENT_ID
        = new FragmentId<>("GET_RELATIONS_REQUEST", () -> GetRelationsRequestContext.builder().build());
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
    protected GetRelationsRequestContext(GetRelationsRequestContextBuilder b) {
        super(b);
        this.relations = b.relations;
        this.relationNames = b.relationNames;
        this.forDate = b.forDate;
        this.forDatesFrame = b.forDatesFrame;
        this.forOperationId = b.forOperationId;
        this.forLastUpdate = b.forLastUpdate;

        flags.set(DataContextFlags.FLAG_INCLUDE_DRAFTS, b.includeDrafts);
        flags.set(DataContextFlags.FLAG_INCLUDE_INACTIVE, b.includeInactive);
        flags.set(DataContextFlags.FLAG_INCLUDE_MERGED, b.includeMerged);
        flags.set(DataContextFlags.FLAG_FETCH_TIMELINE_DATA, b.fetchTimelineData);
        flags.set(DataContextFlags.FLAG_REDUCE_REFERENCE_RELATIONS, b.reduceReferences);
        flags.set(DataContextFlags.FLAG_FETCH_ALL_RELATIONS, b.fetchAllRelations);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<GetRelationsRequestContext> fragmentId() {
        return FRAGMENT_ID;
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
     * @return the inactive elements
     */
    public boolean isIncludeInactive() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_INACTIVE);
    }
    /**
     * @return the merged elements
     */
    public boolean isIncludeMerged() {
        return flags.get(DataContextFlags.FLAG_INCLUDE_MERGED);
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
     * @return the fetchAllRelations
     */
    public boolean isFetchAllRelations() {
        return flags.get(DataContextFlags.FLAG_FETCH_ALL_RELATIONS);
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
         * Include inactive relations flag.
         */
        private boolean includeInactive;
        /**
         * Include merged relations flag.
         */
        private boolean includeMerged;
        /**
         * Return timeline with data.
         */
        private boolean fetchTimelineData;
        /**
         * Fetch all relations available for the given etalon id.
         */
        private boolean fetchAllRelations;
        /**
         * Reduce reference timeline
         */
        private boolean reduceReferences;
        /**
         * Constructor.
         */
        protected GetRelationsRequestContextBuilder() {
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
         * Request inactive additionally. Show inactive rels.
         */
        public GetRelationsRequestContextBuilder includeInactive(boolean includeInactive) {
            this.includeInactive = includeInactive;
            return this;
        }
        /**
         * Request merged additionally. Show merged rels.
         */
        public GetRelationsRequestContextBuilder includeMerged(boolean includeMerged) {
            this.includeMerged = includeMerged;
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
         * @param fetchAllRelations the fetchAllRelations to set
         */
        public GetRelationsRequestContextBuilder fetchAllRelations(boolean fetchAllRelations) {
            this.fetchAllRelations = fetchAllRelations;
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
