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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;



public class IndexRequestContextConfig {
    private final boolean reindexRecords;
    private final boolean reindexRelations;
    private final boolean reindexClassifiers;
    private final boolean reindexMatching;

    private final String operationId;

    private final boolean skipDQ;

    private final boolean suppressConsistencyChecks;

    private final boolean skipNotification;

    private final boolean indexesAreEmpty;

    private final List<String> relationNames;

    private final List<String> classifierNames;


    private IndexRequestContextConfig(final IndexRequestContextConfigBuilder builder) {
        this.reindexRecords = builder.reindexRecords;
        this.reindexRelations = builder.reindexRelations;
        this.reindexClassifiers = builder.reindexClassifiers;
        this.reindexMatching = builder.reindexMatching;
        this.skipNotification = builder.skipNotification;
        this.indexesAreEmpty = builder.indexesAreEmpty;

        this.operationId = builder.operationId;

        this.skipDQ = builder.skipDQ;
        this.suppressConsistencyChecks = builder.suppressConsistencyChecks;

        this.relationNames = builder.relationNames;
        this.classifierNames = builder.classifierNames;
    }

    public boolean isReindexRecords() {
        return reindexRecords;
    }

    public boolean isReindexRelations() {
        return reindexRelations;
    }

    public boolean isReindexClassifiers() {
        return reindexClassifiers;
    }

    public boolean isReindexMatching() {
        return reindexMatching;
    }

    public boolean isSkipNotification() {
        return skipNotification;
    }

    public boolean isIndexesAreEmpty() {
        return indexesAreEmpty;
    }

    public String getOperationId() {
        return operationId;
    }

    public boolean isSkipDQ() {
        return skipDQ;
    }

    public boolean isSuppressConsistencyChecks() {
        return suppressConsistencyChecks;
    }

    public List<String> getRelationNames() {
        return relationNames;
    }

    public List<String> getClassifierNames() {
        return classifierNames;
    }

    public static IndexRequestContextConfigBuilder builder() {
        return new IndexRequestContextConfigBuilder();
    }

    public static class IndexRequestContextConfigBuilder {
        private boolean reindexRecords = true;

        private boolean reindexRelations = true;

        private boolean reindexClassifiers = true;

        private boolean reindexMatching = true;

        private boolean skipNotification = false;

        private boolean indexesAreEmpty = false;

        private String operationId;

        private boolean skipDQ = true;

        private boolean suppressConsistencyChecks = true;

        private List<String> relationNames = new ArrayList<>();

        private List<String> classifierNames = new ArrayList<>();

        private IndexRequestContextConfigBuilder() {}

        public IndexRequestContextConfigBuilder reindexRecords(final boolean reindexRecords) {
            this.reindexRecords = reindexRecords;
            return this;
        }

        public IndexRequestContextConfigBuilder reindexRelations(final boolean reindexRelations) {
            this.reindexRelations = reindexRelations;
            return this;
        }

        public IndexRequestContextConfigBuilder reindexClassifiers(final boolean reindexClassifiers) {
            this.reindexClassifiers = reindexClassifiers;
            return this;
        }

        public IndexRequestContextConfigBuilder reindexMatching(final boolean reindexMatching) {
            this.reindexMatching = reindexMatching;
            return this;
        }

        public IndexRequestContextConfigBuilder operationId(final String operationId) {
            this.operationId = operationId;
            return this;
        }

        public IndexRequestContextConfigBuilder skipDQ(final boolean skipDQ) {
            this.skipDQ = skipDQ;
            return this;
        }

        public IndexRequestContextConfigBuilder indexesAreEmpty(final boolean indexesAreEmpty) {
            this.indexesAreEmpty = indexesAreEmpty;
            return this;
        }

        public IndexRequestContextConfigBuilder skipNotification(final boolean skipNotification) {
            this.skipNotification = skipNotification;
            return this;
        }

        public IndexRequestContextConfigBuilder suppressConsistencyChecks(final boolean suppressConsistencyChecks) {
            this.suppressConsistencyChecks = suppressConsistencyChecks;
            return this;
        }

        public IndexRequestContextConfigBuilder relationNames(List<String> relationNames) {
            if (CollectionUtils.isNotEmpty(relationNames)) {
                this.relationNames = relationNames;
            }
            return this;
        }

        public IndexRequestContextConfigBuilder classifierNames(List<String> classifierNames) {
            if (CollectionUtils.isNotEmpty(classifierNames)) {
                this.classifierNames = classifierNames;
            }
            return this;
        }

        public IndexRequestContextConfig build() {
            return new IndexRequestContextConfig(this);
        }
    }
}
