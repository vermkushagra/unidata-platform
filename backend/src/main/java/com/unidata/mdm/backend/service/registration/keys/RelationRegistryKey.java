package com.unidata.mdm.backend.service.registration.keys;

import javax.annotation.Nonnull;

/**
 * Unique key of relation help identifying relation
 */
public class RelationRegistryKey implements UniqueRegistryKey {

    @Nonnull
    private final String relName;

    public RelationRegistryKey(@Nonnull String relName) {
        this.relName = relName;
    }

    @Nonnull
    public String getRelName() {
        return relName;
    }

    @Override
    public Type keyType() {
        return Type.RELATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RelationRegistryKey that = (RelationRegistryKey) o;

        return relName.equals(that.relName);

    }

    @Override
    public int hashCode() {
        return relName.hashCode();
    }

    @Override
    public String toString() {
        return "{" +
                "relName='" + relName + '\'' +
                '}';
    }
}
