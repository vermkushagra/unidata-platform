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

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.keys.RelationKeys;

/**
 * @author Mikhail Mikhailov
 * Adds some relation keys to identity context.
 */
public interface RelationIdentityContext extends RecordIdentityContext {
    /**
     * Gets the relation etalon id.
     * @return the relationEtalonKey
     */
    String getRelationEtalonKey();
    /**
     * Gets the relation origin id.
     * @return the relationOriginKey
     */
    String getRelationOriginKey();
    /**
     * Gets relation keys from context storage.
     * @return keys or null if not set
     */
    RelationKeys relationKeys();
    /**
     * Gets the keys id.
     * @return keys id
     */
    default StorageId relationKeysId() {
        return StorageId.RELATIONS_RELATION_KEY;
    }
    /**
     * Tells, whether this context is identified by relation etalon id.
     * @return true if so, false otherwise
     */
    default boolean isRelationEtalonKey() {
        return !StringUtils.isBlank(getRelationEtalonKey())
             && StringUtils.isBlank(getRelationOriginKey());
    }
    /**
     * Tells, whether this context is identified by relation origin id.
     * @return true if so, false otherwise
     */
    default boolean isRelationOriginKey() {
        return StringUtils.isBlank(getRelationEtalonKey())
            && !StringUtils.isBlank(getRelationOriginKey());
    }
    /**
     * Context is generally usable.
     * @return true if so, false otherwise
     */
    default boolean isValidRelationKey() {
        return this.isRelationEtalonKey()
            || this.isRelationOriginKey();
    }
}
