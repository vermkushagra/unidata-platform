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

import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.core.type.keys.LSN;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;

/**
 * @author Mikhail Mikhailov
 *
 * Record identifying context.
 * Fields are used for record identification.
 * The identity is available as {@link RecordKeys} in case of success.
 */
public interface RecordIdentityContext extends StorageCapableContext {
    /**
     * Record keys SID.
     */
    StorageId SID_RECORD_KEYS = new StorageId("RECORD_KEYS");
    /**
     * Resolved record keys.
     * Null for new and invalid records.
     * @return {@link RecordKeys} instance
     */
    @Nullable
    default RecordKeys keys() {
        return getFromStorage(SID_RECORD_KEYS);
    }
    /**
     * Puts the record keys.
     * @param keys the keys to save
     */
    default void keys(RecordKeys keys) {
        putToStorage(SID_RECORD_KEYS, keys);
    }
    /**
     * System etalon key, supplied by request for identification. May be null.
     * @return the etalon key
     */
    @Nullable
    String getEtalonKey();
    /**
     * System origin key, supplied by request for identification. May be null.
     * @deprecated The field is deprecated. Resolution by this field is turned off, since this field doesn't support sharded layout.
     * @return the origin key
     */
    @Deprecated
    @Nullable
    String getOriginKey();
    /**
     * Returns source system external identifier supplied by request for identification.
     * Part of origin key.
     * May be null.
     * @return the source system external identifier
     */
    @Nullable
    String getExternalId();
    /**
     * Returns the entity (register / dictionary) identifier supplied by request for identification.
     * Part of origin key.
     * May be null.
     * @return the entity (register / dictionary) identifier
     */
    @Nullable
    String getEntityName();
    /**
     * Returns the source system name supplied by request for identification.
     * Part of origin key.
     * May be null.
     * @return the source system name
     */
    @Nullable
    String getSourceSystem();
    /**
     * Gets external id as object
     * @return external id as object or null
     */
    @Nullable
    ExternalId getExternalIdAsObject();
    /**
     * Local sequence number supplied by request. May be null.
     * @return the number or null
     */
    @Nullable
    Long getLsn();
    /**
     * Shard number. Part of LSN. May be null.
     * @return the shard number
     */
    @Nullable
    Integer getShard();
    /**
     * The LSN object at whole or null, if nothing is set.
     * @return LSN object or null
     */
    @Nullable
    LSN getLsnAsObject();
    /**
     * Context is usable.
     * @return true if so, false otherwise
     */
    default boolean isValidRecordKey() {
        return this.isEtalonRecordKey()
            || this.isOriginExternalId()
            || this.isLsnKey()
            || this.isOriginRecordKey()
            || this.isEnrichmentKey();
    }
    /**
     * The context is based on an etalon key.
     * @return true if so, false otherwise
     */
    default boolean isEtalonRecordKey() {
        return StringUtils.isNotBlank(getEtalonKey());
    }
    /**
     * The context is based on an origin key.
     * @return true if so, false otherwise
     */
    @Deprecated
    default boolean isOriginRecordKey() {
        return StringUtils.isNotBlank(getOriginKey());
    }
    /**
     * The context is based on an external id, source system and entity name combination.
     * @return true if so, false otherwise
     */
    default boolean isOriginExternalId() {
        return Objects.nonNull(getExternalIdAsObject()) && getExternalIdAsObject().isValid();
    }
    /**
     * Checks for GSN identifier being present.
     * @return true, if so, false otherwise
     */
    default boolean isLsnKey() {
        return Objects.nonNull(getLsnAsObject());
    }
    /**
     * The context has a special enrichment identity.
     * @return true if the context is an enrichment, false otherwise
     */
    default boolean isEnrichmentKey() {
        return false;
    }
}
