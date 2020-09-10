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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DeleteRelationsRequestContext
    extends AbstractRelationsFromRequestContext<DeleteRelationRequestContext>
    implements ValidityRangeContext {

    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -6383509762622013682L;
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
    private DeleteRelationsRequestContext(DeleteRelationsRequestContextBuilder b) {
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
        this.cleanAll = b.cleanAll;
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
    public static class DeleteRelationsRequestContextBuilder {
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
        public DeleteRelationsRequestContextBuilder() {
            super();
        }
        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteRelationsRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the goldenKey to set
         */
        public DeleteRelationsRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * @param etalonKey the etalonKey to set
         */
        public DeleteRelationsRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the etalonKey to set
         */
        public DeleteRelationsRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public DeleteRelationsRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public DeleteRelationsRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public DeleteRelationsRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param relations the relations to set
         * @return self
         */
        public DeleteRelationsRequestContextBuilder relations(Map<String, List<DeleteRelationRequestContext>> relations) {
            this.relations = relations;
            return this;
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
        public DeleteRelationsRequestContext build() {
            return new DeleteRelationsRequestContext(this);
        }
    }

}
