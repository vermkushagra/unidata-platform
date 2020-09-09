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

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Required;

public class DuplicateUsePreprocessingDecider implements JobExecutionDecider {

    private boolean usePreprocessing;

    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        if(jobExecution.getStatus() == BatchStatus.FAILED){
            return FlowExecutionStatus.FAILED;
        }

        if (usePreprocessing) {
            return FlowExecutionStatus.COMPLETED;
        } else {
            return FlowExecutionStatus.UNKNOWN;
        }
    }

    @Required
    public void setUsePreprocessing(boolean usePreprocessing) {
        this.usePreprocessing = usePreprocessing;
    }
}