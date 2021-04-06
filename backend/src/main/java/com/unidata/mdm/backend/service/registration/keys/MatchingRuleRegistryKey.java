package com.unidata.mdm.backend.service.registration.keys;

import javax.annotation.Nonnull;

/**
 * matching rule registry key
 */
public class MatchingRuleRegistryKey implements UniqueRegistryKey {

    /**
     * Rule id
     */
    @Nonnull
    private final Integer ruleId;

    /**
     * @param ruleId - rule id
     */
    public MatchingRuleRegistryKey(@Nonnull Integer ruleId) {
        this.ruleId = ruleId;
    }

    @Nonnull
    public Integer getRuleId() {
        return ruleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchingRuleRegistryKey)) return false;

        MatchingRuleRegistryKey that = (MatchingRuleRegistryKey) o;

        return ruleId.equals(that.ruleId);

    }

    @Override
    public int hashCode() {
        return ruleId.hashCode();
    }

    @Override
    public Type keyType() {
        return Type.MATCHING_RULE;
    }

    @Override
    public String toString() {
        return "{" +
                "ruleId=" + ruleId +
                '}';
    }
}
