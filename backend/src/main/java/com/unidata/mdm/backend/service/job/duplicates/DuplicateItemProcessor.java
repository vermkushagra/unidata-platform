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

package com.unidata.mdm.backend.service.job.duplicates;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import com.unidata.mdm.backend.common.audit.AuditLevel;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Required;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.GetRequestContext.GetRequestContextBuilder;

/**
 * This processor avoid problem of hash conflict and transform input data to merge ctx
 */
@Component("duplicateProcessor")
@Scope(value = "step")
public class DuplicateItemProcessor implements ItemProcessor<Collection<String>, MergeRequestContext> {

    private String operationId;

    private String entityName;

    private Long auditLevel;

    private boolean skipNotifications;

    private boolean upRecordsToContext;

    private boolean dirtyMode;

    private Integer shardNumber;

    private boolean mergeRelations;

    private boolean mergeClassifiers;

    private boolean usePreprocessing;

    @Override
    public MergeRequestContext process(Collection<String> clusterIds) throws Exception {
        if (clusterIds.size() < 2) {
            return null;
        }

        List<RecordIdentityContext> filteredDuplicates = clusterIds.stream()
                .map(id -> new GetRequestContextBuilder().etalonKey(id).build())
                .collect(Collectors.toList());
        MergeRequestContext context = new MergeRequestContext
                .MergeRequestContextBuilder()
                .duplicates(filteredDuplicates)
                .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                .entityName(entityName)
                .batchUpsert(true)
                .upRecordsToContext(upRecordsToContext)
                .shardNumber(shardNumber)
                .dirtyMode(dirtyMode)
                .skipRelations(!mergeRelations)
                .skipClassifiers(!mergeClassifiers)
                .clearPreprocessing(!dirtyMode || usePreprocessing)
                .build();
        if (skipNotifications) {
            context.skipNotification();
        }
        context.setOperationId(operationId);
        return context;
    }

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setAuditLevel(Long auditLevel) {
        this.auditLevel = auditLevel;
    }

    @Required
    public void setSkipNotifications(boolean skipNotifications) {
        this.skipNotifications = skipNotifications;
    }

    @Required
    public void setUpRecordsToContext(boolean upRecordsToContext) {
        this.upRecordsToContext = upRecordsToContext;
    }

    @Required
    public void setDirtyMode(boolean dirtyMode) {
        this.dirtyMode = dirtyMode;
    }

    @Required
    public void setShardNumber(Integer shardNumber) {
        this.shardNumber = shardNumber;
    }

    @Required
    public void setMergeRelations(boolean mergeRelations) {
        this.mergeRelations = mergeRelations;
    }

    @Required
    public void setMergeClassifiers(boolean mergeClassifiers) {
        this.mergeClassifiers = mergeClassifiers;
    }

    @Required
    public void setUsePreprocessing(boolean usePreprocessing) {
        this.usePreprocessing = usePreprocessing;
    }
}
