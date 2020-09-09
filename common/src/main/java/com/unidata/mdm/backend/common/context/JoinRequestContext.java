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

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 *
 */
public class JoinRequestContext
    extends CommonRequestContext
    implements RecordIdentityContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -9109988572739215092L;
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
     * Constructor.
     */
    private JoinRequestContext(JoinRequestContextBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
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
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(StorageId.DATA_GET_KEYS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.DATA_GET_KEYS;
    }

    /**
     * Builder shorthand.
     * @return builder
     */
    public static JoinRequestContextBuilder builder() {
        return new JoinRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class JoinRequestContextBuilder {

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
         * Constructor.
         */
        public JoinRequestContextBuilder() {
            super();
        }

        /**
         * @param etalonKey the etalonKey to set
         */
        public JoinRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the etalonKey to set
         */
        public JoinRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public JoinRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public JoinRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public JoinRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public JoinRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the goldenKey to set
         */
        public JoinRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * Builds a context.
         * @return a new context
         */
        public JoinRequestContext build() {
            return new JoinRequestContext(this);
        }
    }
}
