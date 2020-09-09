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

package com.unidata.mdm.backend.service.bulk;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;
import com.unidata.mdm.backend.dto.bulk.RemoveRecordsInformationDTO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.service.job.JobServiceExt;

/**
 * Remove records bulk operation coordinator.
 */
public class RemoveRecordsBulkOperation extends AbstractBulkOperation {

    /**
     * Job service
     */
    @Autowired(required = false)
    private JobServiceExt jobServiceExt;

    @Override
    public boolean run(BulkOperationRequestContext ctx) {
        RemoveRecordsConfiguration configuration = (RemoveRecordsConfiguration) ctx.getConfiguration();

        List<String> etalonIds = getEtalonIds(ctx);
        String idsKey = jobServiceExt.putComplexParameter(etalonIds);
        JobParameterDTO ids = new JobParameterDTO("idsKey", idsKey);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String storageKeyForUser = jobServiceExt.putComplexParameter(authentication);
        JobParameterDTO user = new JobParameterDTO("user", storageKeyForUser);
        JobParameterDTO wipe = new JobParameterDTO("wipe", configuration.isWipeRecords());

        JobParameterDTO entity = new JobParameterDTO("entityName", ctx.getEntityName());

        JobDTO job = new JobDTO();
        job.setDescription("Logical removing records");
        job.setName("Remove job");
        job.setEnabled(true);
        job.setJobNameReference("removeJob");
        job.setParameters(asList(entity, ids, user, wipe));
        JobExecution execution = jobServiceExt.startSystemJob(job);
        return execution != null;
    }

    @Override
    public BulkOperationInformationDTO configure() {
        return new RemoveRecordsInformationDTO();
    }
}
