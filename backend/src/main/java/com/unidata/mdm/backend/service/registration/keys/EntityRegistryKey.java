package com.unidata.mdm.backend.service.registration.keys;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Unique key of entity help identifying entity
 */
public class EntityRegistryKey implements UniqueRegistryKey, Serializable {

    /**
     * entity name
     */
    @Nonnull
    private final String entityName;

    /**
     * constructor
     *
     * @param entityName - entity name
     */
    public EntityRegistryKey(@Nonnull String entityName) {
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
        if (!(o instanceof EntityRegistryKey)) return false;

        EntityRegistryKey that = (EntityRegistryKey) o;

        return entityName.equals(that.entityName);

    }

    @Override
    public int hashCode() {
        return entityName.hashCode();
    }

    @Override
    public Type keyType() {
        return Type.ENTITY;
    }

    @Override
    public String toString() {
        return "{" +
                "entityName='" + entityName + '\'' +
                '}';
    }
}
