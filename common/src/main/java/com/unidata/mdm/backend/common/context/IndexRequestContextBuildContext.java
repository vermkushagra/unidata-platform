package com.unidata.mdm.backend.common.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

public class IndexRequestContextBuildContext {
    private final long gsn;
    private final boolean reindexRecords;
    private final boolean reindexRelations;

    private final String operationId;

    private final boolean skipDQ;

    private final boolean suppressConsistencyChecks;

    private final Map<String, List<String>> relationNames;

    private final Map<String, List<String>> classifierNames;


    private IndexRequestContextBuildContext(final IndexRequestContextBuildContextBuilder builder) {
        this.gsn = builder.gsn;
        this.reindexRecords = builder.reindexRecords;
        this.reindexRelations = builder.reindexRelations;

        this.operationId = builder.operationId;

        this.skipDQ = builder.skipDQ;
        this.suppressConsistencyChecks = builder.suppressConsistencyChecks;

        this.relationNames = builder.relationNames;
        this.classifierNames = builder.classifierNames;
    }

    public long getGsn() {
        return gsn;
    }

    public boolean isReindexRecords() {
        return reindexRecords;
    }

    public boolean isReindexRelations() {
        return reindexRelations;
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

    public Map<String, List<String>> getRelationNames() {
        return relationNames;
    }

    public Map<String, List<String>> getClassifierNames() {
        return classifierNames;
    }

    public static IndexRequestContextBuildContextBuilder builder() {
        return new IndexRequestContextBuildContextBuilder();
    }

    public static class IndexRequestContextBuildContextBuilder {
        private long gsn;

        private boolean reindexRecords = true;

        private boolean reindexRelations = true;

        private String operationId;

        private boolean skipDQ = true;

        private boolean suppressConsistencyChecks = true;

        private Map<String, List<String>> relationNames = new HashMap<>();

        private Map<String, List<String>> classifierNames = new HashMap<>();

        private IndexRequestContextBuildContextBuilder() {}

        public IndexRequestContextBuildContextBuilder gsn(final long gsn) {
            this.gsn = gsn;
            return this;
        }

        public IndexRequestContextBuildContextBuilder reindexRecords(final boolean reindexRecords) {
            this.reindexRecords = reindexRecords;
            return this;
        }

        public IndexRequestContextBuildContextBuilder reindexRelations(final boolean reindexRelations) {
            this.reindexRelations = reindexRelations;
            return this;
        }

        public IndexRequestContextBuildContextBuilder operationId(final String operationId) {
            this.operationId = operationId;
            return this;
        }

        public IndexRequestContextBuildContextBuilder skipDQ(final boolean skipDQ) {
            this.skipDQ = skipDQ;
            return this;
        }

        public IndexRequestContextBuildContextBuilder suppressConsistencyChecks(final boolean suppressConsistencyChecks) {
            this.suppressConsistencyChecks = suppressConsistencyChecks;
            return this;
        }

        public IndexRequestContextBuildContextBuilder relationNames(Map<String, List<String>> relationNames) {
            if (MapUtils.isNotEmpty(relationNames)) {
                this.relationNames = relationNames;
            }
            return this;
        }

        public IndexRequestContextBuildContextBuilder classifierNames(Map<String, List<String>> classifierNames) {
            if (MapUtils.isNotEmpty(classifierNames)) {
                this.classifierNames = classifierNames;
            }
            return this;
        }

        public IndexRequestContextBuildContext build() {
            return new IndexRequestContextBuildContext(this);
        }
    }
}
