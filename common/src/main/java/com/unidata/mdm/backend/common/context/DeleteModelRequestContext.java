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

/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 */
public class DeleteModelRequestContext extends CommonRequestContext implements ModelStorageSpecificContext, Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1636265075513576171L;

    /**
     * Entity IDs to delete..
     */
    private final List<String> entitiesIds;

    /**
     * Nested entities Ids to delete
     */
    private final List<String> nestedEntitiesIds;

    /**
     * Lookup entity IDs to delete..
     */
    private final List<String> lookupEntitiesIds;

    /**
     * Enumeration IDs to delete..
     */
    private final List<String> enumerationIds;

    /**
     * Source system IDs to delete..
     */
    private final List<String> sourceSystemIds;

    /**
     * Relation IDS.
     */
    private final List<String> relationIds;

    /**
     * Storage ID to apply the updates to.
     */
    private final String storageId;

    /**
     * Constructor.
     */
    private DeleteModelRequestContext(DeleteModelRequestContextBuilder b) {
        super();
        this.entitiesIds = b.entitiesIds;
        this.lookupEntitiesIds = b.lookupEntitiesIds;
        this.enumerationIds = b.enumerationIds;
        this.sourceSystemIds = b.sourceSystemIds;
        this.relationIds = b.relationIds;
        this.storageId = b.storageId;
        this.nestedEntitiesIds = b.nestedEntitiesIds;
    }

    /**
     * @return the entitiesIds
     */
    public List<String> getEntitiesIds() {
        return entitiesIds;
    }

    /**
     * @return the lookupEntitiesIds
     */
    public List<String> getLookupEntitiesIds() {
        return lookupEntitiesIds;
    }

    /**
     * @return the enumerationIds
     */
    public List<String> getEnumerationIds() {
        return enumerationIds;
    }

    /**
     * @return the sourceSystemIds
     */
    public List<String> getSourceSystemIds() {
        return sourceSystemIds;
    }

    /**
     * @return the relationIds
     */
    public List<String> getRelationIds() {
        return relationIds;
    }

    /**
     * @return the nested entities ids
     */
    public List<String> getNestedEntitiesIds() {
        return nestedEntitiesIds;
    }

    /**
     * @return the storageId
     */
    @Override
    public String getStorageId() {
        return storageId;
    }

    /**
     * @return true, if entitiesIds are set
     */
    public boolean hasEntitiesIds() {
        return entitiesIds != null && !entitiesIds.isEmpty();
    }

    /**
     * @return true, if lookupEntitiesIds are set
     */
    public boolean hasLookupEntitiesIds() {
        return lookupEntitiesIds != null && !lookupEntitiesIds.isEmpty();
    }

    /**
     * @return true, if enumerationIds are set
     */
    public boolean hasEnumerationIds() {
        return enumerationIds != null && !enumerationIds.isEmpty();
    }

    /**
     * @return true, if sourceSystemIds are set
     */
    public boolean hasSourceSystemIds() {
        return sourceSystemIds != null && !sourceSystemIds.isEmpty();
    }

    /**
     * @return true, if relationIds are set
     */
    public boolean hasRelationIds() {
        return relationIds != null && !relationIds.isEmpty();
    }

    public boolean hasNestedEntitiesIds() {
        return nestedEntitiesIds != null && !nestedEntitiesIds.isEmpty();
    }

    /**
     * @author Mikhail Mikhailov
     *         Builder class.
     */
    public static class DeleteModelRequestContextBuilder {

        /**
         * Entity IDs to delete..
         */
        private List<String> entitiesIds = Collections.emptyList();

        /**
         * Lookup entity IDs to delete..
         */
        private List<String> lookupEntitiesIds = Collections.emptyList();

        /**
         * Enumeration IDs to delete..
         */
        private List<String> enumerationIds = Collections.emptyList();

        /**
         * Source system IDs to delete..
         */
        private List<String> sourceSystemIds = Collections.emptyList();

        /**
         * Relation IDs.
         */
        private List<String> relationIds = Collections.emptyList();

        /**
         * Nested entities Ids to delete
         */
        private List<String> nestedEntitiesIds = Collections.emptyList();

        /**
         * Storage ID to apply the updates to.
         */
        private String storageId;

        /**
         * Constructor.
         */
        public DeleteModelRequestContextBuilder() {
            super();
        }

        /**
         * Sets entitiesIds.
         *
         * @param entitiesIds the ids to set
         * @return self
         */
        public DeleteModelRequestContextBuilder entitiesIds(List<String> entitiesIds) {
            this.entitiesIds = entitiesIds;
            return this;
        }

        /**
         * Sets lookupEntitiesIds.
         *
         * @param lookupEntitiesIds the ids to set
         * @return self
         */
        public DeleteModelRequestContextBuilder lookupEntitiesIds(List<String> lookupEntitiesIds) {
            this.lookupEntitiesIds = lookupEntitiesIds;
            return this;
        }

        /**
         * Sets enumerationIds.
         *
         * @param enumerationIds the ids to set
         * @return self
         */
        public DeleteModelRequestContextBuilder enumerationIds(List<String> enumerationIds) {
            this.enumerationIds = enumerationIds;
            return this;
        }

        /**
         * Sets sourceSystemIds.
         *
         * @param sourceSystemIds the ids to set
         * @return self
         */
        public DeleteModelRequestContextBuilder sourceSystemIds(List<String> sourceSystemIds) {
            this.sourceSystemIds = sourceSystemIds;
            return this;
        }

        /**
         * @param relationIds
         * @return
         */
        public DeleteModelRequestContextBuilder relationIds(List<String> relationIds) {
            this.relationIds = relationIds;
            return this;
        }

        /**
         * Sets storage ID.
         *
         * @param storageId the ID
         * @return self
         */
        public DeleteModelRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }

        /**
         * @param nestedEntitiesIds the IDs of nested entities
         * @return self
         */
        public DeleteModelRequestContextBuilder nestedEntiesIds(List<String> nestedEntitiesIds) {
            this.nestedEntitiesIds = nestedEntitiesIds;
            return this;
        }

        /**
         * Builder method.
         *
         * @return context
         */
        public DeleteModelRequestContext build() {
            return new DeleteModelRequestContext(this);
        }
    }
}