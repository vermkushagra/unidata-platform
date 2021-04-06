/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.Date;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RelationKeys;

/**
 * @author Mikhail Mikhailov
 * Gets relations
 */
public class GetRelationRequestContext
    extends AbstractRelationToRequestContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -5874979941996090899L;
    /**
     * Ealon key.
     */
    private final String relationEtalonKey;
    /**
     * Origin key.
     */
    private final String relationOriginKey;
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
     * Request tasks additionally. Show draft version.
     */
    private final boolean tasks;
    /**
     * Show draft version.
     */
    private final boolean includeDrafts;
    /**
     * Constructor.
     */
    private GetRelationRequestContext(GetRelationRequestContextBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.relationEtalonKey = b.relationEtalonKey;
        this.relationOriginKey = b.relationOriginKey;
        this.forDate = b.forDate;
        this.forOperationId = b.forOperationId;
        this.tasks = b.tasks;
        this.includeDrafts = b.includeDrafts;
    }

    /**
     * @return the goldenKey
     */
    @Override
    public String getRelationEtalonKey() {
        return relationEtalonKey;
    }

    /**
     * @return the relationOriginKey
     */
    @Override
    public String getRelationOriginKey() {
        return relationOriginKey;
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
        return tasks;
    }


    /**
     * @return the unpublishedState
     */
    public boolean isIncludeDrafts() {
        return includeDrafts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationKeys relationKeys() {
        return getFromStorage(relationKeysId());
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
    public static class GetRelationRequestContextBuilder {
        /**
         * Golden key.
         */
        private String relationEtalonKey;
        /**
         * Origin key.
         */
        private String relationOriginKey;
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
         * Constructor.
         */
        public GetRelationRequestContextBuilder() {
            super();
        }

        /**
         * @param relationEtalonKey the etalon key to set
         */
        public GetRelationRequestContextBuilder relationEtalonKey(String relationEtalonKey) {
            this.relationEtalonKey = relationEtalonKey;
            return this;
        }

        /**
         * @param relationOriginKey the origin key to set
         */
        public GetRelationRequestContextBuilder relationOriginKey(String relationOriginKey) {
            this.relationOriginKey = relationOriginKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetRelationRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetRelationRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetRelationRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public GetRelationRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public GetRelationRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public GetRelationRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public GetRelationRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }
        /**
         * @param forDate the forDate to set
         */
        public GetRelationRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
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
         * Request tasks additionally. Show draft version.
         */
        public GetRelationRequestContextBuilder tasks(boolean tasks) {
            this.tasks = tasks;
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
         * Builds a context.
         * @return a new context
         */
        public GetRelationRequestContext build() {
            return new GetRelationRequestContext(this);
        }
    }
}
