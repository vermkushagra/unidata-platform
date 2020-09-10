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
package com.unidata.mdm.backend.common.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;

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
     * Etalon from key.
     */
    private final String etalonKey;
    /**
     * Origin from key.
     */
    private final String originKey;
    /**
     * Origin from external id.
     */
    private final String externalId;
    /**
     * Entity from name.
     */
    private final String entityName;
    /**
     * Origin from name.
     */
    private final String sourceSystem;
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
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.relations = b.relations;
        this.relationNames = b.relationNames;
        this.forDate = b.forDate;
        this.forDatesFrame = b.forDatesFrame;
        this.forOperationId = b.forOperationId;

        flags.set(ContextUtils.CTX_FLAG_FETCH_TASKS, b.tasks);
        flags.set(ContextUtils.CTX_FLAG_INCLUDE_DRAFTS, b.includeDrafts);
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
     * @return the tasks
     */
    public boolean isTasks() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_TASKS);
    }

    /**
     * @return the unpublishedState
     */
    public boolean isIncludeDrafts() {
        return flags.get(ContextUtils.CTX_FLAG_INCLUDE_DRAFTS);
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
    public static class GetRelationsRequestContextBuilder {
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
         * Request tasks additionally. Show draft version.
         */
        private boolean tasks;
        /**
         * Show draft version.
         */
        private boolean includeDrafts;
        /**
         * Constructor.
         */
        public GetRelationsRequestContextBuilder() {
            super();
        }
        /**
         * @param etalonKey the goldenKey to set
         */
        public GetRelationsRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the goldenKey to set
         */
        public GetRelationsRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public GetRelationsRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the etalonKey to set
         */
        public GetRelationsRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public GetRelationsRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public GetRelationsRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public GetRelationsRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
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
         * Request tasks additionally. Show draft version.
         */
        public GetRelationsRequestContextBuilder tasks(boolean tasks) {
            this.tasks = tasks;
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
         * Builds a context.
         * @return a new context
         */
        public GetRelationsRequestContext build() {
            return new GetRelationsRequestContext(this);
        }
    }
}
