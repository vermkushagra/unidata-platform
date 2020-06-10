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

package com.unidata.mdm.backend.common.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Gets classifiers for record request context.
 */
public class GetClassifiersDataRequestContext
    extends CommonRequestContext implements RecordIdentityContext {
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
    private final Map<String, List<GetClassifierDataRequestContext>> classifiers;
    /**
     * 'Load all for names' support.
     */
    private final List<String> classifierNames;
    /**
     * For a particular date (as of).
     */
    private final Date forDate;
    /**
     * Operation id.
     */
    private final String forOperationId;
    /**
     * Constructor.
     */
    private GetClassifiersDataRequestContext(GetClassifiersDataRequestContextBuilder b) {

        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.classifiers = b.classifiers;
        this.classifierNames = b.classifierNames;
        this.forDate = b.forDate;
        this.forOperationId = b.forOperationId;

        // Flags
        flags.set(ContextUtils.CTX_FLAG_FETCH_ORIGINS, b.fetchOrigins);
        flags.set(ContextUtils.CTX_FLAG_FETCH_TASKS, b.tasks);
        flags.set(ContextUtils.CTX_FLAG_INCLUDE_DRAFTS, b.includeDrafts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(keysId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.RECORDS_RECORD_KEYS;
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
    public Map<String, List<GetClassifierDataRequestContext>> getClassifiers() {
        return classifiers == null ? Collections.emptyMap() : this.classifiers;
    }

    /**
     * @return the relationNames
     */
    public List<String> getClassifierNames() {
        return classifierNames == null ? Collections.emptyList() : this.classifierNames;
    }

    /**
     * @return the forDate
     */
    public Date getForDate() {
        return forDate;
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
    public static GetClassifiersDataRequestContextBuilder builder() {
        return new GetClassifiersDataRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetClassifiersDataRequestContextBuilder {
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
         * The specific classifier data record to get.
         */
        private Map<String, List<GetClassifierDataRequestContext>> classifiers;
        /**
         * 'Load all for names' support.
         */
        private List<String> classifierNames;
        /**
         * For a particular date (as of).
         */
        private Date forDate;
        /**
         * Operation id.
         */
        private String forOperationId;
        /**
         * Return fetchOrigins or not.
         */
        private boolean fetchOrigins;
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
        private GetClassifiersDataRequestContextBuilder() {
            super();
        }
        /**
         * @param etalonKey the goldenKey to set
         */
        public GetClassifiersDataRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the goldenKey to set
         */
        public GetClassifiersDataRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public GetClassifiersDataRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the etalonKey to set
         */
        public GetClassifiersDataRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public GetClassifiersDataRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public GetClassifiersDataRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public GetClassifiersDataRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param classifiers the classifiers to set
         * @return self
         */
        public GetClassifiersDataRequestContextBuilder classifiers(Map<String, List<GetClassifierDataRequestContext>> classifiers) {
            this.classifiers = classifiers;
            return this;
        }

        /**
         * @param classifierNames the classifierNames to set
         * @return self
         */
        public GetClassifiersDataRequestContextBuilder classifierNames(List<String> classifierNames) {
            this.classifierNames = classifierNames;
            return this;
        }

        /**
         * @param classifierNames the classifierNames to set
         * @return self
         */
        public GetClassifiersDataRequestContextBuilder classifierNames(String... classifierNames) {
            this.classifierNames = Arrays.asList(classifierNames);
            return this;
        }

        /**
         * @param forDate the forDate to set
         */
        public GetClassifiersDataRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }

        /**
         * @param forOperationId the forOperationId to set
         */
        public GetClassifiersDataRequestContextBuilder forOperationId(String forOperationId) {
            this.forOperationId = forOperationId;
            return this;
        }

        /**
         * @param fetchOrigins the fetchOrigins to set
         */
        public GetClassifiersDataRequestContextBuilder fetchOrigins(boolean fetchOrigins) {
            this.fetchOrigins = fetchOrigins;
            return this;
        }

        /**
         * Request tasks additionally. Show draft version.
         */
        public GetClassifiersDataRequestContextBuilder tasks(boolean tasks) {
            this.tasks = tasks;
            return this;
        }
        /**
         * Request tasks additionally. Show draft version.
         */
        public GetClassifiersDataRequestContextBuilder includeDrafts(boolean includeDrafts) {
            this.includeDrafts = includeDrafts;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        public GetClassifiersDataRequestContext build() {
            return new GetClassifiersDataRequestContext(this);
        }
    }
}
