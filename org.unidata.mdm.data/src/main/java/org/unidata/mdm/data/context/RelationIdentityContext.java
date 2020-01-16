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
package org.unidata.mdm.data.context;

import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.keys.LSN;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.system.context.StorageId;

/**
 * @author Mikhail Mikhailov
 * Adds some relation keys to identity context.
 */
public interface RelationIdentityContext extends RecordIdentityContext {
    /**
     * Relation keys SID.
     */
    StorageId SID_RELATION_KEYS = new StorageId("RELATION_KEYS");
    /**
     * The from record key SID.
     */
    StorageId SID_FROM_KEYS = new StorageId("FROM_KEYS");
    /**
     * Relation name SID.
     */
    StorageId SID_RELATION_NAME =  new StorageId("RELATION_NAME");
    /**
     * Relation type SID.
     */
    StorageId SID_RELATION_TYPE =  new StorageId("RELATION_TYPE");
    /**
     * Gets the relation etalon id.
     * @return the relationEtalonKey
     */
    String getRelationEtalonKey();
    /**
     * Local sequence number supplied by request. May be null.
     * @return the number or null
     */
    @Nullable
    Long getRelationLsn();
    /**
     * Shard number. Part of LSN. May be null.
     * @return the shard number
     */
    @Nullable
    Integer getRelationShard();
    /**
     * The LSN object at whole or null, if nothing is set.
     * @return LSN object or null
     */
    @Nullable
    LSN getRelationLsnAsObject();
    /**
     * Gets the relation origin id.
     * @deprecated The field is deprecated. Resolution by this field is turned off, since this field doesn't support sharded layout.
     * @return the relationOriginKey
     */
    @Deprecated
    String getRelationOriginKey();
    /**
     * Gets relation keys from context storage.
     * @return keys or null if not set
     */
    default RelationKeys relationKeys() {
        return getFromStorage(SID_RELATION_KEYS);
    }
    /**
     * Gets the keys id.
     * @return keys id
     */
    default void relationKeys(RelationKeys keys) {
        putToStorage(SID_RELATION_KEYS, keys);
    }
    /**
     * Gets from record keys from context storage.
     * @return keys or null if not set
     */
    default RecordKeys fromKeys() {
        return getFromStorage(SID_FROM_KEYS);
    }
    /**
     * Sets the from key.
     * @return keys the keys
     */
    default void fromKeys(RecordKeys keys) {
        putToStorage(SID_FROM_KEYS, keys);
    }
    /**
     * Gets resolved relation name.
     * @return name
     */
    default String relationName() {
        return getFromStorage(SID_RELATION_NAME);
    }
    /**
     * Put resolved relation name.
     * @param name the name
     */
    default void relationName(String name) {
        putToStorage(SID_RELATION_NAME, name);
    }
    /**
     * Gets resolved relation type.
     * @return name
     */
    default RelationType relationType() {
        return getFromStorage(SID_RELATION_TYPE);
    }
    /**
     * Put resolved relation type.
     * @param type relation type
     */
    default void relationType(RelationType type) {
        putToStorage(SID_RELATION_TYPE, type);
    }
    /**
     * Tells, whether this context is identified by relation etalon id.
     * @return true if so, false otherwise
     */
    default boolean isRelationEtalonKey() {
        return StringUtils.isNotBlank(getRelationEtalonKey());
    }
    /**
     * Checks for LSN identifier being present.
     * @return true, if so, false otherwise
     */
    default boolean isRelationLsnKey() {
        return Objects.nonNull(getRelationLsnAsObject());
    }
    /**
     * Tells, whether this context is identified by relation origin id.
     * @return true if so, false otherwise
     */
    @Deprecated
    default boolean isRelationOriginKey() {
        return StringUtils.isNotBlank(getRelationOriginKey());
    }
    /**
     * Context is generally usable.
     * @return true if so, false otherwise
     */
    default boolean isValidRelationKey() {
        return this.isRelationEtalonKey()
            || this.isRelationLsnKey()
            || this.isRelationOriginKey();
    }
}
