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

/**
 *
 */

package com.unidata.mdm.backend.service.job.softdeletecleanup;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.job.JobException;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class SoftDeleteCleanupItemReader implements ItemReader<List<DeleteRequestContext>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoftDeleteCleanupItemReader.class);

    private String resourceName;

    @Value("#{stepExecutionContext[ids]}")
    private List<String> ids;

    private boolean complete = false;

    @Value("#{jobParameters[" + JobCommonParameters.PARAM_OPERATION_ID + "]}")
    private String operationId;

    @Value("#{jobParameters[" + JobCommonParameters.PARAM_AUDIT_LEVEL + "]}")
    private Long auditLevel;

    @Autowired
    private CommonRecordsComponent commonServiceComponent;

    @Override
    public List<DeleteRequestContext> read() throws Exception {
        if (complete) {
            LOGGER.info("No data available");
            return null;
        }

        LOGGER.info("Read data [resourceName={}, startId={}, endId={}]", resourceName, ids.get(0), ids.get(ids.size() - 1));

        List<DeleteRequestContext> result = new ArrayList<>();

        for (final String id : ids) {
            try {
                RecordKeys keys = commonServiceComponent.identify(EtalonKey.builder().id(id).build());
                if (keys == null) {
                    continue;
                }

                DeleteRequestContext ctx = new DeleteRequestContext.DeleteRequestContextBuilder()
                        .entityName(keys.getEntityName())
                        .wipe(true)
                        .cascade(true)
                        .etalonKey(id)
                        .workflowAction(true)

                        .auditLevel(auditLevel != null ? auditLevel.shortValue() : AuditLevel.AUDIT_SUCCESS)
                        .build();

                ctx.setOperationId(operationId);

                ctx.putToStorage(StorageId.DATA_UPSERT_KEYS, keys);

                result.add(ctx);
            } catch (final Exception exc) {
                LOGGER.warn("Caught exception {}", exc);
                throw new JobException(exc);
            }
        }


        complete = true;
        return result;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public void setAuditLevel(Long auditLevel) {
        this.auditLevel = auditLevel;
    }
}
