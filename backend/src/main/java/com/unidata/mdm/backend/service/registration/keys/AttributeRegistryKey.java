package com.unidata.mdm.backend.service.registration.keys;

import javax.annotation.Nonnull;

/**
 * Attribute registry key
 */
public class AttributeRegistryKey implements UniqueRegistryKey {

    /**
     * Attribute name
     */
    @Nonnull
    private final String fullAttributeName;

    /**
     * Entity name
     */
    @Nonnull
    private final String entityName;

    /**
     * @param fullAttributeName - full attribute name
     * @param entityName        - entity name
     */
    public AttributeRegistryKey(@Nonnull String fullAttributeName, @Nonnull String entityName) {
        this.entityName = entityName;
        this.fullAttributeName = fullAttributeName;
    }

    @Override
    public Type keyType() {
        return Type.ATTRIBUTE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeRegistryKey)) return false;

        AttributeRegistryKey that = (AttributeRegistryKey) o;

        if (!fullAttributeName.equals(that.fullAttributeName)) return false;
        return entityName.equals(that.entityName);

    }

    @Override
    public int hashCode() {
        int result = fullAttributeName.hashCode();
        result = 31 * result + entityName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "fullAttributeName='" + fullAttributeName + '\'' +
                ", entityName='" + entityName + '\'' +
                '}';
    }

    @Nonnull
    public String getFullAttributeName() {
        return fullAttributeName;
    }

    @Nonnull
    public String getEntityName() {
        return entityName;
    }
}
