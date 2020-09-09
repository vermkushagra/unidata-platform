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

package com.unidata.mdm.backend.service.job.reindexMeta;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;

public class ReindexMetaMappingListener implements JobExecutionListener {

    private static final String CREATE_INDEX_LOCK_NAME = "createIndexLock";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexMetaMappingListener.class);

    @Autowired
    private SearchServiceExt searchServiceExt;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    private boolean recreateAudit;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        final String storageId = SecurityUtils.getCurrentUserStorageId();
        final ILock createIndexLock = hazelcastInstance.getLock(CREATE_INDEX_LOCK_NAME);

        try {
            if (createIndexLock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    boolean isModelIndexExist = searchServiceExt.modelIndexExists(storageId);
                    if (!isModelIndexExist) {
                        searchServiceExt.createModelIndex(storageId);
                    }
                    if (!searchServiceExt.classifierIndexExist(storageId)) {
                        LOGGER.info("createClassifierIndex [ReindexMetaMappingListener]");
                        searchServiceExt.createClassifierIndex(storageId);
                    }

                    searchServiceExt.createAuditIndex(null, recreateAudit);
                }
                finally {
                    createIndexLock.unlock();
                }
            } else {
                LOGGER.error("Error getting lock for creating indexes");
                throw new SystemRuntimeException("Error getting lock for creating indexes", ExceptionId.EX_ERROR_WHILE_CREATING_INDEXES_LOCK_TIME_OUT);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted while creating indexes", e);
            throw new SystemRuntimeException("Error getting lock for creating indexes", ExceptionId.EX_ERROR_WHILE_CREATING_INDEXES_INTERRUPTED);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

    }

    @Required
    public void setRecreateAudit(boolean recreateAudit) {
        this.recreateAudit = recreateAudit;
    }
}
