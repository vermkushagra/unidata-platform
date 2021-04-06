package com.unidata.mdm.backend.service.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.service.ClusterService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MatchingValidationException;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.dao.MatchingGroupDao;
import com.unidata.mdm.backend.notification.notifiers.MatchingGroupsChangesNotifier;
import com.unidata.mdm.backend.po.matching.MatchingGroupPO;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.registration.keys.EntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.MatchingGroupRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.MatchingRuleRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

@Service
public class MatchingGroupServiceImpl implements MatchingGroupsService {

    /**
     * Matching group dao
     */
    @Autowired
    private MatchingGroupDao matchingGroupDao;
    /**
     * Conversion service
     */
    @Autowired
    private ConversionService conversionService;
    /**
     * Registration service
     */
    @Autowired
    private RegistrationService registrationService;
    /**
     * Matching rule dao
     */
    @Autowired
    private MatchingRulesService matchingRulesService;
    /**
     * Notifier.
     */
    @Autowired
    private MatchingGroupsChangesNotifier matchingGroupsChangesNotifier;
    /**
     * Cluster service
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * Groups, materialized on a single app node.
     */
    private ConcurrentHashMap<String, Map<String, MatchingGroup>> groups = new ConcurrentHashMap<>();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchingGroup saveMatchingGroup(@Nonnull MatchingGroup matchingGroup) {
        Collection<MatchingGroup> result = upsertMatchingGroups(Collections.singletonList(matchingGroup));
        return CollectionUtils.isEmpty(result) ? null : result.iterator().next();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchingGroup updateMatchingGroup(@Nonnull MatchingGroup matchingGroup) {
        Collection<MatchingGroup> result = upsertMatchingGroups(Collections.singletonList(matchingGroup));
        return CollectionUtils.isEmpty(result) ? null : result.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Collection<MatchingGroup> upsertMatchingGroups(@Nonnull Collection<MatchingGroup> matchingGroups) {

        if (CollectionUtils.isNotEmpty(matchingGroups)) {

            Set<String> entityNames = new HashSet<>();
            for (MatchingGroup matchingGroup : matchingGroups) {

                matchingGroup.addFetchFunction(matchingRulesService::getMatchingRule);

                // 1. Check completeness
                Collection<ValidationResult> validationResults = matchingGroup.checkCompleteness();
                if (!validationResults.isEmpty()) {
                    throw new MatchingValidationException("Matching group incorrect", ExceptionId.EX_MATCHING_GROUP_INCORRECT,
                            validationResults);
                }

                // 2. Resolve rules
                checkRules(matchingGroup);

                // 3. Check for needing to clear old data
                MatchingGroup old = getMatchingGroup(matchingGroup.getEntityName(), matchingGroup.getName());
                boolean needClearGroupData = isHasMajorChanges(matchingGroup, old);

                // 4. Check for being an update
                if (Objects.isNull(matchingGroup.getId()) && Objects.nonNull(old)) {
                        matchingGroup.setId(old.getId());
                }

                // 5. Persist
                MatchingGroupPO po = conversionService.convert(matchingGroup, MatchingGroupPO.class);
                if (Objects.nonNull(po.getId())) {
                    po = matchingGroupDao.update(po);

                    if(needClearGroupData){
                        clusterService.dropEveryThingForGroup(matchingGroup.getEntityName(), po.getId());
                    }
                } else {
                    po = matchingGroupDao.save(po);
                }

                matchingGroup.setId(po.getId());
                entityNames.add(matchingGroup.getEntityName());
            }

            loadGroups(entityNames, true);
            matchingGroupsChangesNotifier.notifyMatchingGroupsChanged(entityNames);

            return matchingGroups;
        }

        return Collections.emptyList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeMatchingGroup(int id) {

        MatchingGroup matchingGroup = getMatchingGroupsById(id);
        if (Objects.nonNull(matchingGroup)) {
            clusterService.dropEveryThingForGroup(matchingGroup.getEntityName(), matchingGroup.getId());
            registrationService.remove(new MatchingGroupRegistryKey(id));
            matchingGroupDao.delete(id);
            loadGroups(Collections.singleton(matchingGroup.getEntityName()), true);
            matchingGroupsChangesNotifier.notifyMatchingGroupsChanged(Collections.singleton(matchingGroup.getEntityName()));
        }
    }

    @Nonnull
    @Override
    public Collection<MatchingGroup> getMatchingGroupsByEntityName(@Nonnull String entityName) {
        Map<String, MatchingGroup> assignments = groups.get(entityName);
        if (MapUtils.isNotEmpty(assignments)) {
            return new ArrayList<>(assignments.values());
        }

        return Collections.emptyList();
    }

    @Nullable
    @Override
    public MatchingGroup getMatchingGroup(@Nonnull String entityName, @Nonnull String groupName) {
        Map<String, MatchingGroup> assignments = groups.get(entityName);
        if (MapUtils.isNotEmpty(assignments)) {
            return assignments.get(groupName);
        }

        return null;
    }

    @Nonnull
    @Override
    public Collection<MatchingGroup> getAllGroups() {
        return groups.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public MatchingGroup getMatchingGroupsById(int id) {
        return getAllGroups().stream()
                .filter(g -> g.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Nonnull
    @Override
    public Collection<Integer> getGroupIds(@Nonnull String entityName) {
        return matchingGroupDao.getGroupIds(entityName);
    }

    @Override
    public void afterContextRefresh() {
        loadGroups(null, true);
    }

    @Override
    public void loadGroups(@Nullable Collection<String> entityNames, boolean register) {

        Collection<MatchingGroup> payload = new ArrayList<>();
        if (Objects.isNull(entityNames)) {

            payload.addAll(matchingGroupDao.getAll().stream()
                    .map(po -> conversionService.convert(po, MatchingGroup.class))
                    .filter(Objects::nonNull)
                    .map(r -> r.addFetchFunction(matchingRulesService::getMatchingRule))
                    .collect(Collectors.toList()));

            groups.clear();
        } else {

            for (String entityName : entityNames) {

                payload.addAll(matchingGroupDao.getByEntityName(entityName).stream()
                        .map(po -> conversionService.convert(po, MatchingGroup.class))
                        .filter(Objects::nonNull)
                        .map(r -> r.addFetchFunction(matchingRulesService::getMatchingRule))
                        .collect(Collectors.toList()));

                Map<String, MatchingGroup> existing = groups.get(entityName);
                if (Objects.nonNull(existing)) {
                    existing.clear();
                }
            }
        }

        payload.forEach(g -> {

            Map<String, MatchingGroup> entityGroups = groups.get(g.getEntityName());
            if (Objects.isNull(entityGroups)) {

                entityGroups = new HashMap<>();
                groups.put(g.getEntityName(), entityGroups);
            }

            if (register) {
                registerGroup(g);
            }

            entityGroups.put(g.getName(), g);
        });
    }

    /**
     * Checks rules references
     * @param matchingGroup the matching group to check
     */
    private void checkRules(MatchingGroup matchingGroup) {

        // 1. Check id refs.
        if (CollectionUtils.isNotEmpty(matchingGroup.getRulesIds())) {

            Set<UniqueRegistryKey> uniqueRegistryKeys = matchingGroup.getRulesIds()
                    .stream()
                    .map(MatchingRuleRegistryKey::new)
                    .collect(Collectors.toSet());

            if (!registrationService.isAllKeysPresented(uniqueRegistryKeys)) {
                throw new BusinessException("Group {} references rules by id in entity {}, which were not found.",
                        ExceptionId.EX_MATCHING_GROUP_REFER_TO_UNAVAILABLE_RULE_BY_ID,
                        matchingGroup.getName(), matchingGroup.getEntityName());
            }
        }

        // 2. Check name refs
        if (CollectionUtils.isNotEmpty(matchingGroup.getRulesNames())) {
            List<Integer> ruleIds = new ArrayList<>(matchingGroup.getRulesNames().size());
            for (String ruleName : matchingGroup.getRulesNames()) {

                MatchingRule rule = matchingRulesService.getMatchingRule(matchingGroup.getEntityName(), ruleName);
                if (Objects.isNull(rule)) {
                    throw new BusinessException("Group {} references rule {} in entity {}, which was not found.",
                            ExceptionId.EX_MATCHING_GROUP_REFER_TO_UNAVAILABLE_RULE_BY_NAME,
                            matchingGroup.getName(), ruleName, matchingGroup.getEntityName());
                }

                ruleIds.add(rule.getId());
            }

            if (Objects.isNull(matchingGroup.getRulesIds())) {
                matchingGroup.setRulesIds(ruleIds);
            } else {
                ruleIds.addAll(matchingGroup.getRulesIds());
                matchingGroup.setRulesIds(ruleIds.stream()
                        .distinct()
                        .collect(Collectors.toList()));
            }
        }
    }

    private void registerGroup(@Nullable MatchingGroup matchingGroup) {

        if (matchingGroup == null) {
            return;
        }

        MatchingGroupRegistryKey matchingGroupRegistryKey = new MatchingGroupRegistryKey(matchingGroup.getId());
        Set<UniqueRegistryKey> uniqueRegistryKeys = matchingGroup.getRulesIds()
                .stream()
                .map(MatchingRuleRegistryKey::new)
                .collect(Collectors.toSet());

        uniqueRegistryKeys.add(new EntityRegistryKey(matchingGroup.getEntityName()));
        registrationService.registry(matchingGroupRegistryKey, uniqueRegistryKeys, Collections.emptySet());
    }

    private boolean isHasMajorChanges(MatchingGroup newGroup, MatchingGroup oldGroup){
        return oldGroup != null && newGroup != null
                && !(newGroup.getName().equals(oldGroup.getName())
                && newGroup.getEntityName().equals(oldGroup.getEntityName())
                && ((newGroup.getRulesIds() == null && oldGroup.getRulesIds() == null) ||
                CollectionUtils.isEqualCollection(newGroup.getRulesIds(), oldGroup.getRulesIds())));

    }
}
