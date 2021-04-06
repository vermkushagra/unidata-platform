package com.unidata.mdm.backend.dao;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.po.matching.MatchingAlgorithmPO;
import com.unidata.mdm.backend.po.matching.MatchingRulePO;

/**
 * Data access object for matching rules {@link MatchingRulePO}
 * Support CRUD operations
 */
public interface MatchingRuleDao {
    @Nonnull
    MatchingRulePO save(@Nonnull MatchingRulePO matchingRule);

    @Nonnull
    MatchingRulePO update(@Nonnull MatchingRulePO matchingRule);

    void delete(@Nonnull Integer id);

    @Nonnull
    Collection<MatchingRulePO> getByEntityName(@Nonnull String entityName);

    @Nullable
    MatchingRulePO getById(@Nonnull Integer id);

    @Nullable
    MatchingRulePO getByEntityNameAndRuleName(@Nonnull String entityName, @Nonnull String ruleName);

    @Nonnull
    Collection<MatchingAlgorithmPO> getAlgorithmsByRuleId(@Nonnull Integer ruleId);

    @Nonnull
    Collection<MatchingRulePO> getAll();
}
