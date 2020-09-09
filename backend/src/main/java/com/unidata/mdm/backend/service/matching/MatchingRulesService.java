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

package com.unidata.mdm.backend.service.matching;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;

/**
 * Responsible for working with matching rules {@link MatchingRule}
 */
public interface MatchingRulesService extends AfterContextRefresh {
    /**
     * @param matchingRule - new matching rule
     */
    @Nonnull
    MatchingRule saveMatchingRule(@Nonnull MatchingRule matchingRule);

    /**
     * @param matchingRule - updated matching rule
     */
    @Nonnull
    MatchingRule updateMatchingRule(@Nonnull MatchingRule matchingRule);

    /**
     * Does matching rules upsert
     * @param rules the rules to upsert
     * @return processed rules
     */
    Collection<MatchingRule> upsertMatchingRules(@Nonnull Collection<MatchingRule> rules);

    /**
     * @param id - matching rule id
     */
    void removeMatchingRule(int id);

    /**
     * @param entityName - entity name
     * @return collection of matching rules
     */
    @Nonnull
    Collection<MatchingRule> getMatchingRulesByEntityName(@Nonnull String entityName);

    /**
     * @param entityName - entity name
     * @return collection of matching rules
     */
    @Nonnull
    Collection<MatchingRule> getActiveMatchingRulesByEntityName(@Nonnull String entityName);

    /**
     * @param entityName - entity name
     * @param ruleName   - ruleName
     * @return collection of matching rules
     */
    @Nullable
    MatchingRule getMatchingRule(@Nonnull String entityName, @Nonnull String ruleName);

    /**
     * @param id - matching rule id
     * @return related with id matching rule
     */
    @Nullable
    MatchingRule getMatchingRule(int id);

    /**
     * @return collection of matching rules
     */
    @Nonnull
    Collection<MatchingRule> getAllRules();
    /**
     * (Re-)Loads rules from DB to cache.
     * @param entityNames the entity names to reload rules for. Null means "all rules must be reloaded".
     * @param register TODO
     */
    void loadRules(Collection<String> entityNames, boolean register);
}
