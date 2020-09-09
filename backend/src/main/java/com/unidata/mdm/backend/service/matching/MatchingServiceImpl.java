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

import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MatchingServiceImpl implements MatchingService {
    /**
     * Matching group service
     */
    @Autowired
    private MatchingRulesService matchingRulesService;

    @Autowired
    protected HazelcastInstance hazelcastInstance;

    /**
     * Etalon data component.
     */
    @Autowired
    private EtalonRecordsComponent etalonComponent;

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Cluster> constructPreprocessing(@Nonnull EtalonRecord etalon, @Nonnull Date date) {
        return constructInner(etalon, date, false, true);
    }

    @Override
    public Collection<Cluster> construct(@Nonnull EtalonRecord etalon, @Nonnull Date date, Integer ruleId) {

        if(ruleId == null){
            return constructInner(etalon, date,
                    matchingRulesService.getActiveMatchingRulesByEntityName(etalon.getInfoSection().getEntityName()), false, false);
        }

        MatchingRule rule = matchingRulesService.getMatchingRule(ruleId);
        if(rule == null){
            throw new SystemRuntimeException("Can't find a group with id " + ruleId,
                    ExceptionId.EX_MATCHING_GROUP_OR_RULE_NOT_FOUND, ruleId);
        }

        return constructInner(etalon, date, Collections.singletonList(rule), false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Cluster> constructAutoMerge(EtalonRecord etalon, @Nonnull Date date) {
        return constructInner(etalon, date, true, false);
    }

    private Collection<Cluster> constructInner(EtalonRecord etalon, @Nonnull Date date, boolean onlyAutoMerge, boolean onlyPreprocessing) {
        if (etalon == null) {
            return Collections.emptyList();
        }

        String entityName = etalon.getInfoSection().getEntityName();
        Collection<MatchingRule> rules = matchingRulesService.getActiveMatchingRulesByEntityName(entityName);

        // 1. Check defined groups. Return on no groups.
        if (rules.isEmpty()) {
            return Collections.emptyList();
        }

        return constructInner(etalon, date, rules, onlyAutoMerge, onlyPreprocessing);
    }

    private Collection<Cluster> constructInner(@Nonnull EtalonRecord etalon,
                                               @Nonnull Date date,
                                               @NotNull Collection<MatchingRule> rules,
                                               boolean onlyAutoMerge,
                                               boolean onlyPreprocessing) {
        MeasurementPoint.start();
        try {
            String entityName = etalon.getInfoSection().getEntityName();
            Collection<Cluster> clusters = new ArrayList<>();
            for (MatchingRule matchingRule : rules) {
                if(onlyAutoMerge && !matchingRule.isAutoMerge()){
                    continue;
                }

                if(onlyPreprocessing && !matchingRule.isWithPreprocessing()){
                    continue;
                }
                List<Object> values = matchingRule.constuct(etalon);
                if (CollectionUtils.isEmpty(values)) {
                    continue;
                }

                ClusterMetaData metaData = ClusterMetaData.builder()
                        .ruleId(matchingRule.getId())
                        .entityName(entityName)
                        .storage(SecurityUtils.getCurrentUserStorageId())
                        .build();

                List<Object> matchData = matchingRule.constuct(etalon);
                if (CollectionUtils.isEmpty(matchData)) {
                    continue;
                }

                Cluster cluster = new Cluster(date);
                cluster.setData(matchData);
                cluster.setMetaData(metaData);

                clusters.add(cluster);
            }
            return clusters;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Cluster> constructPreprocessing(@Nonnull String etalonId, @Nonnull Date date) {
        EtalonRecord etalonRecord = etalonComponent.loadEtalonData(etalonId, date, null, null, null, false, false);
        if (etalonRecord == null) {
            return Collections.emptyList();
        }
        return constructPreprocessing(etalonRecord, date);
    }


}
