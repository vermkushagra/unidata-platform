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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mikhail Mikhailov
 * Get list of golden records.
 */
public class GetMultipleRequestContext extends CommonRequestContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -4403636926092487309L;
    /**
     * Get contexts
     */
    private final List<GetRequestContext> contexts;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * For a particular date (as of).
     */
    private final Date forDate;
    /**
     * lud
     */
    private final Date lastUpdateDate;
    /**
     * Constructor.
     */
    private GetMultipleRequestContext(GetMultipleRequestContextBuilder b) {
        super();
        this.entityName = b.entityName;
        this.forDate = b.forDate;
        this.lastUpdateDate = b.lastUpdateDate;
        this.contexts = b.etalonKeys.stream()
                                    .map(etalonId -> GetRequestContext.builder()
                                                                      .entityName(b.entityName)
                                                                      .etalonKey(etalonId)
                                                                      .fetchClassifiers(b.fetchClassifiers)
                                                                      .fetchRelations(b.fetchRelations)
                                                                      .forDate(b.forDate)
                                                                      .forLastUpdate(b.lastUpdateDate)
                                                                      .build())
                                    .collect(Collectors.toList());
        this.contexts.forEach(ct -> ct.setOperationId(getOperationId()));
    }

    /**
     * @return the etalonKeys
     */
    public List<GetRequestContext> getInnerGetContexts() {
        return contexts;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the forDate
     */
    public Date getForDate() {
        return forDate;
    }

    /**
     *
     * @return lud
     */
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * The context is based on a golden key.
     * @return true if so, false otherwise
     */
    public boolean isValid() {
        return !this.contexts.isEmpty();
    }

    /**
     * Gets builder.
     * @return builder
     */
    public static GetMultipleRequestContextBuilder builder() {
        return new GetMultipleRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class GetMultipleRequestContextBuilder {
        /**
         * Golden key.
         */
        private List<String> etalonKeys = Collections.emptyList();
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * For a particular date (as of).
         */
        private Date forDate;
        /**
         * lud
         */
        private Date lastUpdateDate;
        /**
         * Return relations or not. False for UI, true for SOAP.
         */
        private boolean fetchRelations;
        /**
         * Return classifiers or not. True for both SOAP and UI.
         */
        private boolean fetchClassifiers;
        /**
         * Constructor.
         */
        public GetMultipleRequestContextBuilder() {
            super();
        }

        /**
         * @param goldenKeys the etalonKeys to set
         */
        public GetMultipleRequestContextBuilder etalonKeys(List<String> goldenKeys) {
            this.etalonKeys = goldenKeys;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public GetMultipleRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param forDate the forDate to set
         */
        public GetMultipleRequestContextBuilder forDate(Date forDate) {
            this.forDate = forDate;
            return this;
        }

        /**
         *
         * @param lud lud
         * @return self
         */
        public GetMultipleRequestContextBuilder lastUpdateDate(Date lud){
            this.lastUpdateDate = lud;
            return this;
        }

        /**
         * @param fetchRelations the fetchRelations to set
         */
        public GetMultipleRequestContextBuilder fetchRelations(boolean fetchRelations) {
            this.fetchRelations = fetchRelations;
            return this;
        }

        /**
         * @param fetchClassifiers the fetchClassifiers to set
         */
        public GetMultipleRequestContextBuilder fetchClassifiers(boolean fetchClassifiers) {
            this.fetchClassifiers = fetchClassifiers;
            return this;
        }

        /**
         * Builds a context.
         * @return a new context
         */
        public GetMultipleRequestContext build() {
            return new GetMultipleRequestContext(this);
        }
    }
}
