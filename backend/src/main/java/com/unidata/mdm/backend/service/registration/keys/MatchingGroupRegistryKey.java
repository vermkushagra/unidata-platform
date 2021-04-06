package com.unidata.mdm.backend.service.registration.keys;

import javax.annotation.Nonnull;

/**
 * Matching group registry key
 */
public class MatchingGroupRegistryKey implements UniqueRegistryKey {

    /**
     * Group id
     */
    @Nonnull
    private final Integer groupId;

    /**
     * @param groupId - group id
     */
    public MatchingGroupRegistryKey(@Nonnull Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchingGroupRegistryKey)) return false;

        MatchingGroupRegistryKey that = (MatchingGroupRegistryKey) o;

        return groupId.equals(that.groupId);

    }

    @Override
    public int hashCode() {
        return groupId.hashCode();
    }

    @Override
    public Type keyType() {
        return Type.MATCHING_GROUP;
    }

    @Nonnull
    public Integer getGroupId() {
        return groupId;
    }

    @Override
    public String toString() {
        return "{" +
                "groupId=" + groupId +
                '}';
    }
}
