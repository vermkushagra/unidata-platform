package com.unidata.mdm.backend.service.matching;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
import com.unidata.mdm.backend.dao.MatchingRuleDao;
import com.unidata.mdm.backend.notification.notifiers.MatchingRulesChangesNotifier;
import com.unidata.mdm.backend.po.matching.MatchingAlgorithmPO;
import com.unidata.mdm.backend.po.matching.MatchingRulePO;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.registration.keys.AttributeRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.EntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.MatchingRuleRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

@Service
public class MatchingRuleServiceImpl implements MatchingRulesService {
    /**
     * Matching rule dao
     */
    @Autowired
    private MatchingRuleDao matchingRuleDao;
    /**
     * Conversion service
     */
    @Autowired
    private ConversionService conversionService;
    /**
     * algorithm service
     */
    @Autowired
    private MatchingAlgorithmService algorithmService;
    /**
     * Registration service
     */
    @Autowired
    private RegistrationService registrationService;
    /**
     * Cluster service
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * HZ notifyer.
     */
    @Autowired
    private MatchingRulesChangesNotifier matchingRulesChangesNotifier;
    /**
     * Assignments to keep in memory.
     */
    private ConcurrentHashMap<String, Map<Integer, MatchingRule>> rules = new ConcurrentHashMap<>();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchingRule saveMatchingRule(@Nonnull MatchingRule matchingRule) {
        Collection<MatchingRule> result = upsertMatchingRules(Collections.singletonList(matchingRule));
        return CollectionUtils.isNotEmpty(result) ? result.iterator().next() : null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchingRule updateMatchingRule(@Nonnull MatchingRule matchingRule) {
        Collection<MatchingRule> result = upsertMatchingRules(Collections.singletonList(matchingRule));
        return CollectionUtils.isNotEmpty(result) ? result.iterator().next() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Collection<MatchingRule> upsertMatchingRules(Collection<MatchingRule> rulesUpdate) {

        if (CollectionUtils.isNotEmpty(rulesUpdate)) {

            Set<String> entityNames = new HashSet<>();
            for (MatchingRule matchingRule : rulesUpdate) {

                matchingRule.getMatchingAlgorithms().forEach(algo -> algo.addFetchFunction(algorithmService::getAlgorithmById));

                // 1. Check completeness
                Collection<ValidationResult> validations = matchingRule.checkCompleteness();
                if (!validations.isEmpty()) {
                    throw new MatchingValidationException("Rule is incorrect", ExceptionId.EX_MATCHING_RULE_INCORRECT,
                            validations);
                }

                // 2. Check attribute links
                checkAttributes(matchingRule);

                // 3. Check for being an update
                if (Objects.isNull(matchingRule.getId())) {
                    MatchingRule old = getMatchingRule(matchingRule.getEntityName(), matchingRule.getName());
                    if (Objects.nonNull(old)) {
                        matchingRule.setId(old.getId());
                    }
                }

                MatchingRulePO po = conversionService.convert(matchingRule, MatchingRulePO.class);
                if (Objects.nonNull(matchingRule.getId())) {
                    po = matchingRuleDao.update(po);
                    clusterService.dropEveryThingForRule(matchingRule.getEntityName(), matchingRule.getId());
                } else {
                    po = matchingRuleDao.save(po);
                }

                matchingRule.setId(po.getId());
                entityNames.add(matchingRule.getEntityName());
            }

            loadRules(entityNames, true);
            matchingRulesChangesNotifier.notifyMatchingRulesChanged(entityNames);

            return rulesUpdate;
        }

        return Collections.emptyList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeMatchingRule(int id) {

        MatchingRule rule = getMatchingRule(id);
        if (Objects.nonNull(rule)) {
            clusterService.dropEveryThingForGroup(rule.getEntityName(), rule.getId());
            registrationService.remove(new MatchingRuleRegistryKey(id));
            matchingRuleDao.delete(id);
            loadRules(Collections.singleton(rule.getEntityName()), true);
            matchingRulesChangesNotifier.notifyMatchingRulesChanged(Collections.singleton(rule.getEntityName()));
        }
    }

    @Nonnull
    @Override
    public Collection<MatchingRule> getMatchingRulesByEntityName(@Nonnull String entityName) {

        Map<Integer, MatchingRule> assignments = rules.get(entityName);
        if (!MapUtils.isEmpty(assignments)) {
            return assignments.values();
        }

        return Collections.emptyList();
    }

    @Nullable
    @Override
    public MatchingRule getMatchingRule(@Nonnull String entityName, @Nonnull String ruleName) {

        Map<Integer, MatchingRule> assignments = rules.get(entityName);
        if (!MapUtils.isEmpty(assignments)) {
            return assignments.values().stream()
                    .filter(r -> r.getName().equals(ruleName))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @Nonnull
    @Override
    public Collection<MatchingRule> getAllRules() {

        return rules.values().stream()
            .map(Map::values)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public MatchingRule getMatchingRule(int id) {

        for (Entry<String, Map<Integer, MatchingRule>> mre : rules.entrySet()) {

            MatchingRule hit = mre.getValue().get(id);
            if (Objects.nonNull(hit)) {
                return hit;
            }
        }

        return null;
    }

    @Override
    public void afterContextRefresh() {
        loadRules(null, true);
    }

    /**
     * Loads rules, currently defined in the DB.
     */
    @Override
    public void loadRules(@Nullable Collection<String> entityNames, boolean register) {

        Collection<MatchingRule> payload = new ArrayList<>();
        if (Objects.isNull(entityNames)) {

            payload.addAll(matchingRuleDao.getAll()
                    .stream()
                    .map(po -> conversionService.convert(po, MatchingRule.class))
                    .filter(Objects::nonNull)
                    .collect(toList()));

            rules.clear();
        } else {

            for (String name : entityNames) {

                payload.addAll(matchingRuleDao.getByEntityName(name)
                        .stream()
                        .map(po -> conversionService.convert(po, MatchingRule.class))
                        .filter(Objects::nonNull)
                        .collect(toList()));

                Map<Integer, MatchingRule> entityRules = rules.get(name);
                if (Objects.nonNull(entityRules)) {
                    entityRules.clear();
                }
            }
        }

        payload.forEach(r -> {

            Map<Integer, MatchingRule> entityRules = rules.get(r.getEntityName());
            if (Objects.isNull(entityRules)) {
                entityRules = new HashMap<>();
                rules.put(r.getEntityName(), entityRules);
            }

            r.setMatchingAlgorithms(loadAlgorithms(r.getId()));
            if (register) {
                registerRule(r);
            }

            entityRules.put(r.getId(), r);
        });
    }

    /**
     * Checks for all entity attributes, used by algorithms in a rule, being present in the registry.
     * @param matchingRule the rule to check
     */
    private void checkAttributes(MatchingRule matchingRule) {

        Collection<UniqueRegistryKey> keys = matchingRule.getMatchingAlgorithms().stream()
             .map(MatchingAlgorithm::getMatchingFields)
             .flatMap(Collection::stream)
             .filter(matchingField -> !matchingField.isConstantField())
             .map(field -> new AttributeRegistryKey(field.getAttrName(),
                     matchingRule.getEntityName()))
             .collect(Collectors.toSet());

        boolean allPresent = registrationService.isAllKeysPresented(keys);
        if (!allPresent) {
            throw new BusinessException("Some entity attributes are not present.",
                    ExceptionId.EX_MATCHING_RULE_CONTAIN_UNAVAILABLE_ATTRIBUTE, matchingRule.getName(),
                    matchingRule.getEntityName());
        }
    }

    /**
     * Loads algorithms, currently defined for a rule
     * @param ruleId the rule id
     * @return collection of algorithms
     */
    private Collection<MatchingAlgorithm> loadAlgorithms(Integer ruleId) {

        Collection<MatchingAlgorithmPO> pos = matchingRuleDao.getAlgorithmsByRuleId(ruleId);
        return pos.stream()
                .map(po -> conversionService.convert(po, MatchingAlgorithm.class))
                .filter(Objects::nonNull)
                .map(rule -> rule.addFetchFunction(algorithmService::getAlgorithmById))
                .collect(toList());
    }

    /**
     * Registers a rule in reference chains.
     * @param matchingRule the rule to register
     */
    private void registerRule(@Nullable MatchingRule matchingRule) {

        if (matchingRule == null) {
            return;
        }

        MatchingRuleRegistryKey registryKey = new MatchingRuleRegistryKey(matchingRule.getId());
        Set<UniqueRegistryKey> keys = new HashSet<>();
        keys.add(new EntityRegistryKey(matchingRule.getEntityName()));
        matchingRule.getMatchingAlgorithms()
                .stream()
                .sequential()
                .map(MatchingAlgorithm::getMatchingFields)
                .flatMap(Collection::stream)
                .map(field -> new AttributeRegistryKey(field.getAttrName(), matchingRule.getEntityName()))
                .collect(Collectors.toCollection(() -> keys));

        registrationService.registry(registryKey, keys, Collections.emptySet());
    }
}
