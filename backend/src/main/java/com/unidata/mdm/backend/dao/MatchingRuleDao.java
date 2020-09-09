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
