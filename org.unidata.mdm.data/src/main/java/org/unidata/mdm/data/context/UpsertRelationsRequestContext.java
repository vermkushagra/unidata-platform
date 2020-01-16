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
 * Upsert relations request context.
 */
public class UpsertRelationsRequestContext
    extends AbstractRelationsFromRequestContext<UpsertRelationRequestContext>
    implements ValidityRangeContext, InputFragment<UpsertRelationsRequestContext> {
    /**
     * This fragment ID.
     */
    public static final FragmentId<UpsertRelationsRequestContext> FRAGMENT_ID
        = new FragmentId<>("UPSERT_RELATIONS_REQUEST", () -> UpsertRelationsRequestContext.builder().build());
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
    protected UpsertRelationsRequestContext(UpsertRelationsRequestContextBuilder b) {
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

    @Override
    public FragmentId<UpsertRelationsRequestContext> fragmentId() {
        return FRAGMENT_ID;
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
        protected UpsertRelationsRequestContextBuilder() {
            super();
        }
        /**
         * @param relations the relations to set
         * @return self
         */
        public UpsertRelationsRequestContextBuilder relations(Map<String, List<UpsertRelationRequestContext>> relations) {
            relations.forEach(this::relations);
            return this;
        }
        /**
         * @param relations the relations to set
         * @return self
         */
        public UpsertRelationsRequestContextBuilder relations(String name, List<UpsertRelationRequestContext> relations) {
            relations.forEach(v -> relation(name, v));
            return this;
        }
        /**
         * @param relations the relations to set
         * @return self
         */
        public UpsertRelationsRequestContextBuilder relation(String relationName, UpsertRelationRequestContext relation) {

            Objects.requireNonNull(relationName);
            if (Objects.isNull(this.relations)) {
                this.relations = new HashMap<>();
            }

            this.relations
                .computeIfAbsent(relationName, k -> new ArrayList<UpsertRelationRequestContext>())
                .add(relation);
            return this;
        }
        /**
         * @param relations the relations to set
         * @return self
         */
        public UpsertRelationsRequestContextBuilder relation(UpsertRelationRequestContext relation) {
            Objects.requireNonNull(relation);
            return relation(relation.getRelationName(), relation);
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
