package com.unidata.mdm.backend.service.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.registration.keys.MatchingGroupRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.MatchingRuleRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

@Service
public class MatchingMetaFacadeServiceImpl implements MatchingMetaFacadeService {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingMetaFacadeServiceImpl.class);

    /**
     * Matching group service
     */
    @Autowired
    private MatchingGroupsService matchingGroupsService;

    /**
     * Matching rule service
     */
    @Autowired
    private MatchingRulesService matchingRulesService;

    @Autowired
    private RegistrationService registrationService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUserSettings(MatchingUserSettings matchingUserSettings) {
        LOGGER.info("Import of matching user settings");
        upsertUserSettings(matchingUserSettings);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importUserSettings(MatchingUserSettings matchingUserSettings) {
        LOGGER.info("Import of matching user settings from zip file");
        matchingRulesService.afterContextRefresh();
        matchingGroupsService.afterContextRefresh();
        upsertUserSettings(matchingUserSettings);
    }
    /**
     * Does actually upsert rules and groups.
     * @param matchingUserSettings the settings to apply
     */
    private void upsertUserSettings(MatchingUserSettings matchingUserSettings) {

        Collection<MatchingRule> matchingRules = Collections.emptyList();
        Collection<MatchingGroup> matchingGroups = Collections.emptyList();
        try {
            matchingRules = matchingRulesService.upsertMatchingRules(matchingUserSettings.getMatchingRules());
            matchingGroups = matchingGroupsService.upsertMatchingGroups(matchingUserSettings.getMatchingGroups());
        } catch (SystemRuntimeException be) {
            rollback(matchingRules, matchingGroups);
            throw be;
        } catch (Exception e) {
            String errorMessage = "Something went wrong while persisting user matching settings.";
            LOGGER.error(errorMessage, e);
            rollback(matchingRules, matchingGroups);
            throw new BusinessException(errorMessage, ExceptionId.EX_MATCHING_USER_SETTINGS_INCORRECT);
        }
    }

    private void rollback(Collection<MatchingRule> matchingRules, Collection<MatchingGroup> matchingGroups) {

        Collection<UniqueRegistryKey> keys = new ArrayList<>(
                (CollectionUtils.isNotEmpty(matchingRules) ? matchingRules.size() : 0) +
                (CollectionUtils.isNotEmpty(matchingGroups) ? matchingGroups.size() : 0));

        if (CollectionUtils.isNotEmpty(matchingGroups)) {
            matchingGroups.stream()
                .map(MatchingGroup::getId)
                .filter(Objects::nonNull)
                .map(MatchingGroupRegistryKey::new)
                .collect(Collectors.toCollection(() -> keys));
        }

        if (CollectionUtils.isNotEmpty(matchingRules)) {
            matchingRules.stream()
                 .map(MatchingRule::getId)
                 .filter(Objects::nonNull)
                 .map(MatchingRuleRegistryKey::new)
                 .collect(Collectors.toCollection(() -> keys));
        }

        if (CollectionUtils.isNotEmpty(keys)) {
            registrationService.batchRemove(keys);
        }
    }
}
