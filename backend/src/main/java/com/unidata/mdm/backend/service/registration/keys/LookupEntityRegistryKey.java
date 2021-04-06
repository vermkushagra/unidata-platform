package com.unidata.mdm.backend.service.registration.keys;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Unique key of lookup entity help identifying lookup entity
 */
public class LookupEntityRegistryKey implements UniqueRegistryKey {

    /**
     * lookup entity name
     */
    @Nonnull
    private final String entityName;

    /**
     * constructor
     *
     * @param entityName - lookup entity name
     */
    public LookupEntityRegistryKey(@Nonnull String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return entity name
     */
    @Nonnull
    public String getEntityName() {
        return entityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LookupEntityRegistryKey)) return false;

        LookupEntityRegistryKey that = (LookupEntityRegistryKey) o;

        return entityName.equals(that.entityName);

    }

    @Override
    public int hashCode() {
        return entityName.hashCode();
    }

    /**
     * @return type of key
     */
    @Override
    public Type keyType() {
        return Type.LOOKUP_ENTITY;
    }

    @Override
    public String toString() {
        return "{" +
                "entityName='" + entityName + '\'' +
                '}';
    }
}
