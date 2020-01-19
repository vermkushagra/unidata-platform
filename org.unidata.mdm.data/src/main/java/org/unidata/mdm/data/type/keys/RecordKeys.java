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

package org.unidata.mdm.data.type.keys;

import java.io.Serializable;

import org.unidata.mdm.core.type.keys.Keys;

/**
 * @author Mikhail Mikhailov
 * Immutable record key.
 */
public class RecordKeys extends Keys<RecordEtalonKey, RecordOriginKey> implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5712117666167666097L;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Constructor.
     */
    private RecordKeys(RecordKeysBuilder b) {

        super(b);
        if (b.entityName != null) {
            this.entityName =  b.entityName;
        } else {
            this.entityName = this.originKey == null
                ? null
                : this.originKey.getEntityName();
        }
    }
    /**
     * @return the entityName
     */
    public String getEntityName() {
        return this.entityName;
    }
    /**
     * Finds an origin key by external id.
     * @param externalId the external id to match
     * @param entityName the entity name to match
     * @param sourceSystem the source system to match
     * @return key or null, if not found
     */
    public RecordOriginKey findByExternalId(String externalId, String entityName, String sourceSystem) {

        for (RecordOriginKey ok : getSupplementaryKeys()) {
            if (ok.getEntityName().equals(entityName)
             && ok.getExternalId().equals(externalId)
             && ok.getSourceSystem().equals(sourceSystem)) {
                return ok;
            }
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public KeysType getType() {
        return KeysType.RECORD_KEYS;
    }
    /**
     * Builder getter.
     * @return new builder instance
     */
    public static RecordKeysBuilder builder() {
        return new RecordKeysBuilder();
    }
    /**
     * Builder getter.
     * @param keys the keys to copy initial values from
     * @return new builder instance
     */
    public static RecordKeysBuilder builder(RecordKeys keys) {
        return new RecordKeysBuilder(keys);
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class RecordKeysBuilder extends KeysBuilder<RecordKeysBuilder, RecordEtalonKey, RecordOriginKey> {
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * Constructor.
         */
        private RecordKeysBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param keys the keys to copy
         */
        private RecordKeysBuilder(RecordKeys keys) {

            super(keys);
            if (keys.entityName != null) {
                this.entityName = keys.entityName;
            } else {
                this.entityName = this.originKey == null
                    ? null
                    : this.originKey.getEntityName();
            }
        }
        /**
         * @param entityName the entityName to set
         */
        public RecordKeysBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }
        /**
         * @return new keys object
         */
        @Override
        public RecordKeys build() {
            return new RecordKeys(this);
        }
    }
}
