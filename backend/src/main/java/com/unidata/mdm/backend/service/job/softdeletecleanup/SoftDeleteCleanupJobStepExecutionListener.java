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

package com.unidata.mdm.backend.service.job.softdeletecleanup;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.unidata.mdm.backend.service.job.common.AbstractJobStepExecutionListener;
import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Dmitrii Kopin
 *         Modify item step execution listener.
 */
@StepScope
public class SoftDeleteCleanupJobStepExecutionListener extends AbstractJobStepExecutionListener {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SoftDeleteCleanupJobStepExecutionListener.class);
    /**
     * HZ innstance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        if (ImportDataJobUtils.getStepState() == null) {
            SoftDeleteCleanupStepExecutionState parameters = new SoftDeleteCleanupStepExecutionState();
            ImportDataJobUtils.setStepState(parameters);
        }

        super.authenticateIfNeeded();
        super.beforeStep(stepExecution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        super.afterStep(stepExecution);
        // 1. Collect counters
        SoftDeleteCleanupStepExecutionState sp = ImportDataJobUtils.removeStepState();
        if (sp.getProcessedRecords() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, SoftDeleteCleanupJobConstants.MODIFY_ITEM_JOB_RECORDS_COUNTER));
            counter.addAndGet(sp.getProcessedRecords());
        }

        if (sp.getDeleteRecords() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, SoftDeleteCleanupJobConstants.MODIFY_ITEM_JOB_RECORDS_DELETE_COUNTER));
            counter.addAndGet(sp.getDeleteRecords());
        }

        if (sp.getFailedRecords() > 0) {
            IAtomicLong counter = hazelcastInstance.getAtomicLong(
                    ImportDataJobUtils.getObjectReferenceName(runId, SoftDeleteCleanupJobConstants.MODIFY_ITEM_JOB_RECORDS_FAILED_COUNTER));
            counter.addAndGet(sp.getFailedRecords());
        }

        super.clearAuthentication();
        return stepExecution.getExitStatus();
    }

}
