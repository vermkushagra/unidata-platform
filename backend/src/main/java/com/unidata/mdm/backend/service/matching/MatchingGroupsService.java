package com.unidata.mdm.backend.service.matching;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;

/**
 * Responsible for working with matching groups  {@link MatchingGroup}
 */
public interface MatchingGroupsService extends AfterContextRefresh {

    /**
     * @param entityName - entity name
     * @return - collection of group ids
     */
    @Nonnull
    Collection<Integer> getGroupIds(@Nonnull String entityName);

    /**
     * @param matchingGroup - new matching group
     */
    @Nonnull
    MatchingGroup saveMatchingGroup(@Nonnull MatchingGroup matchingGroup);

    /**
     * @param matchingGroup - updated matching group
     */
    @Nonnull
    MatchingGroup updateMatchingGroup(@Nonnull MatchingGroup matchingGroup);

    /**
     * Does upsert of a collection of groups.
     * @param matchingGroups the groups to upsert
     * @return upserted
     */
    @Nonnull
    Collection<MatchingGroup> upsertMatchingGroups(@Nonnull Collection<MatchingGroup> matchingGroups);

    /**
     * @param id - matching rule id
     */
    void removeMatchingGroup(int id);

    /**
     * @param entityName - entity name
     * @return collection of matching groups
     */
    @Nonnull
    Collection<MatchingGroup> getMatchingGroupsByEntityName(@Nonnull String entityName);

    /**
     * @param entityName - entity name
     * @param groupName  - group Name
     * @return matching group
     */
    @Nullable
    MatchingGroup getMatchingGroup(@Nonnull String entityName, @Nonnull String groupName);

    /**
     * @return collection of matching groups
     */
    @Nonnull
    Collection<MatchingGroup> getAllGroups();

    /**
     * @param id - matching rule id
     * @return related with id matching group
     */
    @Nullable
    MatchingGroup getMatchingGroupsById(int id);

    /**
     * (Re-) Loads all groups from persistent storage.
     * @param entityNames them entity names to process. Null means all.
     * @param register whether to register groups in the global registry
     */
    public void loadGroups(@Nullable Collection<String> entityNames, boolean register);
}
