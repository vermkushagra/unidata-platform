package org.unidata.mdm.data.context;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.unidata.mdm.core.context.ValidityRangeContext;

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
     * Constructor.
     * @param b the builder
     */
    private UpsertRelationsRequestContext(UpsertRelationsRequestContextBuilder b) {
        super(b);
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
    public static class UpsertRelationsRequestContextBuilder
        extends AbstractRelationsFromRequestContextBuilder<UpsertRelationsRequestContextBuilder> {
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
        @Override
        public UpsertRelationsRequestContext build() {
            return new UpsertRelationsRequestContext(this);
        }
    }
}
