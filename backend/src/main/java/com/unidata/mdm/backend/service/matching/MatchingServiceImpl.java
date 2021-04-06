package com.unidata.mdm.backend.service.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

@Service
public class MatchingServiceImpl implements MatchingService {
    /**
     * Matching group service
     */
    @Autowired
    private MatchingGroupsService matchingGroupsService;

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
    public Collection<Cluster> construct(@Nonnull EtalonRecord etalon, @Nonnull Date date) {
        return constructInner(etalon, date, false);
    }

    @Override
    public Collection<Cluster> construct(@Nonnull EtalonRecord etalon, @Nonnull Date date, Integer groupId, Integer ruleId) {

        if(groupId == null){
            return constructInner(etalon, date,
                    matchingGroupsService.getMatchingGroupsByEntityName(etalon.getInfoSection().getEntityName()));
        }

        MatchingGroup group = matchingGroupsService.getMatchingGroupsById(groupId);
        if(group == null){
            throw new SystemRuntimeException("Can't find a group with id " + groupId,
                    ExceptionId.EX_MATCHING_GROUP_OR_RULE_NOT_FOUND, groupId);
        }
        group = group.copy();

        if(ruleId != null){
            group.getRules().removeIf(matchingRule -> !matchingRule.getId().equals(ruleId));
            if(CollectionUtils.isEmpty(group.getRules())){
                throw new SystemRuntimeException("Can't find a rule with id " + ruleId,
                        ExceptionId.EX_MATCHING_GROUP_OR_RULE_NOT_FOUND, ruleId);
            }
        }

        return constructInner(etalon, date, Collections.singletonList(group));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Cluster> constructAutoMerge(@Nonnull EtalonRecord etalon, @Nonnull Date date) {
        return constructInner(etalon, date, true);
    }

    private Collection<Cluster> constructInner(@Nonnull EtalonRecord etalon, @Nonnull Date date, boolean onlyAutoMerge) {
        if (etalon == null) {
            return Collections.emptyList();
        }

        String entityName = etalon.getInfoSection().getEntityName();
        Collection<MatchingGroup> groups = matchingGroupsService.getMatchingGroupsByEntityName(entityName);

        // 1. Check defined groups. Return on no groups.
        if (groups.isEmpty()) {
            return Collections.emptyList();
        }

        if (onlyAutoMerge) {
            groups.removeIf(matchingGroup -> !matchingGroup.isAutoMerge());
        }

        return constructInner(etalon, date, groups);
    }

    private Collection<Cluster> constructInner(@Nonnull EtalonRecord etalon, @Nonnull Date date, @NotNull Collection<MatchingGroup> groups) {
        MeasurementPoint.start();
        try {
            String entityName = etalon.getInfoSection().getEntityName();
            Collection<Cluster> clusters = new ArrayList<>();
            for (MatchingGroup group : groups) {

                for (MatchingRule matchingRule : group.getRules()) {

                    List<Object> values = matchingRule.constuct(etalon);
                    if (CollectionUtils.isEmpty(values)) {
                        continue;

                    }

                    ClusterMetaData metaData = ClusterMetaData.builder()
                            .ruleId(matchingRule.getId())
                            .groupId(group.getId())
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
    public Collection<Cluster> construct(@Nonnull String etalonId, @Nonnull Date date) {
        EtalonRecord etalonRecord = etalonComponent.loadEtalonData(etalonId, date, null, null, null, false, false);
        if (etalonRecord == null) {
            return Collections.emptyList();
        }
        return construct(etalonRecord, date);
    }
}
