/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.Date;

import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Gets classifier data record.
 */
public class GetClassifierDataRequestContext
    extends CommonRequestContext implements ClassifierIdentityContext  {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -5874979941996090899L;
    /**
     * Name of the classifier.
     */
    private final String classifierName;
    /**
     * Classifier node id.
     */
    private String classifierNodeId;
    /**
     * Classifier node name.
     */
    private final String classifierNodeName;
    /**
     * Classifier node code.
     */
    private final String classifierNodeCode;
    /**
     * Classifier etalon id.
     */
    private final String classifierEtalonKey;
    /**
     * Classifier origin id.
     */
    private final String classifierOriginKey;
    /**
     * Etalon key.
     */
    private final String etalonKey;
    /**
     * Origin key.
     */
    private final String originKey;
    /**
     * Origin foreign id.
     */
    private final String externalId;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Origin name.
     */
    private final String sourceSystem;
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
    private GetClassifierDataRequestContext(GetClassifierDataRequestContextBuilder b) {

        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.classifierEtalonKey = b.classifierEtalonKey;
        this.classifierOriginKey = b.classifierOriginKey;
        this.classifierName = b.classifierName;
        this.classifierNodeId = b.classifierNodeId;
        this.classifierNodeCode = b.classifierNodeCode;
        this.classifierNodeName = b.classifierNodeName;
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
        return getFromStorage(ClassifierIdentityContext.super.keysId());
    }

    /**
     * @return the classifierName
     */
    @Override
    public String getClassifierName() {
        return classifierName;
    }

    /**
     * @return the classifierNodeId
     */
    @Override
    public String getClassifierNodeId() {
        return classifierNodeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassifierNodeName() {
        return classifierNodeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassifierNodeCode() {
        return classifierNodeCode;
    }

    /**
     * @return the goldenKey
     */
    @Override
    public String getClassifierEtalonKey() {
        return classifierEtalonKey;
    }

    /**
     * @return the relationOriginKey
     */
    @Override
    public String getClassifierOriginKey() {
        return classifierOriginKey;
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
     * @return the fetchOrigins
     */
    public boolean isFetchOrigins() {
        return flags.get(ContextUtils.CTX_FLAG_FETCH_ORIGINS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeys classifierKeys() {
        return getFromStorage(classifierKeysId());
    }

    /**
     * Gets new builder.
     * @return builder instance
     */
    public static GetClassifierDataRequestContextBuilder builder() {
        return new GetClassifierDataRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetClassifierDataRequestContextBuilder {
        /**
         * Name of the classifier.
         */
        private String classifierName;
        /**
         * Classifier node id.
         */
        private String classifierNodeId;
        /**
         * Classifier node name.
         */
        private String classifierNodeName;
        /**
         * Classifier node code.
         */
        private String classifierNodeCode;
        /**
         * Classifier etalon id.
         */
        private String classifierEtalonKey;
        /**
         * Classifier origin id.
         */
        private String classifierOriginKey;
        /**
         * Golden key.
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
         * Origin name.
         */
        private String sourceSystem;
        /**
         * For a particular date (as of).
         */
        private Date forDate;
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
         * Return fetchOrigins or not.
         */
        private boolean fetchOrigins;
        /**
         * Constructor.
         */
        private GetClassifierDataRequestContextBuilder() {
            super();
        }

        /**
         * @param classifierName the name to set
         */
        public GetClassifierDataRequestContextBuilder classifierName(String classifierName) {
            this.classifierName = classifierName;
            return this;
        }

        /**
         * @param classifierNodeId the node id to set
         */
        public GetClassifierDataRequestContextBuilder classifierNodeId(String classifierNodeId) {
            this.classifierNodeId = classifierNodeId;
            return this;
        }

        /**
         * Classifier node name.
         */
        public GetClassifierDataRequestContextBuilder classifierNodeName(String classifierNodeName) {
            this.classifierNodeName = classifierNodeName;
            return this;
        }

        /**
         * Classifier node code.
         */
        public GetClassifierDataRequestContextBuilder classifierNodeCode(String classifierNodeCode) {
            this.classifierNodeCode = classifierNodeCode;
            return this;
        }

        /**
         * @param classifierEtalonKey the etalon key to set
         */
        public GetClassifierDataRequestContextBuilder classifierEtalonKey(String classifierEtalonKey) {
            this.classifierEtalonKey = classifierEtalonKey;
            return this;
        }

        /**
         * @param classifierOriginKey the origin key to set
         */
        public GetClassifierDataRequestContextBuilder classifierOriginKey(String classifierOriginKey) {
            this.classifierOriginKey = classifierOriginKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetClassifierDataRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetClassifierDataRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetClassifierDataRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetClassifierDataRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public GetClassifierDataRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public GetClassifierDataRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public GetClassifierDataRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }
        /**
         * @param forDate the forDate to set
         */
        public GetClassifierDataRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }
        /**
         * @param forOperationId the forOperationId to set
         */
        public GetClassifierDataRequestContextBuilder forOperationId(String forOperationId) {
            this.forOperationId = forOperationId;
            return this;
        }
        /**
         * Request tasks additionally. Show draft version.
         */
        public GetClassifierDataRequestContextBuilder tasks(boolean tasks) {
            this.tasks = tasks;
            return this;
        }
        /**
         * Request tasks additionally. Show draft version.
         */
        public GetClassifierDataRequestContextBuilder includeDrafts(boolean includeDrafts) {
            this.includeDrafts = includeDrafts;
            return this;
        }
        /**
         * @param fetchOrigins the fetchOrigins to set
         */
        public GetClassifierDataRequestContextBuilder fetchOrigins(boolean fetchOrigins) {
            this.fetchOrigins = fetchOrigins;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        public GetClassifierDataRequestContext build() {
            return new GetClassifierDataRequestContext(this);
        }
    }
}
