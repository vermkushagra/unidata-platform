package org.unidata.mdm.data.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.unidata.mdm.core.context.ValidityRangeContext;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragment;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DeleteRelationsRequestContext
    extends AbstractRelationsFromRequestContext<DeleteRelationRequestContext>
    implements ValidityRangeContext, InputFragment<DeleteRelationsRequestContext> {
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -6383509762622013682L;
    /**
     * This context fragment id.
     */
    public static final FragmentId<DeleteRelationsRequestContext> FRAGMENT_ID
        = new FragmentId<>("DELETE_RELATIONS_REQUEST", () -> DeleteRelationsRequestContext.builder().build());
    /**
     * The relations to upsert.
     */
    private final Map<String, List<DeleteRelationRequestContext>> relations;
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
     * Clean all (no particular relations).
     */
    private final boolean cleanAll;
    /**
     * Set range from.
     */
    private final Date validFrom;
    /**
     * Set range to.
     */
    private final Date validTo;
    /**
     * Constructor.
     * @param b the builder
     */
    protected DeleteRelationsRequestContext(DeleteRelationsRequestContextBuilder b) {
        super(b);
        this.relations = b.relations;
        this.lastUpdate = b.lastUpdate;
        this.skipCleanse = b.skipCleanse;
        this.bypassExtensionPoints = b.bypassExtensionPoints;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.cleanAll = b.cleanAll;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<DeleteRelationsRequestContext> fragmentId() {
        return FRAGMENT_ID;
    }

    @Override
    public void setOperationId(String operationId) {
        super.setOperationId(operationId);
        if(Objects.nonNull(relations)){
            relations.values().stream()
                    .flatMap(Collection::stream)
                    .forEach(rel-> rel.setOperationId(operationId));
        }
    }

    /**
     * @return the relations
     */
    @Override
    public Map<String, List<DeleteRelationRequestContext>> getRelations() {
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
     * @return the cleanAll
     */
    public boolean isCleanAll() {
        return cleanAll;
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
     * Gets new builder.
     * @return builder
     */
    public static DeleteRelationsRequestContextBuilder builder() {
        return new DeleteRelationsRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder class.
     */
    public static class DeleteRelationsRequestContextBuilder
        extends AbstractRelationsFromRequestContextBuilder<DeleteRelationsRequestContextBuilder> {
        /**
         * The relations to upsert.
         */
        private Map<String, List<DeleteRelationRequestContext>> relations;
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
         * Clean all (no particular relations).
         */
        private boolean cleanAll;
        /**
         * Set range from.
         */
        private Date validFrom;
        /**
         * Set range to.
         */
        private Date validTo;
        /**
         * Constructor.
         */
        protected DeleteRelationsRequestContextBuilder() {
            super();
        }
        /**
         * @param relations the relations to set
         * @return self
         */
        public DeleteRelationsRequestContextBuilder relations(Map<String, List<DeleteRelationRequestContext>> relations) {
            relations.forEach(this::relations);
            return this;
        }
        /**
         * @param relations the relations to set
         * @return self
         */
        public DeleteRelationsRequestContextBuilder relations(String name, List<DeleteRelationRequestContext> relations) {
            relations.forEach(v -> relation(name, v));
            return this;
        }
        /**
         * @param relations the relations to set
         * @return self
         */
        public DeleteRelationsRequestContextBuilder relation(String relationName, DeleteRelationRequestContext relation) {

            Objects.requireNonNull(relationName);
            if (Objects.isNull(this.relations)) {
                this.relations = new HashMap<>();
            }

            this.relations
                .computeIfAbsent(relationName, k -> new ArrayList<DeleteRelationRequestContext>())
                .add(relation);
            return this;
        }
        /**
         * @param relations the relations to set
         * @return self
         */
        public DeleteRelationsRequestContextBuilder relation(DeleteRelationRequestContext relation) {
            Objects.requireNonNull(relation);
            return relation(relation.getRelationName(), relation);
        }
        /**
         * @param lastUpdate the last update to set
         */
        public DeleteRelationsRequestContextBuilder lastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }
        /**
         * @param skipCleanse skip cleanse or not
         */
        public DeleteRelationsRequestContextBuilder skipCleanse(boolean skipCleanse) {
            this.skipCleanse = skipCleanse;
            return this;
        }
        /**
         * @param bypassExtensionPoints bypass extension points or not
         */
        public DeleteRelationsRequestContextBuilder bypassExtensionPoints(boolean bypassExtensionPoints) {
            this.bypassExtensionPoints = bypassExtensionPoints;
            return this;
        }
        /**
         * @param validFrom the range from to set
         */
        public DeleteRelationsRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }
        /**
         * @param validTo the range to to set
         */
        public DeleteRelationsRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }
        /**
         * @param cleanAll the cleanAll to to set
         */
        public DeleteRelationsRequestContextBuilder cleanAll(boolean cleanAll) {
            this.cleanAll = cleanAll;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        @Override
        public DeleteRelationsRequestContext build() {
            return new DeleteRelationsRequestContext(this);
        }
    }
}
