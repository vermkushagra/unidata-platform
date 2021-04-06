package com.unidata.mdm.backend.common.context;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.DataQualityError;

/**
 * @author Mikhail Mikhailov
 * Upsert relations request context.
 */
public class UpsertRelationsRequestContext
    extends AbstractRelationsFromRequestContext<UpsertRelationRequestContext>
    implements ValidityRangeContext {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = 5342638028065367745L;
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
    private final Map<String, List<UpsertRelationRequestContext>> relations;
     /**
     * Last update date to use (optional).
     */
    private final Date lastUpdate;
    /**
     * Skip cleanse functions.
     */
    private final boolean skipCleanse;
    /**
     * Bypass extension points during upsert.
     */
    private final boolean bypassExtensionPoints;
    /**
     * Set range from.
     */
    private final Date validFrom;
    /**
     * Set range to.
     */
    private final Date validTo;
    /**
     * Override on the supplied validFrom and validTo.
     */
    private final boolean override;
    /**
     * List with data quality errors.
     */
    private List<DataQualityError> dqErrors;
    /**
     * Constructor.
     * @param b the builder
     */
    private UpsertRelationsRequestContext(UpsertRelationsRequestContextBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.relations = b.relations;
        this.lastUpdate = b.lastUpdate;
        this.skipCleanse = b.skipCleanse;
        this.bypassExtensionPoints = b.bypassExtensionPoints;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.override = b.override;
    }

    @Override
    public void setOperationId(String operationId) {
        super.setOperationId(operationId);
        if (Objects.nonNull(relations)) {
            relations.values().stream()
                    .flatMap(Collection::stream)
                    .forEach(ctx -> ctx.setOperationId(operationId));
        }
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
    public Map<String, List<UpsertRelationRequestContext>> getRelations() {
        return relations;
    }
    /**
     * @return the lastUpdate
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return the skipCleanse
     */
    public boolean isSkipCleanse() {
        return skipCleanse;
    }

    /**
     * @return the bypassExtensionPoints
     */
    public boolean isBypassExtensionPoints() {
        return bypassExtensionPoints;
    }

    /**
     * @return the validFrom
     */
    @Override
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @return the validTo
     */
    @Override
    public Date getValidTo() {
        return validTo;
    }

    /**
     * @return the override
     */
    public boolean isOverride() {
        return override;
    }

    /**
     * @return the dqErrors
     */
    public List<DataQualityError> getDqErrors() {
        return dqErrors;
    }
    /**
     * @param dqErrors the dqErrors to set
     */
    public void setDqErrors(List<DataQualityError> dqErrors) {
        this.dqErrors = dqErrors;
    }

    /**
     * Gets new builder.
     * @return builder
     */
    public static UpsertRelationsRequestContextBuilder builder() {
        return new UpsertRelationsRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder class.
     */
    public static class UpsertRelationsRequestContextBuilder {
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
        private Map<String, List<UpsertRelationRequestContext>> relations;
        /**
         * Last update date to use (optional).
         */
        private Date lastUpdate;
        /**
         * Skip cleanse functions.
         */
        private boolean skipCleanse;
        /**
         * Bypass extension points during upsert.
         */
        private boolean bypassExtensionPoints;
        /**
         * Set range from.
         */
        private Date validFrom;
        /**
         * Set range to.
         */
        private Date validTo;
        /**
         * Override on the supplied validFrom and validTo.
         */
        private boolean override;
        /**
         * Constructor.
         */
        public UpsertRelationsRequestContextBuilder() {
            super();
        }
        /**
         * @param etalonKey the goldenKey to set
         */
        public UpsertRelationsRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public UpsertRelationsRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public UpsertRelationsRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public UpsertRelationsRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public UpsertRelationsRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public UpsertRelationsRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public UpsertRelationsRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param relations the relations to set
         * @return self
         */
        public UpsertRelationsRequestContextBuilder relations(Map<String, List<UpsertRelationRequestContext>> relations) {
            this.relations = relations;
            return this;
        }
        /**
         * @param lastUpdate the last update to set
         */
        public UpsertRelationsRequestContextBuilder lastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        /**
         * @param skipCleanse skip cleanse or not
         */
        public UpsertRelationsRequestContextBuilder skipCleanse(boolean skipCleanse) {
            this.skipCleanse = skipCleanse;
            return this;
        }

        /**
         * @param bypassExtensionPoints bypass extension points or not
         */
        public UpsertRelationsRequestContextBuilder bypassExtensionPoints(boolean bypassExtensionPoints) {
            this.bypassExtensionPoints = bypassExtensionPoints;
            return this;
        }

        /**
         * @param validFrom the range from to set
         */
        public UpsertRelationsRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        /**
         * @param validTo the range to to set
         */
        public UpsertRelationsRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }

        /**
         * Override all missing relations on the supplied period.
         * @param override the override to set
         * @return self
         */
        public UpsertRelationsRequestContextBuilder override(boolean override) {
            this.override = override;
            return this;
        }

        /**
         * Builds a context.
         * @return a new context
         */
        public UpsertRelationsRequestContext build() {
            return new UpsertRelationsRequestContext(this);
        }
    }
}
