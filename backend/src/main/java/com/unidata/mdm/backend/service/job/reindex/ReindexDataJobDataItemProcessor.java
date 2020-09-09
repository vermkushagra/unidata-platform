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

package com.unidata.mdm.backend.service.job.reindex;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContextConfig;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.data.RecordsServiceComponent;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.AbstractEntityDef;

/**
 * @author Denis Kostovarov
 */
@Component("reindexDataJobItemProcessor")
@StepScope
public class ReindexDataJobDataItemProcessor implements ItemProcessor<Pair<Long, String>, IndexRequestContext>, InitializingBean {
    /**
     * Skip data quality
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_SKIP_DQ + "]}")
    private boolean skipDq;
    /**
     * Suppress system checks.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_SUPPRESS_CONSISTENCY_CHECK + "] ?: true }")
    private boolean suppressConsistencyChecks;
    /**
     * Clean types
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_CLEAN_INDEXES + "] ?: false }")
    private boolean indexesAreEmpty;
    /**
     * Job operation id
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_OPERATION_ID + "]}")
    private String operationId;
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private Boolean jobReindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private Boolean jobReindexRelations;
    /**
     * If true, classifiers will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS + "] ?: false}")
    private Boolean jobReindexClassifiers;
    /**
     * If true, matching data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_MATCHING + "] ?: false}")
    private Boolean jobReindexMatching;
    /**
     * Enable jms notifications
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_SKIP_NOTIFICATIONS + "] ?: false}")
    private boolean skipNotifications;
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private Boolean stepReindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private Boolean stepReindexRelations;
    /**
     * If true, classifiers will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS + "] ?: false}")
    private Boolean stepReindexClassifiers;
    /**
     * If true, matching data will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_MATCHING + "] ?: false}")
    private Boolean stepReindexMatching;
    /**
     * Entity name
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_ENTITY_NAME + "]}")
    private String entityName;
    /**
     * Linked with entity relation
     */
    private Map<String, List<String>> relationNames = Collections.emptyMap();

    /**
     * Linked with entity classifiers
     */
    private Map<String, List<String>> classifierNames = Collections.emptyMap();

    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    @Autowired
    private RecordsServiceComponent recordsServiceComponent;

    @Autowired
    private OriginRecordsComponent originRecordsComponent;

    @Override
    public IndexRequestContext process(Pair<Long, String> row) throws Exception {

        Long gsn = row.getKey();
        String name = row.getValue();

        // record from already removed registry
        if (!metaModelService.isEntity(name) && !metaModelService.isLookupEntity(name)) {
            return null;
        }

        GetRequestContext gCtx = GetRequestContext.builder()
                .gsn(gsn)
                .build();

        WorkflowTimelineDTO timeline = null;
        try {
            timeline = originRecordsComponent.loadWorkflowTimeline(gCtx, true);
        } catch (SystemRuntimeException e) { /* NOP */ }

        if (timeline == null || timeline.getIntervals().isEmpty()) {
            return null;
        }

        IndexRequestContextConfig config = IndexRequestContextConfig.builder()
                .operationId(operationId)
                .indexesAreEmpty(indexesAreEmpty)
                .reindexRecords(jobReindexRecords || stepReindexRecords)
                .reindexRelations(jobReindexRelations || stepReindexRelations)
                .reindexClassifiers(jobReindexClassifiers || stepReindexClassifiers)
                .reindexMatching(jobReindexMatching || stepReindexMatching)
                .skipDQ(skipDq)
                .skipNotification(skipNotifications)
                .suppressConsistencyChecks(suppressConsistencyChecks)
                .classifierNames(StringUtils.isNotBlank(entityName)
                        ? classifierNames.get(entityName)
                        : classifierNames.computeIfAbsent(gCtx.keys().getEntityName(), this::getClassifiersForEntity))
                .relationNames(StringUtils.isNotBlank(entityName)
                        ? relationNames.get(entityName)
                        : relationNames.computeIfAbsent(gCtx.keys().getEntityName(), this::getRelationDefsForEntityName))
                .build();

        return recordsServiceComponent.buildIndexRequestContext(config, gCtx.keys(), timeline);
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setJobReindexRecords(Boolean reindexRecords) {
        this.jobReindexRecords = reindexRecords;
    }

    public void setJobReindexRelations(Boolean reindexRelations) {
        this.jobReindexRelations = reindexRelations;
    }

    public void setSkipDq(boolean skipDq) {
        this.skipDq = skipDq;
    }

    private List<String> getRelationDefsForEntityName(String entityName) {

        if (StringUtils.isBlank(entityName)) {
            return Collections.emptyList();
        }

        return metaModelService.getRelationsByFromEntityName(entityName)
                               .stream()
                               .map(AbstractEntityDef::getName)
                               .collect(Collectors.toList());
    }

    private List<String> getClassifiersForEntity(String entityName) {
        if (StringUtils.isBlank(entityName)) {
            return Collections.emptyList();
        }
        return metaModelService.getClassifiersForEntity(entityName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.isBlank(entityName)) {
            relationNames = new HashMap<>();
            classifierNames = new HashMap<>();
        } else {
            List<String> rels = getRelationDefsForEntityName(entityName);
            List<String> classifiers = getClassifiersForEntity(entityName);
            relationNames = Collections.singletonMap(entityName, rels);
            classifierNames = Collections.singletonMap(entityName, classifiers);
        }
    }
}
