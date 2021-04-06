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
