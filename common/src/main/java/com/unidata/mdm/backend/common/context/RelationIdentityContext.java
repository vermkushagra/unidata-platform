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
