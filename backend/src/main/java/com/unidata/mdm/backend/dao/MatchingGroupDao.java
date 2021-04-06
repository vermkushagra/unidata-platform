package com.unidata.mdm.backend.dao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

import com.unidata.mdm.backend.po.matching.MatchingGroupPO;

/**
 * CRUD data access object for working with {@link MatchingGroupPO}
 */
public interface MatchingGroupDao {

    @Nonnull
    Collection<Integer> getGroupIds(@Nonnull String entityName);

    @Nonnull
    MatchingGroupPO save(@Nonnull MatchingGroupPO matchingRule);

    @Nonnull
    MatchingGroupPO update(@Nonnull MatchingGroupPO matchingRule);

    void delete(@Nonnull Integer id);

    @Nonnull
    Collection<MatchingGroupPO> getByEntityName(@Nonnull String entityName);

    @Nullable
    MatchingGroupPO getById(@Nonnull Integer id);

    @Nullable
    MatchingGroupPO getByEntityNameAndGroupName(@Nonnull String entityName, @Nonnull String groupName);

    @Nonnull
    Collection<MatchingGroupPO> getAll();
}
