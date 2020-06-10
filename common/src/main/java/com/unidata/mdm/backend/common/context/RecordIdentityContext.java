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

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Object identifying context.
 */
public interface RecordIdentityContext {
    /**
     * Resolved and cached keys.
     * @return
     */
    RecordKeys keys();
    /**
     * The id.
     * @return id
     */
    StorageId keysId();
    /**
     * Returns surrogate etalon key. Part of the etalon key.
     * @return the surrogate etalon key
     */
    String getEtalonKey();
    /**
     * Returns the origin surrogate key. Part of the origin key.
     * @return the surrogate key
     */
    String getOriginKey();
    /**
     * Returns source system external identifier. Part of the origin key.
     * @return the source system external identifier
     */
    String getExternalId();
    /**
     * Returns the entity (register / dictionary) identifier. Part of the origin key.
     * @return the entity (register / dictionary) identifier
     */
    String getEntityName();
    /**
     * Returns the source system name. Part of the origin key.
     * @return the source system name
     */
    String getSourceSystem();
    /**
     * Global sequence number.
     * @return the number or null
     */
    default Long getGsn() {
        return null;
    }
    /**
     * Context is usable.
     * @return true if so, false otherwise
     */
    default boolean isValidRecordKey() {
        return this.isEtalonRecordKey()
            || this.isOriginRecordKey()
            || this.isOriginExternalId()
            || this.isEnrichmentKey()
            || this.isGsnKey();
    }
    /**
     * The context is based on an etalon key.
     * @return true if so, false otherwise
     */
    default boolean isEtalonRecordKey() {
        return !StringUtils.isBlank(getEtalonKey());
    }
    /**
     * The context is based on an origin key.
     * @return true if so, false otherwise
     */
    default boolean isOriginRecordKey() {
        return !StringUtils.isBlank(getOriginKey());
    }
    /**
     * The context is based on an external id, source system and entity name combination.
     * @return true if so, false otherwise
     */
    default boolean isOriginExternalId() {
        return !StringUtils.isBlank(getExternalId())
            && !StringUtils.isBlank(getSourceSystem())
            && !StringUtils.isBlank(getEntityName());
    }
    /**
     * The context has a special enrichment identity.
     * @return true if the context is an enrichment, false otherwise
     */
    default boolean isEnrichmentKey() {
        return false;
    }
    /**
     * Checks for GSN identifier being present.
     * @return true, if so, false otherwise
     */
    default boolean isGsnKey() {
        return Objects.nonNull(getGsn());
    }
}
