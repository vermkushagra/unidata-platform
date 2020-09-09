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

package com.unidata.mdm.backend.service.job.removerelations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class RemoveRelationsItemProcessor implements ItemProcessor<String, List<DeleteRelationRequestContext>> {

    /**
     * Entity name
     */
    private String entityName;

    /**
     * operation id
     */
    private String operationId;

    /**
     * for date
     */
    private Timestamp forDate;

    /**
     * operation executor
     */
    private String operationExecutor;

    private final List<String> relationsNames = new ArrayList<>();

    private RelationsServiceComponent relationsServiceComponent;

    @Autowired
    public void setRelationsServiceComponent(RelationsServiceComponent relationsServiceComponent) {
        this.relationsServiceComponent = relationsServiceComponent;
    }

    @Override
    public List<DeleteRelationRequestContext> process(final String etalonId) {
        final List<GetRelationDTO> relationDTOS = relationsServiceComponent.loadRelationsToEtalon(
                GetRelationsRequestContext.builder()
                        .etalonKey(etalonId)
                        .relationNames(relationsNames)
                        .forOperationId(operationId)
                        .forDate(Date.from(forDate.toInstant()))
                        .build()
        );

        return relationDTOS.stream()
                .map(GetRelationDTO::getRelationKeys)
                .map(k -> {
                    final DeleteRelationRequestContext deleteRelationRequestContext = DeleteRelationRequestContext.builder()
                            .relationEtalonKey(k.getEtalonId())
                            .relationName(k.getRelationName())
                            .entityName(entityName)
                            .wipe(true)
                            .auditLevel(AuditLevel.AUDIT_SUCCESS)
                            .build();
                    deleteRelationRequestContext.setOperationId(operationId);
                    return deleteRelationRequestContext;
                }).collect(Collectors.toList());
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Required
    public void setOperationId(final String operationId) {
        this.operationId = operationId;
    }

    @Required
    public void setForDate(Timestamp forDate) {
        this.forDate = forDate;
    }

    public String getOperationExecutor() {
        return operationExecutor;
    }

    public void setOperationExecutor(final String operationExecutor) {
        this.operationExecutor = operationExecutor;
    }

    public void setRelationsNames(final Collection<String> relationsNames) {
        this.relationsNames.clear();
        if (CollectionUtils.isNotEmpty(relationsNames)) {
            this.relationsNames.addAll(relationsNames);
        }
    }
}
